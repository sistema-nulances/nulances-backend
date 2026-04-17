package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auth2FatoresSetupResponse {
    private String secret;
    private String otpauthUrl;
}