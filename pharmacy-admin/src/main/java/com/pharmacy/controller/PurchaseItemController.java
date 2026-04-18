package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.service.PurchaseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-item")
public class PurchaseItemController {

    @Autowired
    private PurchaseItemService purchaseItemService;

    @PostMapping
    public Result<PurchaseItem> add(@RequestBody PurchaseItem purchaseItem) {
        boolean success = purchaseItemService.save(purchaseItem);
        return success ? Result.success(purchaseItem) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = purchaseItemService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<PurchaseItem> update(@RequestBody PurchaseItem purchaseItem) {
        boolean success = purchaseItemService.updateById(purchaseItem);
        return success ? Result.success(purchaseItem) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<PurchaseItem> getById(@PathVariable Long id) {
        PurchaseItem purchaseItem = purchaseItemService.getById(id);
        return purchaseItem != null ? Result.success(purchaseItem) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<PurchaseItem>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PurchaseItem> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PurchaseItem::getCreateTime);
        return Result.success(purchaseItemService.page(page, wrapper));
    }
}
