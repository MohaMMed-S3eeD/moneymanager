package com.example.moneymanager.repositroy;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moneymanager.entity.ProfileEntity;

public interface ProfileRepo extends JpaRepository <ProfileEntity, Long> {
    Optional<ProfileEntity> findByEmail(String email);
}