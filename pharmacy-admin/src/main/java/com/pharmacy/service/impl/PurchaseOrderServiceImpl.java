package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.PurchaseItemDTO;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.mapper.PurchaseOrderMapper;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
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
            items.add(item);
        }
        purchaseItemService.saveBatch(items);
        
        return purchaseOrder;
    }
}
