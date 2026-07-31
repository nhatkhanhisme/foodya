package com.foodya.foodya_backend.auth.application;

import com.foodya.foodya_backend.auth.api.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.auth.api.dto.JwtAuthResponse;
import com.foodya.foodya_backend.auth.api.dto.LoginRequest;
import com.foodya.foodya_backend.auth.api.dto.RegisterRequest;
import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.shared.security.JwtService;
import com.foodya.foodya_backend.auth.domain.Role;
import com.foodya.foodya_backend.auth.domain.User;
import com.foodya.foodya_backend.auth.domain.UserStatus;
import com.foodya.foodya_backend.auth.persistence.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository, passwordEncoder, authenticationManager, jwtService, tokenBlacklistService);
    }

    private User activeUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("nguyenvana")
                .email("nguyenvana@example.com")
                .password("hashed-password")
                .fullName("Nguyen Van A")
                .phoneNumber("+84987654321")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /** Stubs out the token-generation tail shared by register/login/refresh. */
    private void stubTokenGeneration(Authentication authentication, User user) {
        when(jwtService.generateToken(authentication)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(authentication)).thenReturn("refresh-token");
        when(jwtService.extractExpirationTime("access-token"))
                .thenReturn(System.currentTimeMillis() + 3_600_000L);
        when(jwtService.extractExpirationTime("refresh-token"))
                .thenReturn(System.currentTimeMillis() + 2_592_000_000L);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Nested
    class RegisterUser {

        private RegisterRequest.RegisterRequestBuilder validRequest() {
            return RegisterRequest.builder()
                    .username("nguyenvana")
                    .email("nguyenvana@example.com")
                    .password("SecurePass123!")
                    .fullName("Nguyen Van A")
                    .phoneNumber("0987654321")
                    .role("CUSTOMER");
        }

        @Test
        void registersAndReturnsTokens() {
            RegisterRequest request = validRequest().build();
            when(userRepository.existsByUsername("nguyenvana")).thenReturn(false);
            when(userRepository.existsByEmail("nguyenvana@example.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("+84987654321")).thenReturn(false);
            when(passwordEncoder.encode("SecurePass123!")).thenReturn("hashed-password");

            Authentication authentication = new UsernamePasswordAuthenticationToken("nguyenvana", null);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);

            User saved = activeUser();
            stubTokenGeneration(authentication, saved);

            JwtAuthResponse response = authService.registerUser(request);

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getUserId()).isEqualTo(saved.getId().toString());
            assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(response.getUsername()).isEqualTo(saved.getUsername());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User persisted = userCaptor.getValue();
            assertThat(persisted.getUsername()).isEqualTo("nguyenvana");
            assertThat(persisted.getPassword()).isEqualTo("hashed-password");
            assertThat(persisted.getPhoneNumber()).isEqualTo("+84987654321");
            assertThat(persisted.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(persisted.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(persisted.getIsEmailVerified()).isFalse();
        }

        @Test
        void normalizesLocalPhoneNumberToInternationalFormat() {
            RegisterRequest request = validRequest().phoneNumber("0912345678").build();
            when(userRepository.existsByPhoneNumber("+84912345678")).thenReturn(false);

            Authentication authentication = new UsernamePasswordAuthenticationToken("nguyenvana", null);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            stubTokenGeneration(authentication, activeUser());

            authService.registerUser(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPhoneNumber()).isEqualTo("+84912345678");
        }

        @Test
        void skipsPhoneUniquenessCheckWhenPhoneNotProvided() {
            RegisterRequest request = validRequest().phoneNumber(null).build();
            Authentication authentication = new UsernamePasswordAuthenticationToken("nguyenvana", null);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            stubTokenGeneration(authentication, activeUser());

            authService.registerUser(request);

            verify(userRepository, never()).existsByPhoneNumber(anyString());
        }

        @Test
        void rejectsDuplicateUsername() {
            RegisterRequest request = validRequest().build();
            when(userRepository.existsByUsername("nguyenvana")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE);
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsDuplicateEmail() {
            RegisterRequest request = validRequest().build();
            when(userRepository.existsByEmail("nguyenvana@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE);
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsDuplicatePhoneNumber() {
            RegisterRequest request = validRequest().build();
            when(userRepository.existsByPhoneNumber("+84987654321")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE);
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsRoleThatCannotBeSelfAssigned() {
            RegisterRequest request = validRequest().role("ADMIN").build();

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Login {

        @Test
        void authenticatesAndStampsLastLogin() {
            LoginRequest request = LoginRequest.builder().username("nguyenvana").password("SecurePass123!").build();
            Authentication authentication = new UsernamePasswordAuthenticationToken("nguyenvana", null);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);

            User user = activeUser();
            stubTokenGeneration(authentication, user);

            JwtAuthResponse response = authService.login(request);

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getUsername()).isEqualTo("nguyenvana");
            assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(user.getLastLoginAt()).isNotNull();
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void propagatesBadCredentials() {
            LoginRequest request = LoginRequest.builder().username("nguyenvana").password("wrong").build();
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class RefreshToken {

        @Test
        void issuesNewAccessTokenForValidRefreshToken() {
            User user = activeUser();
            when(jwtService.validateToken("refresh-token")).thenReturn(true);
            when(jwtService.isTokenType(eq("refresh-token"), any())).thenReturn(true);
            when(tokenBlacklistService.isRevoked("refresh-token")).thenReturn(false);
            when(jwtService.extractUsername("refresh-token")).thenReturn("nguyenvana");
            when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(user));
            when(jwtService.generateToken(any())).thenReturn("new-access-token");
            when(jwtService.extractExpirationTime("new-access-token"))
                    .thenReturn(System.currentTimeMillis() + 3_600_000L);
            when(jwtService.extractExpirationTime("refresh-token"))
                    .thenReturn(System.currentTimeMillis() + 2_592_000_000L);

            JwtAuthResponse response = authService.refreshToken("refresh-token");

            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(response.getUsername()).isEqualTo("nguyenvana");
            assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
        }

        @Test
        void rejectsInvalidSignature() {
            when(jwtService.validateToken("bad-token")).thenReturn(false);

            assertThatThrownBy(() -> authService.refreshToken("bad-token"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_TOKEN_REVOKED);
        }

        @Test
        void rejectsAccessTokenPresentedAsRefreshToken() {
            when(jwtService.validateToken("access-token")).thenReturn(true);
            when(jwtService.isTokenType(eq("access-token"), any())).thenReturn(false);

            assertThatThrownBy(() -> authService.refreshToken("access-token"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_TOKEN_REVOKED);
        }

        @Test
        void rejectsRevokedRefreshToken() {
            when(jwtService.validateToken("refresh-token")).thenReturn(true);
            when(jwtService.isTokenType(eq("refresh-token"), any())).thenReturn(true);
            when(tokenBlacklistService.isRevoked("refresh-token")).thenReturn(true);

            assertThatThrownBy(() -> authService.refreshToken("refresh-token"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_TOKEN_REVOKED);
        }

        @Test
        void rejectsBannedAccount() {
            User banned = activeUser();
            banned.setStatus(UserStatus.BANNED);
            when(jwtService.validateToken("refresh-token")).thenReturn(true);
            when(jwtService.isTokenType(eq("refresh-token"), any())).thenReturn(true);
            when(tokenBlacklistService.isRevoked("refresh-token")).thenReturn(false);
            when(jwtService.extractUsername("refresh-token")).thenReturn("nguyenvana");
            when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(banned));

            assertThatThrownBy(() -> authService.refreshToken("refresh-token"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_ACCOUNT_BANNED);
        }

        @Test
        void rejectsWhenUserNoLongerExists() {
            when(jwtService.validateToken("refresh-token")).thenReturn(true);
            when(jwtService.isTokenType(eq("refresh-token"), any())).thenReturn(true);
            when(tokenBlacklistService.isRevoked("refresh-token")).thenReturn(false);
            when(jwtService.extractUsername("refresh-token")).thenReturn("ghost");
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refreshToken("refresh-token"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Nested
    class Logout {

        @Test
        void revokesBothTokensWhenValid() {
            when(jwtService.validateToken("access-token")).thenReturn(true);
            when(jwtService.validateToken("refresh-token")).thenReturn(true);

            authService.logout("access-token", "refresh-token");

            verify(tokenBlacklistService).revoke("access-token");
            verify(tokenBlacklistService).revoke("refresh-token");
        }

        @Test
        void skipsBlankOrInvalidTokens() {
            when(jwtService.validateToken("refresh-token")).thenReturn(false);

            authService.logout(null, "refresh-token");

            verify(tokenBlacklistService, never()).revoke(anyString());
        }
    }

    @Nested
    class ChangePassword {

        private ChangePasswordRequest.ChangePasswordRequestBuilder validRequest() {
            return ChangePasswordRequest.builder()
                    .currentPassword("OldPassword123!")
                    .newPassword("NewPassword123!")
                    .confirmPassword("NewPassword123!");
        }

        @Test
        void updatesPasswordWhenCurrentMatches() {
            User user = activeUser();
            user.setPassword("hashed-old");
            when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("OldPassword123!", "hashed-old")).thenReturn(true);
            when(passwordEncoder.matches("NewPassword123!", "hashed-old")).thenReturn(false);
            when(passwordEncoder.encode("NewPassword123!")).thenReturn("hashed-new");

            authService.changePassword("nguyenvana", validRequest().build());

            assertThat(user.getPassword()).isEqualTo("hashed-new");
            verify(userRepository).save(user);
        }

        @Test
        void rejectsMismatchedConfirmation() {
            ChangePasswordRequest request = validRequest().confirmPassword("Different123!").build();

            assertThatThrownBy(() -> authService.changePassword("nguyenvana", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR);
            verify(userRepository, never()).findByUsername(anyString());
        }

        @Test
        void rejectsWrongCurrentPassword() {
            User user = activeUser();
            user.setPassword("hashed-old");
            when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("OldPassword123!", "hashed-old")).thenReturn(false);

            assertThatThrownBy(() -> authService.changePassword("nguyenvana", validRequest().build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR);
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsNewPasswordSameAsCurrent() {
            User user = activeUser();
            user.setPassword("hashed-old");
            when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("OldPassword123!", "hashed-old")).thenReturn(true);
            when(passwordEncoder.matches("NewPassword123!", "hashed-old")).thenReturn(true);

            assertThatThrownBy(() -> authService.changePassword("nguyenvana", validRequest().build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR);
            verify(userRepository, never()).save(any());
        }
    }
}
