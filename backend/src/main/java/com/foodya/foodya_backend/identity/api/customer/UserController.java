package com.foodya.foodya_backend.identity.api.customer;

import com.foodya.foodya_backend.identity.api.dto.UpdateProfileRequest;
import com.foodya.foodya_backend.identity.api.dto.UserProfileResponse;
import com.foodya.foodya_backend.identity.application.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User Profile APIs for mobile app")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {

    private final ProfileService profileService;

    @Operation(summary = "Get Current User Profile",
        description = "Retrieve the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        )
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(profileService.getCurrentUserProfile());
    }

    @Operation(summary = "Update my profile", description = "Update profile information of the current user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully. Phone number auto-normalized to +84...",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        )
    })
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        log.info("Controller received request: {}", request);
        return ResponseEntity.ok(profileService.updateProfile(request));
    }
}
