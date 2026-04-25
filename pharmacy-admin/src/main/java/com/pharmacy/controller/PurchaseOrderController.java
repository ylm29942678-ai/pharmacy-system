package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseItemService purchaseItemService;

    @PostMapping("/create")
    public Result<PurchaseOrder> create(@RequestBody PurchaseOrderCreateDTO dto) {
        try {
            PurchaseOrder order = purchaseOrderService.createPurchaseOrder(dto);
            return Result.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建采购单失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<PurchaseOrder> add(@RequestBody PurchaseOrder purchaseOrder) {
        boolean success = purchaseOrderService.save(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        LambdaQueryWrapper<PurchaseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseItem::getPurchaseId, id);
        purchaseItemService.remove(wrapper);

        boolean success = purchaseOrderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<PurchaseItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseItem::getPurchaseId, id);
            purchaseItemService.remove(wrapper);
        }
        boolean success = purchaseOrderService.removeByIds(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }

    @PutMapping
    public Result<PurchaseOrder> update(@RequestBody PurchaseOrder purchaseOrder) {
        boolean success = purchaseOrderService.updateById(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(@PathVariable Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getById(id);
        return purchaseOrder != null ? Result.success(purchaseOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<PurchaseOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PurchaseOrder::getCreateTime);
        return Result.success(purchaseOrderService.page(page, wrapper));
    }
}
