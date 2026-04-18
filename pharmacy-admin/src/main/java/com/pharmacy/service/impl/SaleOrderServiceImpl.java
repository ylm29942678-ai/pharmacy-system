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
        SaleOrder saleOrder = new SaleOrder();
        BeanUtils.copyProperties(dto, saleOrder);
        saleOrder.setCreateTime(LocalDateTime.now());
        saleOrder.setUpdateTime(LocalDateTime.now());
        
        save(saleOrder);
        
        Long orderId = saleOrder.getOrderId();
        List<SaleItem> items = new ArrayList<>();
        for (SaleItemDTO itemDTO : dto.getItems()) {
            SaleItem item = new SaleItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setOrderId(orderId);
            item.setCreateTime(LocalDateTime.now());
            items.add(item);
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
}
