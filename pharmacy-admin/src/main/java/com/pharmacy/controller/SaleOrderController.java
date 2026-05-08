package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.entity.User;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import com.pharmacy.service.StockService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sale-order")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;

    @PostMapping("/create")
    public Result<SaleOrder> create(@RequestBody SaleOrderCreateDTO dto) {
        SaleOrder order = saleOrderService.createSaleOrder(dto);
        return Result.success(order);
    }

    @PostMapping
    public Result<SaleOrder> add(@RequestBody SaleOrder saleOrder) {
        boolean success = saleOrderService.save(saleOrder);
        return success ? Result.success(saleOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        restoreSaleOrderEffects(id);
        boolean success = saleOrderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            restoreSaleOrderEffects(id);
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
        fillDisplayNames(saleOrder);
        return saleOrder != null ? Result.success(saleOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<SaleOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SaleOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SaleOrder::getCreateTime);
        Page<SaleOrder> resultPage = saleOrderService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    private void restoreSaleOrderEffects(Long orderId) {
        SaleOrder order = saleOrderService.getById(orderId);
        LambdaQueryWrapper<SaleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SaleItem::getOrderId, orderId);
        List<SaleItem> items = saleItemService.list(itemWrapper);

        for (SaleItem item : items) {
            LambdaQueryWrapper<Stock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(Stock::getMedId, item.getMedId())
                    .eq(Stock::getBatchNo, item.getBatchNo())
                    .eq(Stock::getStatus, 1)
                    .last("LIMIT 1");
            Stock stock = stockService.getOne(stockWrapper, false);
            if (stock != null) {
                int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
                int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
                stock.setStockNum(stockNum + quantity);
                stockService.updateById(stock);
            }
        }

        if (order != null && order.getCustId() != null && order.getTotalPrice() != null) {
            Customer customer = customerService.getById(order.getCustId());
            if (customer != null && customer.getTotalConsume() != null) {
                BigDecimal totalConsume = customer.getTotalConsume().subtract(order.getTotalPrice());
                customer.setTotalConsume(totalConsume.max(BigDecimal.ZERO));
                customerService.updateById(customer);
            }
        }

        saleItemService.remove(itemWrapper);
    }

    private void fillDisplayNames(SaleOrder order) {
        if (order == null) {
            return;
        }
        if (order.getCustId() != null) {
            Customer customer = customerService.getById(order.getCustId());
            if (customer != null) {
                order.setCustName(customer.getCustName());
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
