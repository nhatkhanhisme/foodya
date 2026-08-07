package com.foodya.foodya_backend.identity.api.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodya.foodya_backend.identity.api.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.identity.api.dto.JwtAuthResponse;
import com.foodya.foodya_backend.identity.api.dto.LoginRequest;
import com.foodya.foodya_backend.identity.api.dto.RefreshTokenRequest;
import com.foodya.foodya_backend.identity.api.dto.RegisterRequest;
import com.foodya.foodya_backend.identity.application.AuthService;
import com.foodya.foodya_backend.common.exception.GlobalExceptionHandler;
import com.foodya.foodya_backend.common.security.AuthCookieService;
import com.foodya.foodya_backend.common.security.CsrfTokenService;
import com.foodya.foodya_backend.identity.domain.Role;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;
    @Mock
    private AuthCookieService authCookieService;
    @Mock
    private CsrfTokenService csrfTokenService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService, authCookieService, csrfTokenService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private JwtAuthResponse tokenResponse(String accessToken, String refreshToken) {
        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3_600_000L)
                .refreshTokenExpiresIn(2_592_000_000L)
                .userId("user-id")
                .username("nguyenvana")
                .role(Role.CUSTOMER)
                .build();
    }

    @Nested
    class Register {

        @Test
        void registersAndSetsAuthCookies() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("nguyenvana")
                    .email("nguyenvana@example.com")
                    .password("SecurePass123!")
                    .fullName("Nguyen Van A")
                    .phoneNumber("0987654321")
                    .role("CUSTOMER")
                    .build();
            when(authService.registerUser(any())).thenReturn(tokenResponse("access-1", "refresh-1"));
            when(csrfTokenService.deriveToken("refresh-1")).thenReturn("csrf-1");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").value("access-1"));

            verify(authCookieService).setAuthCookies(
                    any(HttpServletResponse.class), eq("refresh-1"), eq("csrf-1"), eq(2_592_000_000L));
        }
    }

    @Nested
    class Login {

        @Test
        void logsInAndSetsAuthCookies() throws Exception {
            LoginRequest request = LoginRequest.builder().username("nguyenvana").password("SecurePass123!").build();
            when(authService.login(any())).thenReturn(tokenResponse("access-1", "refresh-1"));
            when(csrfTokenService.deriveToken("refresh-1")).thenReturn("csrf-1");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-1"));

            verify(authCookieService).setAuthCookies(
                    any(HttpServletResponse.class), eq("refresh-1"), eq("csrf-1"), eq(2_592_000_000L));
        }
    }

    @Nested
    class Refresh {

        @Test
        void refreshesFromCookieWhenCsrfHeaderIsValid() throws Exception {
            when(csrfTokenService.isValid("cookie-refresh", "csrf-header")).thenReturn(true);
            when(authService.refreshToken("cookie-refresh")).thenReturn(tokenResponse("access-2", "cookie-refresh"));
            when(csrfTokenService.deriveToken("cookie-refresh")).thenReturn("csrf-2");

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .cookie(new Cookie(AuthCookieService.REFRESH_COOKIE_NAME, "cookie-refresh"))
                            .header("X-XSRF-TOKEN", "csrf-header"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-2"));

            verify(authCookieService).setAuthCookies(
                    any(HttpServletResponse.class), eq("cookie-refresh"), eq("csrf-2"), eq(2_592_000_000L));
        }

        @Test
        void rejectsCookieRefreshWithMissingCsrfHeader() throws Exception {
            when(csrfTokenService.isValid(eq("cookie-refresh"), any())).thenReturn(false);

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .cookie(new Cookie(AuthCookieService.REFRESH_COOKIE_NAME, "cookie-refresh")))
                    .andExpect(status().isForbidden());

            verify(authService, never()).refreshToken(anyString());
        }

        @Test
        void refreshesFromBodyForMobileClientsWithoutTouchingCookies() throws Exception {
            RefreshTokenRequest body = RefreshTokenRequest.builder().refreshToken("mobile-refresh").build();
            when(authService.refreshToken("mobile-refresh")).thenReturn(tokenResponse("access-3", "mobile-refresh"));

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-3"));

            verify(authCookieService, never()).setAuthCookies(any(), anyString(), anyString(), any(Long.class));
            verify(csrfTokenService, never()).isValid(any(), any());
        }

        @Test
        void rejectsWhenNeitherCookieNorBodyProvideAToken() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh"))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).refreshToken(anyString());
        }
    }

    @Nested
    class Logout {

        @Test
        void logsOutFromCookieWithValidCsrfAndClearsCookies() throws Exception {
            when(csrfTokenService.isValid("cookie-refresh", "csrf-header")).thenReturn(true);

            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer access-token")
                            .header("X-XSRF-TOKEN", "csrf-header")
                            .cookie(new Cookie(AuthCookieService.REFRESH_COOKIE_NAME, "cookie-refresh")))
                    .andExpect(status().isNoContent());

            verify(authService).logout("access-token", "cookie-refresh");
            verify(authCookieService).clearAuthCookies(any(HttpServletResponse.class));
        }

        @Test
        void rejectsCookieLogoutWithInvalidCsrf() throws Exception {
            when(csrfTokenService.isValid(eq("cookie-refresh"), any())).thenReturn(false);

            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer access-token")
                            .cookie(new Cookie(AuthCookieService.REFRESH_COOKIE_NAME, "cookie-refresh")))
                    .andExpect(status().isForbidden());

            verify(authService, never()).logout(anyString(), anyString());
        }

        @Test
        void logsOutMobileClientUsingBodyRefreshTokenAndStillClearsCookies() throws Exception {
            RefreshTokenRequest body = RefreshTokenRequest.builder().refreshToken("mobile-refresh").build();

            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isNoContent());

            verify(authService).logout("access-token", "mobile-refresh");
            verify(authCookieService).clearAuthCookies(any(HttpServletResponse.class));
            verify(csrfTokenService, never()).isValid(any(), any());
        }
    }

    @Nested
    class ChangePassword {

        @Test
        void changesPasswordForAuthenticatedUser() throws Exception {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("nguyenvana", null));
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .currentPassword("OldPassword123!")
                    .newPassword("NewPassword123!")
                    .confirmPassword("NewPassword123!")
                    .build();

            mockMvc.perform(post("/api/v1/auth/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password changed successfully"));

            verify(authService).changePassword(eq("nguyenvana"), any());
        }
    }
}
