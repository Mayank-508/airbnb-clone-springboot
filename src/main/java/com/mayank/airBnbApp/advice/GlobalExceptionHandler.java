package com.mayank.airBnbApp.advice;

import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception)
    {
        ApiError apiError=ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return buildResponseEntity(apiError);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception)
    {
        ApiError apiError= ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage()).build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationErrors(MethodArgumentNotValidException methodArgumentNotValidException)
    {
        List<String> errors= methodArgumentNotValidException.getBindingResult().getAllErrors()
                .stream()
                .map(error-> error.getDefaultMessage()).toList();

        ApiError apiError=ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("input Validaton Failed")
                .subErrors(errors).build();

        return buildResponseEntity(apiError);
    }


    public ResponseEntity<ApiResponse<?>> buildResponseEntity(ApiError apiError)
    {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

}
