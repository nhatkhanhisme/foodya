package com.example.foodya.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Cấu hình màu cho chế độ TỐI (Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    secondary = SecondaryYellow,
    tertiary = PrimaryLight,
    background = Black,
    surface = Color(0xFF1E1E1E), // Màu card trong dark mode
    onPrimary = White,
    onSecondary = Black,
    onBackground = White,
    onSurface = White
)

// 2. Cấu hình màu cho chế độ SÁNG (Light Mode)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,      // Màu của nút, header, icon chính
    secondary = SecondaryYellow,  // Màu phụ trợ
    tertiary = PrimaryLight,

    background = LightGray,       // Màu nền toàn app (nên để xám rất nhạt cho sạch)
    surface = White,              // Màu nền của các Card, Box, Popup

    onPrimary = White,            // Màu chữ nằm TRÊN nền Primary (Chữ trên nút cam -> màu trắng)
    onSecondary = Black,
    onBackground = Black,         // Màu chữ nằm TRÊN nền Background
    onSurface = Black             // Màu chữ nằm TRÊN nền Surface (Card)
)

@Composable
fun FoodyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // QUAN TRỌNG: Đặt dynamicColor = false để app luôn dùng màu Cam của bạn
    // Nếu để true, nó sẽ lấy màu theo hình nền điện thoại user (sẽ ra màu xanh/tím...)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Code set màu cho thanh trạng thái (Status Bar)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Đổi màu status bar thành màu Cam
            window.statusBarColor = colorScheme.primary.toArgb()
            // Hoặc muốn Status bar màu trắng icon đen thì dùng:
            // window.statusBarColor = Color.White.toArgb()
            // WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}