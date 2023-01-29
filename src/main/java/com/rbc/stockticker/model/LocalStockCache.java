package com.rbc.stockticker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class LocalStockCache {

    private final String ticker;
    private final LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalStockCache that = (LocalStockCache) o;
        return Objects.equals(ticker, that.ticker) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, date);
    }
}
