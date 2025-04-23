package com.example.busymate.ui.screen.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.busymate.model.UMKM
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.busymate.R
import com.example.busymate.ui.component.FormUMKM
import com.example.busymate.utils.uploadImage
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditUMKMScreen(
    umkmId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    var umkmData by remember { mutableStateOf<UMKM?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var nameUMKM by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUMKM by remember { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    LaunchedEffect(umkmId) {
        database.child("umkm").child(umkmId)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.getValue(UMKM::class.java)
                        if (data != null) {
                            umkmData = data
                            nameUMKM = data.nameUMKM
                            location = data.location
                            description = data.description
                            contact = data.contact
                            category = data.category
                            imageUMKM = data.imageUMKM
                        }
                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Gagal memuat data UMKM", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                }
            )
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            FormUMKM(
                name = nameUMKM,
                onNameChange = { nameUMKM = it },
                location = location,
                onLocationChange = { location = it },
                category = category,
                onCategoryChange = { category = it },
                description = description,
                onDescriptionChange = { description = it },
                contact = contact,
                onContactChange = { contact = it },
                imageUrl = imageUMKM,
                selectedImageUri = selectedImageUri,
                onImageClick = { imagePickerLauncher.launch("image/*") }
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val imageUrl = if (selectedImageUri != null) {
                                uploadImage(selectedImageUri!!, context, imageUMKM)
                            } else {
                                imageUMKM
                            }

                            if (imageUrl == null) {
                                Log.e("CreateUMKM", "uploadImage returned null")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Gagal upload gambar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                return@launch
                            }
                            Log.d("CreateUMKM", "Received imageUrl: $imageUrl")

                            val updatedUMKM = UMKM(
                                id = umkmId,
                                nameUMKM = nameUMKM,
                                location = location,
                                description = description,
                                contact = contact,
                                category = category,
                                imageUMKM = imageUrl,
                                products = umkmData?.products ?: emptyList()
                            )

                            withContext(Dispatchers.Main) {
                                database.child("umkm").child(umkmId)
                                    .setValue(updatedUMKM)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "UMKM berhasil diperbarui",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Gagal memperbarui UMKM",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Terjadi kesalahan: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("EditUMKMScreen", "Error updating UMKM", e)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}