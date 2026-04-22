package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Medicine;
import com.pharmacy.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @PostMapping
    public Result<Medicine> add(@RequestBody Medicine medicine) {
        boolean success = medicineService.save(medicine);
        return success ? Result.success(medicine) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = medicineService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Medicine> update(@RequestBody Medicine medicine) {
        boolean success = medicineService.updateById(medicine);
        return success ? Result.success(medicine) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Medicine> getById(@PathVariable Integer id) {
        Medicine medicine = medicineService.getById(id);
        return medicine != null ? Result.success(medicine) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Medicine>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String medName,
            @RequestParam(required = false) String medType,
            @RequestParam(required = false) String status) {
        Page<Medicine> page = new Page<>(current, size);
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        if (medName != null && !medName.isEmpty()) {
            wrapper.like(Medicine::getMedName, medName);
        }
        if (medType != null && !medType.isEmpty()) {
            wrapper.eq(Medicine::getMedType, medType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Medicine::getStatus, Integer.parseInt(status));
        } else {
            wrapper.eq(Medicine::getStatus, 1);
        }
        wrapper.orderByAsc(Medicine::getMedId);
        return Result.success(medicineService.page(page, wrapper));
    }
}
