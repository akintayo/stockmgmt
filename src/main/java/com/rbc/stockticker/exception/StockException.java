package com.rbc.stockticker.exception;

import com.rbc.stockticker.model.Stock;
import lombok.Data;

public class StockException extends RuntimeException {

    public StockException () {
        super();
    }
    public StockException(String message) {
        super(message);
    }
}
