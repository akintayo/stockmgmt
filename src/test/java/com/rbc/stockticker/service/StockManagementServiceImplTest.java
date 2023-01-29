package com.rbc.stockticker.service;

import com.rbc.stockticker.exception.DuplicateObjectException;
import com.rbc.stockticker.exception.InvalidFieldException;
import com.rbc.stockticker.model.Stock;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StockManagementServiceImplTest {

    @Resource
    private StockManagementService stockManagementService;

    @Test
    void addStock() {
        Stock stock = new Stock();
        stock.setTicker("AA");
        stock.setDate(LocalDate.of(2023, 1, 10));
        assertNull(stock.getId());
        //persist
        stockManagementService.addStock(stock);
        assertTrue( stock.getId() > 0);
    }

    @Test
    void addDuplicateStock() {
        // create a stock
        addStock();
        DuplicateObjectException exception = Assertions.assertThrows(DuplicateObjectException.class, this::addStock);
        Assertions.assertEquals("Duplicate record not allowed", exception.getMessage());
    }

    @Test
    void retrieveStocksByTicker() {
        addStock();
        List<Stock> result = stockManagementService.retrieveStocksByTicker("AA");
        assertEquals(1, result.size());
        assertEquals("AA", result.get(0).getTicker());
    }

    @Test
    void retrieveStocksByTickerNotExisting() {
        addStock();
        List<Stock> result = stockManagementService.retrieveStocksByTicker("AAB");
        assertEquals(0, result.size());
    }

    @Test
    void processBulkStockDataset() throws IOException {
        File testFile = new File("src/test/resources/dow_jones_test_index.data");
        InputStream targetStream = FileUtils.openInputStream(testFile);
        int count = stockManagementService.processBulkStockDataset(targetStream);
        assertEquals(11, count);
        //fetch a ticker
        List<Stock> stocks = stockManagementService.retrieveStocksByTicker("WMT");
        assertEquals(4, stocks.size());
    }

    @Test
    void processBulkStockDatasetIncompatibleContent() {
        File testFile = new File("src/test/resources/incompatible-bulkdata.csv");
        InvalidFieldException exception = Assertions.assertThrows(InvalidFieldException.class, () -> {
            InputStream targetStream = FileUtils.openInputStream(testFile);
            stockManagementService.processBulkStockDataset(targetStream);
        });
        Assertions.assertEquals("File header format not compatible", exception.getMessage());

    }

    @Test
    void processBulkStockDatasetWithDuplicates() throws IOException {
        File testFile = new File("src/test/resources/dow_jones_test_duplicate_index.data");

        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            InputStream targetStream = FileUtils.openInputStream(testFile);
            stockManagementService.processBulkStockDataset(targetStream);
        });
        //there should be nothing in the database
        assertEquals(0, stockManagementService.retrieveStocksByTicker("AA").size());
    }
}