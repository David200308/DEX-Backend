package com.skyproton.dex_backend.repository;

import com.skyproton.dex_backend.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AuthRepo extends JpaRepository<Auth, Long> {
    @Query("SELECT a FROM Auth a WHERE a.uuid = ?1")
    Auth findByUuid(String uuid);
}
