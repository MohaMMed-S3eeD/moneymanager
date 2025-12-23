package com.example.moneymanager.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String Email;
    private String password;
    private String profileImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
