package com.rbc.stockticker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(schema = "stock", uniqueConstraints = { @UniqueConstraint(columnNames = { "ticker", "date" }) })
@Entity
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private int quarter;

    @Column
    private String ticker;

    @Column
    private LocalDate date;

    @Column
    private BigDecimal openPrice;

    @Column
    private BigDecimal closePrice;

    @Column
    private BigDecimal highPrice;

    @Column
    private BigDecimal lowPrice;

    @Column
    private Long currentTransactionVolume;

    private float percentChangePrice;

    private float percentVolumeChange;

    @Column
    private Long previousWeekVolume;

    @Column
    private BigDecimal nextWeekOpenPrice;

    @Column
    private BigDecimal nextWeekClosePrice;

    private float nextWeekPercentPriceChange;

    @Column
    private int daysToNextDividend;

    @Column
    private float nextDividendReturn;

}
