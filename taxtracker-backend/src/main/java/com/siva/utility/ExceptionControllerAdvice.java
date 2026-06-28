package com.siva.utility;

import com.siva.exception.TaxTrackerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(TaxTrackerException.class)
    public ResponseEntity<ErrorInfo> handleTaxTrackerException(TaxTrackerException ex) {
        logger.error("TaxTrackerException: ", ex);
        ErrorInfo error = new ErrorInfo(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception ex) {
        logger.error("Exception: ", ex);
        ErrorInfo error = new ErrorInfo("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
