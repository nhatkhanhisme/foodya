package com.foodya.foodya_backend.identity.application;

import com.foodya.foodya_backend.identity.domain.Role;
import com.foodya.foodya_backend.identity.domain.User;
import com.foodya.foodya_backend.identity.domain.UserStatus;
import com.foodya.foodya_backend.identity.persistence.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService service;

    private User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("nguyenvana")
                .email("nguyenvana@example.com")
                .password("hashed")
                .fullName("Nguyen Van A")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void loadUserByUsernameReturnsTheEntityItself() {
        service = new CustomUserDetailsService(userRepository);
        User user = user();
        when(userRepository.findByUsername("nguyenvana")).thenReturn(Optional.of(user));

        UserDetails loaded = service.loadUserByUsername("nguyenvana");

        // User implements UserDetails directly, so isEnabled()/isAccountNonLocked() reflect live DB state
        assertThat(loaded).isSameAs(user);
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        service = new CustomUserDetailsService(userRepository);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByEmailReturnsTheEntity() {
        service = new CustomUserDetailsService(userRepository);
        User user = user();
        when(userRepository.findByEmail("nguyenvana@example.com")).thenReturn(Optional.of(user));

        assertThat(service.loadUserByEmail("nguyenvana@example.com")).isSameAs(user);
    }

    @Test
    void loadUserByIdReturnsTheEntity() {
        service = new CustomUserDetailsService(userRepository);
        User user = user();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThat(service.loadUserById(user.getId())).isSameAs(user);
    }
}
