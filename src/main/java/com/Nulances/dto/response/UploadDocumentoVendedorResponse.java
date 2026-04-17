package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadDocumentoVendedorResponse {
    private String uploadUrl;
    private String objectKey;
    private String fileUrl;
    private long expiresInSeconds;
}