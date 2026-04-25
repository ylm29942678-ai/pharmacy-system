package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-order")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private SaleItemService saleItemService;

    @PostMapping("/create")
    public Result<SaleOrder> create(@RequestBody SaleOrderCreateDTO dto) {
        try {
            SaleOrder order = saleOrderService.createSaleOrder(dto);
            return Result.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建销售单失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<SaleOrder> add(@RequestBody SaleOrder saleOrder) {
        boolean success = saleOrderService.save(saleOrder);
        return success ? Result.success(saleOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        LambdaQueryWrapper<SaleItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaleItem::getOrderId, id);
        saleItemService.remove(wrapper);
        
        boolean success = saleOrderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<SaleItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SaleItem::getOrderId, id);
            saleItemService.remove(wrapper);
        }
        boolean success = saleOrderService.removeByIds(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }

    @PutMapping
    public Result<SaleOrder> update(@RequestBody SaleOrder saleOrder) {
        boolean success = saleOrderService.updateById(saleOrder);
        return success ? Result.success(saleOrder) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<SaleOrder> getById(@PathVariable Long id) {
        SaleOrder saleOrder = saleOrderService.getById(id);
        return saleOrder != null ? Result.success(saleOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<SaleOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SaleOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SaleOrder::getCreateTime);
        return Result.success(saleOrderService.page(page, wrapper));
    }
}
