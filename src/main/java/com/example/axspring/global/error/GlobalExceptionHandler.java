package com.example.axspring.global.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.axspring.ocr.application.exception.OcrFileTooLargeException;
import com.example.axspring.ocr.application.exception.UnsupportedOcrFileTypeException;
import com.example.axspring.user.application.exception.DuplicateEmailException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        ErrorResponse response = new ErrorResponse(
                errorCode.code(),
                errorCode.message(),
                MDC.get("requestId")
        );

        return ResponseEntity
                .status(errorCode.status())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        Exception exception
    ) {
        log.error("Unhandled exception", exception);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                errorCode.code(),
                errorCode.message(),
                MDC.get("requestId")
        );

        return ResponseEntity
                .status(errorCode.status())
                .body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
        DuplicateEmailException exception
    ) {
        ErrorCode errorCode = ErrorCode.DUPLICATE_EMAIL;

        ErrorResponse response = new ErrorResponse(
            errorCode.code(),
            errorCode.message(),
            MDC.get("requestId")
        );

        return ResponseEntity
            .status(errorCode.status())
            .body(response);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            UnsupportedOcrFileTypeException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(
            RuntimeException exception
    ) {
        return error(ErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(OcrFileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleOcrFileTooLargeException(
            OcrFileTooLargeException exception
    ) {
        return error(ErrorCode.FILE_TOO_LARGE);
    }

    private ResponseEntity<ErrorResponse> error(ErrorCode errorCode) {
        ErrorResponse response = new ErrorResponse(
                errorCode.code(),
                errorCode.message(),
                MDC.get("requestId"));
        return ResponseEntity.status(errorCode.status()).body(response);
    }
}
