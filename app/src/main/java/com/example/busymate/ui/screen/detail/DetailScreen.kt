package com.example.busymate.ui.screen.detail

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.model.UMKM
import com.example.busymate.ui.component.ProductCard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun DetailScreen(
    umkmId: Int
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    var umkm by remember { mutableStateOf<UMKM?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(umkmId) {
        database.child("umkm")
            .orderByChild("id")
            .equalTo(umkmId.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            umkm = child.getValue(UMKM::class.java)
                            break
                        }
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            })
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
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

    fun openWhatsApp(context: Context, phoneNumber: String) {
        val uri = "https://wa.me/$phoneNumber".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = umkm!!.imageUMKM,
            contentDescription = umkm!!.nameUMKM,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(umkm!!.nameUMKM, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(umkm!!.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                umkm!!.category.split(",").forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag, color = MaterialTheme.colorScheme.primary) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Deskripsi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(umkm!!.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            if (umkm!!.products.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(umkm!!.products) { product ->
                        ProductCard(product = product)
                    }
                }
            } else {
                Text("Belum ada produk.", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    umkm?.contact?.let { phone ->
                        openWhatsApp(context, phone)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hubungi via WhatsApp")
            }
        }
    }
}