package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.StockMapper;
import com.pharmacy.service.StockService;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
}
