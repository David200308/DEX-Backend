package com.skyproton.dex_backend.tools;

import org.springframework.stereotype.Component;

@Component
public class Generator {
    public String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    public Long generateTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
