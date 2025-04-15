package com.example.busymate.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.busymate.model.Category
import com.example.busymate.model.UMKM
import com.example.busymate.model.dummyCategory
import com.example.busymate.model.dummyUMKM
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.busymate.R
import com.example.busymate.ui.component.CategoryChip
import com.example.busymate.ui.component.Search
import com.example.busymate.ui.component.UMKMCardHorizontal
import com.example.busymate.ui.component.UMKMCardVertical

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    categories: List<Category> = dummyCategory,
    umkmList: List<UMKM> = dummyUMKM,
    onUMKMClick: (UMKM) -> Unit = {},
    onCategorySelected: (Category) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(categories.first()) }
    val filteredUMKM = umkmList.filter { umkm ->
        val categoryMatch = if (selectedCategory?.textCategory == R.string.tag_all) {
            true
        } else {
            val selectedCategoryString = stringResource(id = selectedCategory!!.textCategory)
            stringResource(id = umkm.category) == selectedCategoryString
        }

        val queryMatch = query.isBlank() || stringResource(id = umkm.name)
            .contains(query, ignoreCase = true)

        categoryMatch && queryMatch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
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
                            onCategorySelected(category)
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.top_umkm),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUMKM) { umkm ->
                    UMKMCardHorizontal(
                        umkm = umkm,
                        onClick = { onUMKMClick(umkm) },
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

        items(filteredUMKM) { umkm ->
            UMKMCardVertical(
                umkm = umkm,
                onClick = { onUMKMClick(umkm) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}