package com.rbc.stockticker.exception;

import com.rbc.stockticker.model.Stock;

public class InvalidFieldException extends StockException {

    public InvalidFieldException() {
        super();
    }

    public InvalidFieldException(String message) {
        super(message);
    }

}
