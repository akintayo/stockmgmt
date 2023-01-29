package com.rbc.stockticker.service;

import com.rbc.stockticker.exception.DuplicateObjectException;
import com.rbc.stockticker.model.LocalStockCache;
import com.rbc.stockticker.model.Stock;
import com.rbc.stockticker.model.StockRequestDTO;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Validated
public class StockValidatorUtil {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    private Set<LocalStockCache> localStockCache = new HashSet<>();

    /**
     * Validates the deserialized row and validates
     * @param stockRequestDTO object to validate
     * @return the {@link Stock} entity
     */
    Stock validateStock(@Valid StockRequestDTO stockRequestDTO) {
        checkDuplicate(new LocalStockCache(stockRequestDTO.getTicker(), stockRequestDTO.getDate()));
        return MODEL_MAPPER.map(stockRequestDTO, Stock.class);
    }

    /**
     * Check for duplicate
     * @param stock to check in the cache for duplicate
     */
    void checkDuplicate(LocalStockCache stock) {
        if (localStockCache.contains(stock)) {
            throw new DuplicateObjectException("Duplicate record not allowed");
        }
    }

    void updateCache(List<Stock> stockList) {
        stockList.forEach(stock -> localStockCache.add(new LocalStockCache(stock.getTicker(), stock.getDate())));
    }

    void updateCache(Stock stock) {
        localStockCache.add(new LocalStockCache(stock.getTicker(), stock.getDate()));
    }
}
