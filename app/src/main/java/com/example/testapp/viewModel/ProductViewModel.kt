package com.example.testapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Data
import com.example.testapp.model.ProductDetailResponse
import com.example.testapp.model.ProductEntity
import com.example.testapp.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _productData = MutableStateFlow<ProductDetailResponse?>(null)
    val productData: StateFlow<ProductDetailResponse?> = _productData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _savedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val savedProducts: StateFlow<List<ProductEntity>> = _savedProducts

    fun fetchProductDetails(productId: Int, variationId: Int) {
        Log.d("my Response", "here")
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = repository.getProductDetails(productId, variationId)
                _productData.value = response
                Log.d("my Response success", response.status.toString())
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Log.d("my Response catch", e.toString())
            } finally {
                _isLoading.value = false
                Log.d("my Response loading", "isLoading")
            }
        }
    }

    fun addProduct(data: Data, count: Int) {
        val product = ProductEntity(
            id = data.id,
            name = data.name,
            image = data.image,
            price = data.price,
            quantity = count
        )

        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun fetchSavedProducts() {
        viewModelScope.launch {
            try {
                val products = repository.getAllProducts()
                _savedProducts.value = products
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteProductById(productId: String) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
            fetchSavedProducts()
        }
    }

    fun getProductById(productId: String): ProductEntity? {
        var product: ProductEntity? = null
        viewModelScope.launch {
            product = repository.getProductById(productId)
        }
        return product
    }
}