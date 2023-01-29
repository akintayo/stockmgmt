package com.rbc.stockticker.service;

import com.rbc.stockticker.exception.InvalidFieldException;
import com.rbc.stockticker.model.LocalStockCache;
import com.rbc.stockticker.model.Stock;
import com.rbc.stockticker.model.StockRequestDTO;
import com.rbc.stockticker.model.StockResponseDTO;
import com.rbc.stockticker.repository.StockRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockManagementServiceImpl implements StockManagementService {
    private static final String[] HEADERS = { "quarter", "stock", "date", "open", "high", "low", "close",
            "volume", "percent_change_price", "percent_change_volume_over_last_wk",
            "previous_weeks_volume", "next_weeks_open", "next_weeks_close", "percent_change_next_weeks_price",
            "days_to_next_dividend", "percent_return_next_dividend"};

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Resource
    private StockValidatorUtil stockValidatorUtil;

    @Resource
    private StockRepository stockRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Stock addStock(Stock stock) {
        stockValidatorUtil.checkDuplicate(new LocalStockCache(stock.getTicker(), stock.getDate()));
        stockRepository.save(stock);
        stockValidatorUtil.updateCache(stock);
        return stock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> retrieveStocksByTicker(String ticker) {
        return stockRepository.findStocksByTickerIgnoreCaseOrderByDate(ticker);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int processBulkStockDataset(InputStream inputStream) throws IOException {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        CSVParser parser = format.parse(fileReader);

        if (parser.getHeaderNames().size() != HEADERS.length) {
            throw new InvalidFieldException("File header format not compatible");
        }
        Iterable<CSVRecord> csvRecords = parser.getRecords();
        List<Stock> newStocks = new ArrayList<>(parser.getRecords().size());
        for (CSVRecord record : csvRecords) {
            StockRequestDTO stockRequestDTO = new StockResponseDTO();
            stockRequestDTO.setQuarter(Integer.parseInt(checkForNull(record.get("quarter"))));
            stockRequestDTO.setTicker(checkForNull(record.get("stock")));
            stockRequestDTO.setDate(convertDate(checkForNull(record.get("date"))));
            stockRequestDTO.setOpenPrice(new BigDecimal(checkForNull(record.get("open"))));
            stockRequestDTO.setHighPrice(new BigDecimal(checkForNull(record.get("high"))));
            stockRequestDTO.setLowPrice(new BigDecimal(checkForNull(record.get("low"))));
            stockRequestDTO.setClosePrice(new BigDecimal(checkForNull(record.get("close"))));
            stockRequestDTO.setCurrentTransactionVolume(Long.valueOf(checkForNull(record.get("volume"))));
            stockRequestDTO.setPercentChangePrice(Float.parseFloat(checkForNull(record.get("percent_change_price"))));
            if (StringUtils.isNotEmpty(record.get("percent_change_volume_over_last_wk") )) {
                stockRequestDTO.setPercentVolumeChange(Float.parseFloat(record.get("percent_change_volume_over_last_wk")));
            }
            if (StringUtils.isNotEmpty(record.get("previous_weeks_volume") )) {
                stockRequestDTO.setPreviousWeekVolume(Long.valueOf(record.get("previous_weeks_volume")));
            }
            stockRequestDTO.setNextWeekOpenPrice(new BigDecimal(checkForNull(record.get("next_weeks_open"))));
            stockRequestDTO.setNextWeekClosePrice(new BigDecimal(checkForNull(record.get("next_weeks_close"))));
            stockRequestDTO.setNextWeekPercentPriceChange(Float.parseFloat(checkForNull(record.get("percent_change_price"))));
            stockRequestDTO.setDaysToNextDividend(Integer.parseInt(checkForNull(record.get("days_to_next_dividend"))));
            stockRequestDTO.setNextDividendReturn(Float.parseFloat(checkForNull(record.get("percent_return_next_dividend"))));
            Stock stock = stockValidatorUtil.validateStock(stockRequestDTO);
            newStocks.add(stock);
        }
        log.info("Persisting {} uploaded datasets", newStocks.size());
        stockRepository.saveAll(newStocks);
        stockValidatorUtil.updateCache(newStocks);
        return newStocks.size();
    }

    private LocalDate convertDate(String date) {
        return LocalDate.parse(date, DATE_TIME_FORMATTER);
    }

    private String checkForNull(String value) {
        if (StringUtils.isBlank(value)) {
            throw new InvalidFieldException("Field must have a value");
        }
        return value.replaceAll("\\$", "");
    }
}
