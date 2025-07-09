package com.skyproton.dex_backend.tools;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.security")
public class Config {
    private String key;
    private String jwtkey;
    private String infuraApiKey;
}
