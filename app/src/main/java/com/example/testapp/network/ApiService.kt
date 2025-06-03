package com.example.testapp.network

import com.example.testapp.model.ProductDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("rest/V1/productdetails/{productId}/{variationId}")
    suspend fun getProductDetail(
        @Path("productId") productId: Int,
        @Path("variationId") variationId: Int,
        @Query("lang") lang: String,
        @Query("store") store: String
    ): ProductDetailResponse
}