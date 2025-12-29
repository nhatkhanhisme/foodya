package com.example.foodya.ui.screen.merchant.menu.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodya.ui.components.ImagePickerBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDialog(
    isCreating: Boolean,
    name: String,
    description: String,
    price: String,
    imageUrl: String,
    category: String,
    isAvailable: Boolean,
    isProcessing: Boolean,
    selectedImageUri: Uri?,
    isUploadingImage: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onIsAvailableChange: (Boolean) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageCleared: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isCreating) "Thêm món mới" else "Chỉnh sửa món") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Tên món *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Mô tả") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = onPriceChange,
                        label = { Text("Giá (VNĐ) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = onCategoryChange,
                        label = { Text("Danh mục") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    ImagePickerBox(
                        selectedImageUri = selectedImageUri,
                        onImageSelected = onImageSelected,
                        onImageCleared = onImageCleared,
                        label = "Hình ảnh món ăn",
                        isRequired = true,
                        isLoading = isUploadingImage
                    )
                }
                
                if (!isCreating) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Còn hàng")
                            Switch(
                                checked = isAvailable,
                                onCheckedChange = onIsAvailableChange
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Lưu")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text("Hủy")
            }
        }
    )
}
