package com.rbc.stockticker.exception;

public class ObjectNotFoundException extends StockException {

    public ObjectNotFoundException() {
        super();
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
