package com.example.busymate.ui.component

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.busymate.model.ProductItem
import java.util.UUID

@SuppressLint("UseKtx")
@Composable
fun ProductForm(
    initialProduct: ProductItem? = null,
    onSubmit: (ProductItem, Uri?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var price by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }

    val initialImageUri =
        initialProduct?.imageUrl?.takeIf { it.isNotBlank() }?.let { Uri.parse(it) }
    var imageUri by remember { mutableStateOf(initialImageUri) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pilih Gambar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Produk") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Harga") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (name.isNotBlank() && price.toIntOrNull() != null) {
                        val product = ProductItem(
                            id = initialProduct?.id ?: UUID.randomUUID().toString(),
                            name = name,
                            imageUrl = initialProduct?.imageUrl ?: "",
                            price = price.toInt()
                        )
                        onSubmit(product, imageUri)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (initialProduct == null) "Tambah" else "Simpan")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Batal")
            }
        }
    }
}