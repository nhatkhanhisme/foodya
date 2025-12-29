package com.example.foodya.domain.usecase

import com.example.foodya.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        // Validation logic
        if (currentPassword.isBlank()) {
            return Result.failure(Exception("Mật khẩu hiện tại không được để trống"))
        }

        if (newPassword.isBlank()) {
            return Result.failure(Exception("Mật khẩu mới không được để trống"))
        }

        if (newPassword.length < 6) {
            return Result.failure(Exception("Mật khẩu mới phải có ít nhất 6 ký tự"))
        }

        if (newPassword != confirmPassword) {
            return Result.failure(Exception("Mật khẩu xác nhận không khớp"))
        }

        if (currentPassword == newPassword) {
            return Result.failure(Exception("Mật khẩu mới phải khác mật khẩu hiện tại"))
        }

        // Call repository
        return authRepository.changePassword(currentPassword, newPassword, confirmPassword)
    }
}
