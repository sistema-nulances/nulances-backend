package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadBemMidiaResponse {

    private String objectKey;
    private String uploadUrl;
    private Long expiresInSeconds;
}