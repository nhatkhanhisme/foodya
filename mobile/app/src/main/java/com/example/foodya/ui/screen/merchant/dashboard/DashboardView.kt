package com.example.foodya.ui.screen.merchant.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.navigation.NavHostController

@Composable
fun DashboardView(navController: NavHostController){
    Column(){
        Text(text = "Merchant Dashboard")
    }
}