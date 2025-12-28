package com.example.moneymanager.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.moneymanager.dto.AuthDTO;
import com.example.moneymanager.dto.ProfileDTO;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositroy.ProfileRepo;
import com.example.moneymanager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));
        newProfile = profileRepo.save(newProfile);

        // send activation email logic can be added here
        String activationLink = "http://localhost:8989/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager account";
        String body = "Click the following link to active your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
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

    public boolean activateProfile(String token) {
        return profileRepo.findByActivationToken(token).map(profile -> {
            profile.setIsActive(true);
            profileRepo.save(profile);
            return true;
        }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepo.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentProfile = null;
        if (email == null) {
            currentProfile = getCurrentProfile();
        } else {
            currentProfile = profileRepo.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentProfile.getId())
                .fullName(currentProfile.getFullName())
                .Email(currentProfile.getEmail())
                .profileImgUrl(currentProfile.getProfileImgUrl())
                .createdAt(currentProfile.getCreatedAt())
                .updatedAt(currentProfile.getUpdatedAt())
                .build();

    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            // Generate JWT token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "User", getPublicProfile(authDTO.getEmail()));

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid Email or Password" + e);
        }
    }
}
