package com.example.busymate.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import com.example.busymate.BuildConfig

suspend fun uploadImage(
    uri: Uri,
    context: Context,
    oldImageUrl: String? = null
): String? {
    return try {
        val supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Storage)
        }
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            ?: run {
                Log.e("UploadImage", "Failed to read bytes from URI: $uri")
                return null
            }

        val bucket = supabase.storage.from("umkm-images")
        val path = "umkm/img_${System.currentTimeMillis()}.jpg"

        bucket.upload(path, bytes, upsert = true)

        oldImageUrl?.let { oldUrl ->
            val oldPath = oldUrl.substringAfter("storage/v1/object/public/umkm-images/")
            if (oldPath != path) {
                bucket.delete(oldPath)
                Log.d("UploadImage", "Old image deleted: $oldPath")
            }
        }
        bucket.publicUrl(path).also { url ->
            Log.d("UploadImage", "Upload succeeded, public URL = $url")
        }
    } catch (t: Throwable) {
        Log.e("UploadImage", "Exception during upload", t)
        null
    }
}