package com.skyproton.dex_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "auth")
@Entity
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "wallet_address", nullable = false)
    private String wallet_address;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
