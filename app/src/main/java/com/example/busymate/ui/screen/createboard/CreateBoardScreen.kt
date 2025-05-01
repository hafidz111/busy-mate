package com.example.busymate.ui.screen.createboard

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
import com.example.busymate.model.Board
import com.example.busymate.model.UMKM
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.FormBoard
import com.example.busymate.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateBoardScreen(
    modifier: Modifier,
    onCreateSuccess: () -> Unit,
    viewModel: CreateBoardViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val created by viewModel.created.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    LaunchedEffect(created) {
        if (created) {
            onCreateSuccess()
        }
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
        Text(
            stringResource(R.string.form_board),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        FormBoard(
            description = description,
            onDescriptionChange = { description = it },
            selectedImageUri = selectedImageUri,
            onImageClick = { imagePickerLauncher.launch("image/*") },
            imageUrl = "",
            isPrivate = isPrivate,
            onPrivacyChange = { isPrivate = it }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    Toast.makeText(context, "Login dulu ya, Boss!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val imageUrl = selectedImageUri?.let { uri ->
                        uploadImage(uri, context) ?: ""
                    } ?: ""

                    if (selectedImageUri != null && imageUrl.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Gagal upload gambarnya", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return@launch
                    }

                    val umkm = UMKM(id = uid)

                    val board = Board(
                        id = "",
                        description = description,
                        umkm = umkm,
                        imageUrl = imageUrl,
                        isPrivate = isPrivate,
                        timestamp = System.currentTimeMillis()
                    )

                    withContext(Dispatchers.Main) {
                        viewModel.createBoard(board)
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