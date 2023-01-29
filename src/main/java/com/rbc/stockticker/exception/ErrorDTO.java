package com.rbc.stockticker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

/**
 * DTO for error to be sent to client
 */
@Data
public class ErrorDTO {
    private String code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> fieldErrors;
}
