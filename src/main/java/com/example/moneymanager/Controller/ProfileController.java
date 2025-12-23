package com.example.moneymanager.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moneymanager.dto.ProfileDTO;
import com.example.moneymanager.service.ProfileService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO profileRegistered = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileRegistered);
    }

    @GetMapping("/activate")
    public String getMethodName(@RequestParam String token) {
        boolean activated = profileService.activateProfile(token);
        if (activated) {
            return "Profile activated successfully.";
        } else {
            return "Invalid activation token.";
        }    
    }
    
}
