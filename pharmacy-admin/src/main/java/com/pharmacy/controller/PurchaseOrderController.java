package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.entity.Supplier;
import com.pharmacy.entity.User;
import com.pharmacy.exception.BusinessException;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
import com.pharmacy.service.StockService;
import com.pharmacy.service.SupplierService;
import com.pharmacy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseItemService purchaseItemService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;

    @PostMapping("/create")
    public Result<PurchaseOrder> create(@RequestBody PurchaseOrderCreateDTO dto) {
        PurchaseOrder order = purchaseOrderService.createPurchaseOrder(dto);
        return Result.success(order);
    }

    @PostMapping
    public Result<PurchaseOrder> add(@RequestBody PurchaseOrder purchaseOrder) {
        boolean success = purchaseOrderService.save(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        rollbackPurchaseStock(id);
        boolean success = purchaseOrderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            rollbackPurchaseStock(id);
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
        fillDisplayNames(purchaseOrder);
        return purchaseOrder != null ? Result.success(purchaseOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<PurchaseOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrder> resultPage = purchaseOrderService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    private void rollbackPurchaseStock(Long purchaseId) {
        LambdaQueryWrapper<PurchaseItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        List<PurchaseItem> items = purchaseItemService.list(itemWrapper);

        for (PurchaseItem item : items) {
            LambdaQueryWrapper<Stock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(Stock::getMedId, item.getMedId())
                    .eq(Stock::getBatchNo, item.getBatchNo())
                    .eq(Stock::getStatus, 1)
                    .last("LIMIT 1");
            Stock stock = stockService.getOne(stockWrapper, false);
            int purchaseNum = item.getPurchaseNum() == null ? 0 : item.getPurchaseNum();
            if (stock == null) {
                throw new BusinessException("采购单对应库存不存在，不能直接删除");
            }
            int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
            if (stockNum < purchaseNum) {
                throw new BusinessException("采购单已有库存被销售或调整，不能直接删除");
            }
            stock.setStockNum(stockNum - purchaseNum);
            stockService.updateById(stock);
        }

        purchaseItemService.remove(itemWrapper);
    }

    private void fillDisplayNames(PurchaseOrder order) {
        if (order == null) {
            return;
        }
        if (order.getSupplierId() != null) {
            Supplier supplier = supplierService.getById(order.getSupplierId());
            if (supplier != null) {
                order.setSupplierName(supplier.getSupplierName());
            }
        }
        if (order.getUserId() != null) {
            User user = userService.getById(order.getUserId());
            if (user != null) {
                order.setUserRealName(user.getRealName());
            }
        }
    }
}
