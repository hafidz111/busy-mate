package com.example.busymate.ui.component

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.model.ProductItem

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

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close)
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialProduct == null) stringResource(R.string.add_product)
                    else stringResource(R.string.edit_product),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val displayUri = imageUri ?: initialProduct?.imageUrl
                if (displayUri != null) {
                    AsyncImage(
                        model = displayUri,
                        contentDescription = stringResource(R.string.image_product),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = stringResource(R.string.choose_image),
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name_product_text)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.price_product_text)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newProduct = ProductItem(
                        id = initialProduct?.id.orEmpty(),
                        name = name,
                        price = price.toIntOrNull() ?: 0,
                        imageUrl = initialProduct?.imageUrl.orEmpty()
                    )
                    onSubmit(newProduct, imageUri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (initialProduct == null) stringResource(R.string.add_product) else stringResource(
                        R.string.save
                    )
                )
            }
        }
    }
}