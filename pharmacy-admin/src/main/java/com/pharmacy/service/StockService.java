package com.pharmacy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pharmacy.entity.Stock;
import com.pharmacy.vo.StockVO;

public interface StockService extends IService<Stock> {
    IPage<StockVO> getStockPageWithMedicine(Page<StockVO> page);
}
