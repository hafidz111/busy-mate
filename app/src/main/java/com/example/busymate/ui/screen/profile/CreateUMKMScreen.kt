package com.example.busymate.ui.screen.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.model.UMKM
import com.example.busymate.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateUMKMScreen(
    modifier: Modifier,
    onCreateSuccess: () -> Unit
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.form_umkm), fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            selectedImageUri?.let {
                AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.Crop)
            } ?: Text(stringResource(R.string.choose_picture_umkm), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name_umkm)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text(stringResource(R.string.no_whatsapp)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(stringResource(R.string.location)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text(stringResource(R.string.category_create)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        database.child("umkm").child(uid).setValue(newUMKM)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "UMKM berhasil didaftarkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onCreateSuccess()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal mendaftar UMKM", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.submit))
        }
    }
}