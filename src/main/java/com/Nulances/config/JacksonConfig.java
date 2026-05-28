package com.Nulances.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    /**
     * Configura o ObjectMapper para tratar strings vazias ("") em campos enum
     * como null, evitando erros 500 ao criar anúncios com categorias não-veiculares
     * onde campos como marca, tipo, combustível etc. não são enviados.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // String vazia "" em campo Enum é tratada como null (não lança exceção)
        mapper.coercionConfigDefaults()
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}
