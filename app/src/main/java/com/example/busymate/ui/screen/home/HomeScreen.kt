package com.example.busymate.ui.screen.home

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.busymate.R
import com.example.busymate.ui.component.CategoryChip
import com.example.busymate.ui.component.Search
import com.example.busymate.ui.component.UMKMCard
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val query by viewModel.query.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val umkmList by viewModel.umkmList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val filteredUMKM = umkmList.filter { umkm ->
        val categoryMatch = selectedCategory?.textCategory?.let {
            it.equals(stringResource(R.string.tag_all), true) || umkm.category.contains(it, ignoreCase = true)
        } ?: true

        val queryMatch = query.isBlank() || umkm.nameUMKM.contains(query, ignoreCase = true)

        categoryMatch && queryMatch
    }

    if (!errorMessage.isNullOrEmpty()) {
        Snackbar {
            Text(text = errorMessage ?: "")
        }
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
                    onQueryChange = { viewModel.onQueryChanged(it) },
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
                            viewModel.onCategorySelected(category)
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
                UMKMCard(
                    umkm = umkm,
                    onClick = { navController.navigate("detail/${umkm.id}/${umkm.nameUMKM}") },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}