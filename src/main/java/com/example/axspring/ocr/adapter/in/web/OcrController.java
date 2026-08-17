package com.example.axspring.ocr.adapter.in.web;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.axspring.ocr.application.exception.OcrFileTooLargeException;
import com.example.axspring.ocr.application.exception.UnsupportedOcrFileTypeException;
import com.example.axspring.ocr.application.port.in.OcrCommand;
import com.example.axspring.ocr.application.port.in.ParseBusinessCardUseCase;
import com.example.axspring.ocr.application.port.in.ParseReceiptUseCase;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;
import com.example.axspring.user.domain.UserId;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/ocr")
public class OcrController {
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_MIME_TYPES =
            Set.of(
                "image/jpeg",
                "image/png",
                "image/webp"
            );

    private final ParseBusinessCardUseCase parseBusinessCardUseCase;
    private final ParseReceiptUseCase parseReceiptUseCase;

    public OcrController(
        ParseBusinessCardUseCase parseBusinessCardUseCase,
        ParseReceiptUseCase parseReceiptUseCase
    ) {
        this.parseBusinessCardUseCase = parseBusinessCardUseCase;
        this.parseReceiptUseCase = parseReceiptUseCase;
    }

    @PostMapping(
        value = "/business-card",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BusinessCardOcrResponse>
            parseBusinessCard(
                    @AuthenticationPrincipal Jwt jwt,
                    @RequestPart("file") MultipartFile file,
                    HttpServletRequest request
    ) throws IOException {

        OcrImage image = toOcrImage(file);

        BusinessCardOcrResult result = parseBusinessCardUseCase.parseBusinessCard(
            new OcrCommand(
                            new UserId(jwt.getSubject()),
                            image,
                            request.getRemoteAddr(),
                            request.getHeader("User-Agent")
                )
            );

        return ResponseEntity.ok(
                BusinessCardOcrResponse.from(result));
    }
    
    @PostMapping(
            value = "/receipt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReceiptOcrResponse>
            parseReceipt(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {

        OcrImage image = toOcrImage(file);

        ReceiptOcrResult result = parseReceiptUseCase
                .parseReceipt(
                        new OcrCommand(
                         new UserId(jwt.getSubject()),
                            image,
                            request.getRemoteAddr(),
                            request.getHeader("User-Agent")
                    )
                );

        return ResponseEntity.ok(
                ReceiptOcrResponse.from(result));
    }
    
    private OcrImage toOcrImage(
        MultipartFile file
    ) throws IOException {
        validateFile(file);

        return new OcrImage(
                file.getBytes(),
                resolveFileName(file),
                file.getContentType(),
                file.getSize());
    }
    
    private void validateFile(
        MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "OCR file must not be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new OcrFileTooLargeException(
                    MAX_FILE_SIZE);
        }

        String mimeType = file.getContentType();

        if (mimeType == null
                || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new UnsupportedOcrFileTypeException(
                    mimeType);
        }
    }
    
    private String resolveFileName(
        MultipartFile file
    ) {
        String name = file.getOriginalFilename();

        return name == null || name.isBlank() ? "ocr-image" : name;
    }
}
