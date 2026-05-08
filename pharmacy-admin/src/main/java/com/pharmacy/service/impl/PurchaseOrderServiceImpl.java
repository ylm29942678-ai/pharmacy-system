package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.PurchaseItemDTO;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.PurchaseOrderMapper;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
import com.pharmacy.service.StockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private PurchaseItemService purchaseItemService;

    @Autowired
    private StockService stockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder createPurchaseOrder(PurchaseOrderCreateDTO dto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        BeanUtils.copyProperties(dto, purchaseOrder);
        purchaseOrder.setPurchaseStatus(0);
        purchaseOrder.setCreateTime(LocalDateTime.now());
        purchaseOrder.setUpdateTime(LocalDateTime.now());
        
        save(purchaseOrder);
        
        Long purchaseId = purchaseOrder.getPurchaseId();
        List<PurchaseItem> items = new ArrayList<>();
        for (PurchaseItemDTO itemDTO : dto.getItems()) {
            PurchaseItem item = new PurchaseItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setPurchaseId(purchaseId);
            item.setCreateTime(LocalDateTime.now());
            if (item.getBatchNo() == null || item.getBatchNo().trim().isEmpty()) {
                item.setBatchNo("DEFAULT");
            }
            items.add(item);
            increaseStock(dto, item);
        }
        purchaseItemService.saveBatch(items);
        
        return purchaseOrder;
    }

    private void increaseStock(PurchaseOrderCreateDTO dto, PurchaseItem item) {
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getMedId, item.getMedId())
                .eq(Stock::getBatchNo, item.getBatchNo())
                .eq(Stock::getStatus, 1)
                .last("LIMIT 1");

        Stock stock = stockService.getOne(wrapper, false);
        if (stock == null) {
            stock = new Stock();
            stock.setMedId(item.getMedId());
            stock.setBatchNo(item.getBatchNo());
            stock.setProductionDate(item.getProductionDate());
            stock.setExpireDate(item.getExpireDate());
            stock.setStockNum(item.getPurchaseNum());
            stock.setPurchasePrice(item.getPurchasePrice());
            stock.setCabinet(item.getCabinet());
            stock.setSupplierId(dto.getSupplierId());
            stock.setStatus(1);
            stock.setRemark(item.getRemark());
            stockService.save(stock);
            return;
        }

        int currentStock = stock.getStockNum() == null ? 0 : stock.getStockNum();
        int purchaseNum = item.getPurchaseNum() == null ? 0 : item.getPurchaseNum();
        stock.setStockNum(currentStock + purchaseNum);
        stock.setPurchasePrice(item.getPurchasePrice());
        stock.setCabinet(item.getCabinet());
        stock.setProductionDate(item.getProductionDate());
        stock.setExpireDate(item.getExpireDate());
        stock.setSupplierId(dto.getSupplierId());
        stockService.updateById(stock);
    }
}
