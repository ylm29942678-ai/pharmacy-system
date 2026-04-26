package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.StockMapper;
import com.pharmacy.service.StockService;
import com.pharmacy.vo.StockVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
    @Override
    public IPage<StockVO> getStockPageWithMedicine(Page<StockVO> page) {
        IPage<StockVO> stockPage = baseMapper.selectStockPageWithMedicine(page);
        // 计算近效期和低库存状态
        LocalDate today = LocalDate.now();
        for (StockVO vo : stockPage.getRecords()) {
            // 判断是否近效期（30天内）
            if (vo.getExpireDate() != null) {
                long daysUntilExpire = java.time.temporal.ChronoUnit.DAYS.between(today, vo.getExpireDate());
                vo.setIsNearExpire(daysUntilExpire <= 30 && daysUntilExpire > 0);
            } else {
                vo.setIsNearExpire(false);
            }
            // 判断是否低库存
            if (vo.getStockMin() != null && vo.getStockNum() != null) {
                vo.setIsLowStock(vo.getStockNum() <= vo.getStockMin());
            } else {
                vo.setIsLowStock(false);
            }
        }
        return stockPage;
    }
}
