package com.example.foodya.ui.screen.customer.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.navigation.NavHostController

@Composable
fun CartView(navController: NavHostController){
    Column(){
        Text(text = "Customer Cart")
    }
}