package com.pharmacy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Medicine;
import com.pharmacy.mapper.MedicineMapper;
import com.pharmacy.service.MedicineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineMapper medicineMapper;

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
        boolean success = medicineMapper.updateMedicineById(medicine) > 0;
        return success ? Result.success(medicine) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Medicine> getById(@PathVariable Integer id) {
        Medicine medicine = medicineMapper.selectMedicineById(id);
        return medicine != null ? Result.success(medicine) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Medicine>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String medName,
            @RequestParam(required = false) String medType,
            @RequestParam(required = false) Integer status) {
        log.info("查询药品 - current: {}, size: {}, medName: {}, medType: {}, status: {}", current, size, medName, medType, status);
        Page<Medicine> page = new Page<>(current, size);
        Page<Medicine> result = medicineMapper.selectMedicinePage(page, medName, medType, status);
        log.info("查询结果 - 总记录数: {}, 当前页记录数: {}", result.getTotal(), result.getRecords().size());
        return Result.success(result);
    }
}
