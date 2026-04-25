package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.SaleItemDTO;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.mapper.SaleOrderMapper;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private CustomerService customerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createSaleOrder(SaleOrderCreateDTO dto) {
        System.out.println("开始创建销售订单，DTO数据: " + dto);
        
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
        
        System.out.println("准备保存销售订单: " + saleOrder);
        save(saleOrder);
        System.out.println("销售订单已保存，生成的orderId: " + saleOrder.getOrderId());
        
        Long orderId = saleOrder.getOrderId();
        List<SaleItem> items = new ArrayList<>();
        for (SaleItemDTO itemDTO : dto.getItems()) {
            SaleItem item = new SaleItem();
            item.setMedId(itemDTO.getMedId());
            item.setBatchNo(itemDTO.getBatchNo());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotalPrice(itemDTO.getTotalPrice());
            item.setOrderId(orderId);
            item.setCreateTime(LocalDateTime.now());
            if (item.getBatchNo() == null || item.getBatchNo().trim().isEmpty()) {
                item.setBatchNo("DEFAULT");
            }
            items.add(item);
        }
        System.out.println("准备保存 " + items.size() + " 条销售明细");
        saleItemService.saveBatch(items);
        System.out.println("销售明细已保存");
        
        if (dto.getCustId() != null) {
            Customer customer = customerService.getById(dto.getCustId());
            if (customer != null) {
                customer.setTotalConsume(customer.getTotalConsume().add(dto.getTotalPrice()));
                customerService.updateById(customer);
            }
        }
        
        return saleOrder;
    }
}
