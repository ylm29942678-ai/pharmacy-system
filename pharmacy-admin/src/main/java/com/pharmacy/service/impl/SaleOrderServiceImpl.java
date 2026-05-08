package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.SaleItemDTO;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.exception.BusinessException;
import com.pharmacy.mapper.SaleOrderMapper;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import com.pharmacy.service.StockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MedicineService medicineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createSaleOrder(SaleOrderCreateDTO dto) {
        SaleOrder saleOrder = new SaleOrder();
        // 只复制需要的字段，不复制orderId让它自增
        saleOrder.setCustId(dto.getCustId());
        saleOrder.setUserId(dto.getUserId());
        saleOrder.setTotalPrice(dto.getTotalPrice());
        saleOrder.setPayType(dto.getPayType());
        saleOrder.setOrderType(dto.getOrderType());
        saleOrder.setPayStatus(dto.getPayStatus());
        saleOrder.setRemark(dto.getRemark());
        saleOrder.setCreateTime(LocalDateTime.now());
        saleOrder.setUpdateTime(LocalDateTime.now());
        
        save(saleOrder);
        
        Long orderId = saleOrder.getOrderId();
        List<SaleItem> items = new ArrayList<>();
        for (SaleItemDTO itemDTO : dto.getItems()) {
            items.addAll(deductStockAndBuildItems(itemDTO, orderId));
        }
        saleItemService.saveBatch(items);
        
        if (dto.getCustId() != null) {
            Customer customer = customerService.getById(dto.getCustId());
            if (customer != null) {
                customer.setTotalConsume(customer.getTotalConsume().add(dto.getTotalPrice()));
                customerService.updateById(customer);
            }
        }
        
        return saleOrder;
    }

    private List<SaleItem> deductStockAndBuildItems(SaleItemDTO itemDTO, Long orderId) {
        if (itemDTO.getMedId() == null) {
            throw new BusinessException("药品不能为空");
        }
        if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
            throw new BusinessException("销售数量必须大于0");
        }

        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getMedId, itemDTO.getMedId())
                .eq(Stock::getStatus, 1);
        if (itemDTO.getBatchNo() != null && !itemDTO.getBatchNo().trim().isEmpty()) {
            wrapper.eq(Stock::getBatchNo, itemDTO.getBatchNo().trim());
        }
        wrapper.orderByAsc(Stock::getCreateTime).orderByAsc(Stock::getStockId);

        List<Stock> stocks = stockService.list(wrapper);
        String medicineName = getMedicineName(itemDTO.getMedId());
        int available = stocks.stream()
                .map(Stock::getStockNum)
                .filter(num -> num != null)
                .mapToInt(Integer::intValue)
                .sum();

        if (available < itemDTO.getQuantity()) {
            String batchText = itemDTO.getBatchNo() == null || itemDTO.getBatchNo().trim().isEmpty()
                    ? ""
                    : "，批号：" + itemDTO.getBatchNo().trim();
            throw new BusinessException("库存不足：" + medicineName + batchText + "，当前库存 " + available + "，本次销售 " + itemDTO.getQuantity());
        }

        List<SaleItem> saleItems = new ArrayList<>();
        int remaining = itemDTO.getQuantity();
        for (Stock stock : stocks) {
            if (remaining <= 0) {
                break;
            }
            int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
            int deductNum = Math.min(stockNum, remaining);
            if (deductNum <= 0) {
                continue;
            }

            stock.setStockNum(stockNum - deductNum);
            stockService.updateById(stock);

            SaleItem item = new SaleItem();
            item.setOrderId(orderId);
            item.setMedId(itemDTO.getMedId());
            item.setBatchNo(stock.getBatchNo());
            item.setQuantity(deductNum);
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotalPrice(calculateTotalPrice(itemDTO.getUnitPrice(), deductNum));
            item.setCreateTime(LocalDateTime.now());
            saleItems.add(item);

            remaining -= deductNum;
        }
        return saleItems;
    }

    private BigDecimal calculateTotalPrice(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private String getMedicineName(Integer medId) {
        Medicine medicine = medicineService.getById(medId);
        if (medicine == null || medicine.getMedName() == null || medicine.getMedName().trim().isEmpty()) {
            return "药品ID " + medId;
        }
        return medicine.getMedName();
    }
}
