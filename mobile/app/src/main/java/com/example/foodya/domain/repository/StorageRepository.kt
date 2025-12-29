package com.example.foodya.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

/**
 * StorageRepository - Interface for image storage operations
 * 
 * Provides methods to upload images to cloud storage (Supabase)
 * and retrieve public URLs for uploaded images
 */
interface StorageRepository {
    /**
     * Upload an image to cloud storage
     * 
     * @param imageUri Android Uri of the image to upload
     * @param bucketName Name of the storage bucket (e.g., "food-images")
     * @return Flow<Result<String>> - Success returns public URL, Failure returns error
     */
    fun uploadImage(
        imageUri: Uri,
        bucketName: String = "images"
    ): Flow<Result<String>>

    /**
     * Delete an image from cloud storage
     * 
     * @param imageUrl Public URL or path of the image to delete
     * @param bucketName Name of the storage bucket
     * @return Flow<Result<Unit>> - Success returns Unit, Failure returns error
     */
    fun deleteImage(
        imageUrl: String,
        bucketName: String = "images"
    ): Flow<Result<Unit>>
}
