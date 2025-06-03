package com.example.testapp.repository

import com.example.testapp.dao.ProductDao
import com.example.testapp.model.ProductDetailResponse
import com.example.testapp.model.ProductEntity
import com.example.testapp.network.RetrofitClient

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductDetails(productId: Int, variationId: Int): ProductDetailResponse {
        return RetrofitClient.instance.getProductDetail(
            productId = productId,
            variationId = variationId,
            lang = "en",
            store = "KWD"
        )
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun getAllProducts(): List<ProductEntity> {
        return productDao.getAllProducts()
    }

    suspend fun deleteProductById(productId: String) {
        productDao.deleteProductById(productId)
    }

    suspend fun getProductById(productId: String): ProductEntity? {
        return productDao.getProductById(productId)
    }
}