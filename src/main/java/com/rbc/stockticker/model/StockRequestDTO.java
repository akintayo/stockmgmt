package com.rbc.stockticker.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Stock
 */
@Data
public class StockRequestDTO {

    @Min(value = 1, message = "quarter must be a minimum of 1")
    @Max(value = 4, message = "quarter must be maximum 4")
    private int quarter;

    @NotEmpty(message = "ticker cannot be null")
    private String ticker;

    @JsonFormat(pattern = "M/d/yyyy")
    private LocalDate date;

    @PositiveOrZero(message = "openPrice cannot be negative")
    private BigDecimal openPrice;

    @PositiveOrZero(message = "closePrice cannot be negative")
    private BigDecimal closePrice;

    @PositiveOrZero(message = "highPrice cannot be negative")
    private BigDecimal highPrice;

    @PositiveOrZero(message = "lowPrice price cannot be negative")
    private BigDecimal lowPrice;

    @Min(value = 1, message = "must not be zero")
    private Long currentTransactionVolume;

    private Long previousWeekVolume;

    private float percentChangePrice;

    private float percentVolumeChange;

    private float nextWeekPercentPriceChange;

    @PositiveOrZero(message = "price cannot be negative")
    private BigDecimal nextWeekOpenPrice;

    @PositiveOrZero(message = "nextWeekClosePrice cannot be negative")
    private BigDecimal nextWeekClosePrice;

    @PositiveOrZero( message = "daysToNextDividend Cannot be negative")
    private int daysToNextDividend;

    private float nextDividendReturn;
}
