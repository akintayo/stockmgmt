package com.rbc.stockticker.web;

import com.rbc.stockticker.model.Stock;
import com.rbc.stockticker.model.StockRequestDTO;
import com.rbc.stockticker.model.StockResponseDTO;
import com.rbc.stockticker.service.StockManagementService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class StockController {

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    @Resource
    private StockManagementService stockManagementService;

    @PostMapping(value = "/")
    public ResponseEntity<StockResponseDTO> createStock(@Valid @RequestBody StockRequestDTO stockRequestDTO) {

        Stock stock = MODEL_MAPPER.map(stockRequestDTO, Stock.class);
        Stock createdStock = stockManagementService.addStock(stock);
        StockResponseDTO responseDTO = MODEL_MAPPER.map(createdStock, StockResponseDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<Integer> uploadStock(@RequestPart MultipartFile data) throws IOException {
        int processed = stockManagementService.processBulkStockDataset(data.getInputStream());
        return new ResponseEntity<>(processed, HttpStatus.OK);
    }

    @GetMapping(value = "/{ticker}")
    public ResponseEntity<List<StockResponseDTO>> getStockTicker(@PathVariable("ticker") String ticker) {
        List<Stock> stocks = stockManagementService.retrieveStocksByTicker(ticker);
        List<StockResponseDTO> responseDTOS = stocks.stream().map(stock -> MODEL_MAPPER.map(stock, StockResponseDTO.class)).collect(Collectors.toCollection(() -> new ArrayList<>(stocks.size())));
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }
}
