package com.example.axspring.ocr.application.port.in;

import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.user.domain.UserId;

public record OcrCommand(
    UserId requestedBy,
    OcrImage image
) {
    
}
