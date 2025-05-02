package com.example.busymate.ui.screen.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.OwnerSection
import com.example.busymate.ui.component.ProductCard
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DetailScreen(
    umkmId: String,
    nameUMKM: String,
    viewModel: DetailViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val context = LocalContext.current

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val umkm by viewModel.umkm.collectAsState()
    val products by viewModel.productList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    //fitur follow
    val ownerUser by viewModel.ownerUser.collectAsState()
    val isFollowing by viewModel.isFollowing.collectAsState()
    val followLoading by viewModel.followLoading.collectAsState()

    LaunchedEffect(umkmId) {
        viewModel.getUMKMById(umkmId)
        viewModel.getProductsByUMKM(umkmId)
        viewModel.fetchOwner(umkmId)
        currentUserId?.takeIf { it != umkmId }?.let { viewModel.checkFollowing(it, umkmId) }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!errorMessage.isNullOrEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        return
    }

    if (umkm == null) {
        Text(
            text = stringResource(R.string.umkm_not_found),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    fun openWhatsApp(
        context: Context,
        rawPhone: String,
        message: String = context.getString(R.string.message_chat)
    ) {
        val digits = rawPhone.filter { it.isDigit() }
        val withCountryCode = when {
            digits.startsWith("0") -> "62" + digits.drop(1)
            digits.startsWith("62") -> digits
            else -> "62$digits"
        }

        val encodedMessage = Uri.encode(message)

        val uri = "https://wa.me/$withCountryCode?text=$encodedMessage".toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    umkm?.let { data ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            ownerUser?.let { user ->
                OwnerSection(
                    user = user,
                    isFollowing = isFollowing,
                    isOwner = (currentUserId == data.id),
                    followLoading = followLoading,
                    onToggleFollow = { viewModel.toggleFollow(data.id) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            AsyncImage(
                model = umkm!!.imageUMKM,
                contentDescription = umkm!!.nameUMKM,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    nameUMKM,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(umkm!!.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    umkm!!.category.split(",").forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag.trim(), color = MaterialTheme.colorScheme.primary) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(umkm!!.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.product),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (products.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(products) { product ->
                            ProductCard(product = product)
                        }
                    }
                } else {
                    Text(stringResource(R.string.empty_product), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (umkm?.contact.isNullOrEmpty()) {
                            Toast.makeText(context, "Nomor WhatsApp tidak tersedia", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            openWhatsApp(
                                context = context,
                                rawPhone = umkm!!.contact,
                                message = context.getString(R.string.message_send, umkm!!.nameUMKM)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.connect))
                }
            }
        }
    }
}