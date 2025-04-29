package com.example.busymate.ui.screen.create

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busymate.R
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.UMKM
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.FormUMKM
import com.example.busymate.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateUMKMScreen(
    modifier: Modifier,
    onCreateSuccess: () -> Unit,
    viewModel: CreateUMKMViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val created by viewModel.created.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0L) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    LaunchedEffect(created) {
        if (created) onCreateSuccess()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
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
            price = price.toString(),
            onPriceChange = { price = it.toLong() },
            selectedImageUri = selectedImageUri,
            onImageClick = { imagePickerLauncher.launch("image/*") },
            imageUrl = ""
        )

        Button(
            onClick = {
                if (selectedImageUri == null) {
                    Toast.makeText(context, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                CoroutineScope(Dispatchers.IO).launch {
                    val imageUrl = uploadImage(selectedImageUri!!, context)

                    if (imageUrl == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Gagal upload gambar", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return@launch
                    }

                    val validatedPrice = if (price.toString().isBlank() ){ 0 } else { price.toLong() }
                    val umkm = UMKM(
                        id = uid,
                        imageUMKM = imageUrl,
                        nameUMKM = name,
                        contact = contact,
                        location = location,
                        category = category,
                        description = description,
                        price = validatedPrice,
                        tags = category.split(",").map(String::trim),
                        products = emptyList()
                    )

                    withContext(Dispatchers.Main) {
                        viewModel.createUMKM(umkm)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text(stringResource(R.string.submit))
            }
        }
    }
}