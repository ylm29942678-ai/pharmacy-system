package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.annotation.OperateLog;
import com.pharmacy.common.Result;
import com.pharmacy.dto.StockCheckCreateDTO;
import com.pharmacy.entity.StockCheck;
import com.pharmacy.service.StockCheckService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/stock-check")
public class StockCheckController {

    @Autowired
    private StockCheckService stockCheckService;

    @PostMapping
    @OperateLog(module = "库存管理", type = "新增", content = "新增盘点记录")
    public Result<StockCheck> add(@RequestBody StockCheck stockCheck) {
        boolean success = stockCheckService.save(stockCheck);
        return success ? Result.success(stockCheck) : Result.error("添加失败");
    }

    @PostMapping("/create")
    @OperateLog(module = "库存管理", type = "新增", content = "创建盘点单")
    @Transactional(rollbackFor = Exception.class)
    public Result<List<StockCheck>> create(@RequestBody StockCheckCreateDTO dto) {
        List<StockCheck> stockCheckList = new ArrayList<>();
        for (StockCheckCreateDTO.StockCheckItemDTO item : dto.getItems()) {
            StockCheck stockCheck = new StockCheck();
            BeanUtils.copyProperties(item, stockCheck);
            stockCheck.setCheckNo(dto.getCheckNo());
            stockCheck.setCheckUser(dto.getCheckUser());
            stockCheck.setCheckTime(dto.getCheckTime());
            stockCheck.setRemark(dto.getRemark());
            
            Integer profitLoss = item.getActualStock() - item.getSystemStock();
            stockCheck.setProfitLoss(profitLoss);
            
            if (item.getUnitPrice() != null) {
                BigDecimal profitLossAmount = item.getUnitPrice().multiply(new BigDecimal(profitLoss));
                stockCheck.setProfitLossAmount(profitLossAmount);
            }
            
            stockCheckList.add(stockCheck);
        }
        boolean success = stockCheckService.saveBatch(stockCheckList);
        return success ? Result.success(stockCheckList) : Result.error("创建失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = stockCheckService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<StockCheck> update(@RequestBody StockCheck stockCheck) {
        boolean success = stockCheckService.updateById(stockCheck);
        return success ? Result.success(stockCheck) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<StockCheck> getById(@PathVariable Long id) {
        StockCheck stockCheck = stockCheckService.getById(id);
        return stockCheck != null ? Result.success(stockCheck) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<StockCheck>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String checkNo) {
        Page<StockCheck> page = new Page<>(current, size);
        LambdaQueryWrapper<StockCheck> wrapper = new LambdaQueryWrapper<>();
        if (checkNo != null && !checkNo.isEmpty()) {
            wrapper.eq(StockCheck::getCheckNo, checkNo);
        }
        wrapper.orderByDesc(StockCheck::getCreateTime);
        return Result.success(stockCheckService.page(page, wrapper));
    }
}
