package com.example.moneymanager.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.moneymanager.dto.ProfileDTO;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositroy.ProfileRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);

        // send activation email logic can be added here
        String activationLink = "http://localhost:8989/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager account";
        String body = "Click the following link to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .Email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImgUrl(profileDTO.getProfileImgUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

    };

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .Email(profileEntity.getEmail())
                .profileImgUrl(profileEntity.getProfileImgUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

}
