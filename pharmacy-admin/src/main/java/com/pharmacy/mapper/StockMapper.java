package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.entity.Stock;
import com.pharmacy.vo.StockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
    
    @Select("SELECT s.*, m.med_name, m.spec, m.unit, m.stock_min " +
            "FROM stock s LEFT JOIN medicine m ON s.med_id = m.med_id " +
            "ORDER BY s.create_time DESC")
    IPage<StockVO> selectStockPageWithMedicine(Page<StockVO> page);
}
