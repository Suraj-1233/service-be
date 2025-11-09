package com.LaundryApplication.LaundryApplication.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex, ServletWebRequest req) {
        logError(req, ex);
        return buildResponse(ex.getMessage(), ex.getStatus(), req, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, ServletWebRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        logError(req, ex); // log for tracking

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", fieldErrors);
        body.put("path", req.getRequest().getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex, ServletWebRequest req) {
        logError(req, ex);
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, req, ex);
    }

    private void logError(ServletWebRequest req, Exception ex) {
        String method = req.getRequest().getMethod();
        String path = req.getRequest().getRequestURI();
        log.error("\n🚨 [EXCEPTION TRACKED]" +
                        "\n➡️ Endpoint: {} {}" +
                        "\n➡️ Exception: {}" +
                        "\n➡️ Message: {}" +
                        "\n➡️ Root Cause: {}" +
                        "\n=========================================",
                method, path,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                getRootCause(ex).getMessage(),
                ex);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status, ServletWebRequest req, Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("exception", ex.getClass().getSimpleName());
        body.put("path", req.getRequest().getRequestURI());
        return new ResponseEntity<>(body, status);
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex.getCause();
        return (cause == null || cause == ex) ? ex : getRootCause(cause);
    }
}
