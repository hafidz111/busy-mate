package com.example.busymate.ui.screen.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busymate.R
import com.example.busymate.model.UMKM
import com.example.busymate.ui.component.FormUMKM
import com.example.busymate.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateUMKMScreen(
    modifier: Modifier, onCreateSuccess: () -> Unit
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val imageUMKM by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.form_umkm), fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        FormUMKM(
            name = name,
            onNameChange = { name = it },
            contact = contact,
            onContactChange = { contact = it },
            location = location,
            onLocationChange = { location = it },
            category = category,
            onCategoryChange = { category = it },
            description = description,
            onDescriptionChange = { description = it },
            selectedImageUri = selectedImageUri,
            onImageClick = { imagePickerLauncher.launch("image/*") },
            imageUrl = imageUMKM ?: ""
        )

        Button(
            onClick = {
                val uid = user?.uid ?: return@Button

                if (selectedImageUri == null) {
                    Toast.makeText(context, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val imageUrl = uploadImage(selectedImageUri!!, context)
                    if (imageUrl == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Gagal upload gambar", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return@launch
                    }

                    val newUMKM = UMKM(
                        id = uid,
                        imageUMKM = imageUrl,
                        nameUMKM = name,
                        contact = contact,
                        location = location,
                        category = category,
                        description = description,
                        tags = category.split(",").map { it.trim() },
                        products = emptyList()
                    )

                    withContext(Dispatchers.Main) {
                        database.child("umkm").child(uid).setValue(newUMKM).addOnSuccessListener {
                            Toast.makeText(
                                context, "UMKM berhasil didaftarkan", Toast.LENGTH_SHORT
                            ).show()
                            onCreateSuccess()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Gagal mendaftar UMKM", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(stringResource(R.string.submit))
        }
    }
}