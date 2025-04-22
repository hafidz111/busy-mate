package com.example.busymate.ui.screen.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.busymate.model.Category
import com.example.busymate.model.UMKM
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.busymate.R
import com.example.busymate.ui.component.CategoryChip
import com.example.busymate.ui.component.Search
import com.example.busymate.ui.component.UMKMCardVertical
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.LaunchedEffect

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var umkmList by remember { mutableStateOf<List<UMKM>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(true) {
        val database = FirebaseDatabase.getInstance().reference

        database.child("categories").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                list.add(Category(id = 0, textCategory = context.getString(R.string.tag_all)))

                for (catSnap in snapshot.children) {
                    val category = catSnap.getValue(Category::class.java)
                    category?.let { list.add(it) }
                }

                categories = list
                selectedCategory = list.firstOrNull()
                Log.d("Firebase", "Categories loaded: ${categories.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
            }
        })

        database.child("umkm").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UMKM>()
                for (umkmSnap in snapshot.children) {
                    val umkm = umkmSnap.getValue(UMKM::class.java)
                    umkm?.let { list.add(it) }
                }
                umkmList = list
                isLoading = false
                Log.d("Firebase", "UMKM loaded: ${umkmList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data UMKM", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    val filteredUMKM = umkmList.filter { umkm ->
        val categoryMatch = selectedCategory?.textCategory?.let {
            if (it.equals(stringResource(R.string.tag_all), true)) true
            else umkm.category.contains(it, ignoreCase = true)
        } ?: true

        val queryMatch = query.isBlank() || umkm.nameUMKM.contains(query, ignoreCase = true)

        categoryMatch && queryMatch
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box {
                Image(
                    painter = painterResource(R.drawable.banner),
                    contentDescription = "Banner Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
                Search(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }

        item {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    CategoryChip(
                        category = category,
                        isSelected = isSelected,
                        onClick = {
                            selectedCategory = category
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.list_umkm),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (filteredUMKM.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.empty_umkm), style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            items(filteredUMKM) { umkm ->
                UMKMCardVertical(
                    umkm = umkm,
                    onClick = { navController.navigate("detail/${umkm.id}/${umkm.nameUMKM}") },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}