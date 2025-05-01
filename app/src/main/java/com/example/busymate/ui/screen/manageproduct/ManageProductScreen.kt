package com.example.busymate.ui.screen.manageproduct

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.common.UiState
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.ProductItem
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.ProductForm
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ManageProductScreen(
    userId: String,
    showForm: Boolean,
    editingProduct: ProductItem?,
    onEdit: (ProductItem) -> Unit,
    onDismissForm: () -> Unit,
    onNavigateToCreateUMKM: () -> Unit,
    viewModel: ManageProductViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val productState by viewModel.products.collectAsState()
    val storeState by viewModel.hasStore.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.checkHasStore(userId)
        viewModel.fetchProducts(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = storeState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is UiState.Success -> {
                if (!state.data) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.no_store_yet),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onNavigateToCreateUMKM) {
                                Text(stringResource(R.string.create_umkm))
                            }
                        }
                    }
                } else {
                    when (val productStateValue = productState) {
                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is UiState.Success -> {
                            val products = productStateValue.data
                            if (products.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.empty_product),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                LazyColumn {
                                    items(products) { product ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(4.dp)
                                        ) {
                                            Row(modifier = Modifier.padding(16.dp)) {
                                                AsyncImage(
                                                    model = product.imageUrl,
                                                    contentDescription = stringResource(R.string.image_product),
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        stringResource(
                                                            R.string.name_product,
                                                            product.name
                                                        ),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Text(
                                                        stringResource(
                                                            R.string.price_product,
                                                            product.price
                                                        )
                                                    )
                                                }

                                                Row {
                                                    IconButton(onClick = { onEdit(product) }) {
                                                        Icon(
                                                            Icons.Default.Edit,
                                                            stringResource(R.string.edit_product)
                                                        )
                                                    }
                                                    IconButton(onClick = {
                                                        viewModel.deleteProduct(userId, product.id)
                                                    }) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            stringResource(R.string.delete_product)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = productStateValue.errorMessage,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        Dialog(onDismissRequest = onDismissForm) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (editingProduct == null) stringResource(R.string.add_product) else stringResource(
                            R.string.edit_product
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProductForm(
                        initialProduct = editingProduct,
                        onSubmit = { product, imageUri ->
                            if (editingProduct == null) {
                                viewModel.addProduct(context, userId, product, imageUri)
                            } else {
                                viewModel.editProduct(userId, product)
                            }
                            onDismissForm()
                        },
                        onCancel = onDismissForm
                    )
                }
            }
        }
    }
}