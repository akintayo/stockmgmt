package com.rbc.stockticker.service;

import com.rbc.stockticker.exception.DuplicateObjectException;
import com.rbc.stockticker.model.Stock;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * API for stock management
 */
public interface StockManagementService {
    /**
     * Creates a new stock record
     * @param stock the stock to create
     * @return the created stock
     * @throws DuplicateObjectException if a duplicate entry is found
     */
    Stock addStock(Stock stock) throws DuplicateObjectException;

    List<Stock> retrieveStocksByTicker(String ticker);

    /**
     * Process bulk stock data set
     * @param inputStream the stream of data received from the clent
     * @return the count of records inserted into the database
     * @throws IOException if stream cannot be processed
     * @throws DuplicateObjectException if a duplicate entry is found
     */
    int processBulkStockDataset(InputStream inputStream) throws IOException, DuplicateObjectException;
}
