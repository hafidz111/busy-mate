package com.example.busymate.ui.screen.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.model.Category
import com.example.busymate.ui.component.CategoryChip
import com.example.busymate.ui.component.ProductCard
import com.google.firebase.database.DatabaseError

@SuppressLint("UseKtx")
@Composable
fun ProfileUMKMScreen(
    modifier: Modifier = Modifier, navController: NavController
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance().reference
    var umkmData by remember { mutableStateOf<UMKM?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            database.child("umkm").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.getValue(UMKM::class.java)
                        umkmData = data
                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                })
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        umkmData?.let { data ->
            val imageUri = remember(data.imageUMKM) {
                runCatching { Uri.parse(data.imageUMKM) }.getOrNull()
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Log.d("UMKM", "Image URL: ${data.imageUMKM}")

                Spacer(modifier = Modifier.height(16.dp))

                Text(data.nameUMKM, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(data.location, fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    data.category.split(",").forEach {
                        CategoryChip(
                            category = Category(textCategory = it),
                            isSelected = false,
                            onClick = {})
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.description), fontWeight = FontWeight.Bold)
                Text(data.description)

                Text(stringResource(R.string.no_whatsapp), fontWeight = FontWeight.Bold)
                Text(data.contact)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    stringResource(R.string.product), fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                if (data.products.isEmpty()) {
                    Text(stringResource(R.string.empty_product), color = Color.Gray)
                } else {
                    LazyRow {
                        items(data.products) { product ->
                            ProductCard(product = product)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("edit_umkm/${data.id}")
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.edit))
                }
            }
        } ?: run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.empty_store))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate("create_umkm")
                }) {
                    Text(stringResource(R.string.register_umkm))
                }
            }
        }
    }
}