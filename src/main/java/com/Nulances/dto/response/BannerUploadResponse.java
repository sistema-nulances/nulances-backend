package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BannerUploadResponse {
    private String uploadUrl;
    private String objectKey;
}