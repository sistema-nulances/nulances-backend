package com.Nulances.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerUploadRequest {

    @NotBlank(message = "Nome do arquivo é obrigatório.")
    private String fileName;

    @NotBlank(message = "Content-Type é obrigatório.")
    private String contentType;
}