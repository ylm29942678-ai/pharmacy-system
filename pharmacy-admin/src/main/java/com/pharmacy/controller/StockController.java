package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Stock;
import com.pharmacy.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public Result<Stock> add(@RequestBody Stock stock) {
        boolean success = stockService.save(stock);
        return success ? Result.success(stock) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = stockService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Stock> update(@RequestBody Stock stock) {
        boolean success = stockService.updateById(stock);
        return success ? Result.success(stock) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Stock> getById(@PathVariable Integer id) {
        Stock stock = stockService.getById(id);
        return stock != null ? Result.success(stock) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Stock>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Stock> page = new Page<>(current, size);
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Stock::getCreateTime);
        return Result.success(stockService.page(page, wrapper));
    }
}
