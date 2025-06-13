package com.pukhovkirill.mapsketch.handler;

import java.util.Map;
import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pukhovkirill.mapsketch.exception.BadRequestException;
import com.pukhovkirill.mapsketch.exception.GeoObjectNotFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> badRequestException(BadRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "error", "Bad request",
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> geoObjectNotFoundException(GeoObjectNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "error", "Not found",
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> illegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "error", "Bad request",
                "message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = String.format("Invalid '%s': '%s'", e.getName(), e.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", new Timestamp(System.currentTimeMillis()).toString(),
                "error", "Bad request",
                "message", msg));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> internalServerErrorException(RuntimeException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "error", "Internal server error",
                "message", e.getMessage()));
    }
}
