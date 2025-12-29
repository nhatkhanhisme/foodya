package com.example.foodya.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.foodya.domain.repository.StorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StorageRepositoryImpl - Implementation of StorageRepository using Supabase Storage
 * 
 * Handles:
 * - Converting Android Uri to ByteArray
 * - Uploading images to Supabase Storage
 * - Generating unique filenames
 * - Returning public URLs
 */
@Singleton
class StorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageRepository {

    companion object {
        private const val TAG = "StorageRepository"
        
        // TODO: Replace with your actual Supabase credentials
        private const val SUPABASE_URL = "https://rxifptroexopdnqtxjnk.supabase.co"
        private const val SUPABASE_ANON_KEY = "sb_publishable_D14ygBkxN6-ddpGc-GPljQ_kZjWFOls"
    }

    // Lazy initialization of Supabase client
    private val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Storage)
        }
    }

    override fun uploadImage(
        imageUri: Uri,
        bucketName: String
    ): Flow<Result<String>> = flow {
        try {
            Log.d(TAG, "Starting image upload for URI: $imageUri")

            // Step 1: Convert Uri to ByteArray
            val imageBytes = uriToByteArray(imageUri)
                ?: throw IllegalArgumentException("Failed to read image data from URI")

            Log.d(TAG, "Image data read successfully. Size: ${imageBytes.size} bytes")

            // Step 2: Generate unique filename
            val timestamp = System.currentTimeMillis()
            val uuid = UUID.randomUUID().toString().substring(0, 8)
            val extension = getFileExtension(imageUri) ?: "jpg"
            val fileName = "food_${timestamp}_${uuid}.$extension"

            Log.d(TAG, "Generated filename: $fileName")

            // Step 3: Upload to Supabase Storage
            val bucket = supabase.storage.from(bucketName)

            bucket.upload(
                path = fileName,
                data = imageBytes
            ) {
                upsert = false // hoặc true nếu bạn muốn ghi đè file trùng tên
            }

            Log.d(TAG, "Upload successful")

            // Step 4: Get public URL
            val publicUrl = bucket.publicUrl(fileName)
            
            Log.d(TAG, "Public URL: $publicUrl")

            emit(Result.success(publicUrl))

        } catch (e: Exception) {
            Log.e(TAG, "Upload failed", e)
            emit(Result.failure(
                Exception("Failed to upload image: ${e.message}", e)
            ))
        }
    }

    override fun deleteImage(
        imageUrl: String,
        bucketName: String
    ): Flow<Result<Unit>> = flow {
        try {
            Log.d(TAG, "Deleting image: $imageUrl")

            // Extract filename from URL
            val fileName = imageUrl.substringAfterLast("/")
            
            val bucket = supabase.storage.from(bucketName)
            bucket.delete(fileName)

            Log.d(TAG, "Image deleted successfully")
            emit(Result.success(Unit))

        } catch (e: Exception) {
            Log.e(TAG, "Delete failed", e)
            emit(Result.failure(
                Exception("Failed to delete image: ${e.message}", e)
            ))
        }
    }

    /**
     * Convert Android Uri to ByteArray using ContentResolver
     */
    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read image data", e)
            null
        }
    }

    /**
     * Extract file extension from Uri
     */
    private fun getFileExtension(uri: Uri): String? {
        return when {
            // Try to get extension from URI path
            uri.path?.contains(".") == true -> {
                uri.path?.substringAfterLast(".")
            }
            // Try to get MIME type from ContentResolver
            else -> {
                val mimeType = context.contentResolver.getType(uri)
                when (mimeType) {
                    "image/jpeg", "image/jpg" -> "jpg"
                    "image/png" -> "png"
                    "image/webp" -> "webp"
                    "image/gif" -> "gif"
                    else -> "jpg" // Default fallback
                }
            }
        }
    }
}
