package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/create")
    public Result<PurchaseOrder> create(@RequestBody PurchaseOrderCreateDTO dto) {
        try {
            PurchaseOrder order = purchaseOrderService.createPurchaseOrder(dto);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("创建采购单失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<PurchaseOrder> add(@RequestBody PurchaseOrder purchaseOrder) {
        boolean success = purchaseOrderService.save(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = purchaseOrderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
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
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        return Result.success(purchaseOrderService.page(page, wrapper));
    }
}
