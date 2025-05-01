package com.example.busymate.ui.screen.manageproduct

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.busymate.R
import com.example.busymate.common.UiState
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.ProductItem
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.ProductForm
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

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
                                                .padding(vertical = 6.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .padding(8.dp)
                                                ) {
                                                    IconButton(onClick = { onEdit(product) }) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = stringResource(R.string.edit_product),
                                                            tint = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                    IconButton(onClick = {
                                                        viewModel.deleteProduct(userId, product.id)
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = stringResource(R.string.delete_product),
                                                            tint = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp)
                                                ) {
                                                    AsyncImage(
                                                        model = product.imageUrl,
                                                        contentDescription = stringResource(R.string.image_product),
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(90.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color.LightGray)
                                                    )

                                                    Spacer(modifier = Modifier.width(16.dp))

                                                    Column(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .align(Alignment.CenterVertically)
                                                    ) {
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
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }
                onDismissForm()
            },
            sheetState = bottomSheetState,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                ProductForm(
                    initialProduct = editingProduct,
                    onSubmit = { product, imageUri ->
                        if (editingProduct == null) {
                            viewModel.addProduct(context, userId, product, imageUri)
                        } else {
                            viewModel.editProduct(userId, product)
                        }
                        scope.launch { bottomSheetState.hide() }
                        onDismissForm()
                    },
                    onCancel = {
                        scope.launch { bottomSheetState.hide() }
                        onDismissForm()
                    }
                )
            }
        }
    }
}