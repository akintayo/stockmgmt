package com.rbc.stockticker.repository;

import com.rbc.stockticker.model.Stock;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StockRepository extends CrudRepository<Stock, Long> {

    List<Stock> findStocksByTickerOrderByDate(String ticker);
}
