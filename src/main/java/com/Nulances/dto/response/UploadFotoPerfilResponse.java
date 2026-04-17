package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFotoPerfilResponse {
    private String objectKey;
    private String uploadUrl;
    private Long expiresInSeconds;
}