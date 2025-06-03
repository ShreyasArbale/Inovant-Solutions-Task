package com.example.testapp.screens

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.testapp.adapter.SavedProductAdapter
import com.example.testapp.database.AppDatabase
import com.example.testapp.databinding.ActivityCartBinding
import com.example.testapp.repository.ProductRepository
import com.example.testapp.viewModel.ProductViewModel
import com.example.testapp.viewModel.ProductViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: SavedProductAdapter
    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backImageButton.setOnClickListener {
            finish()
        }

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_database"
        ).build()

        val repository = ProductRepository(database.productDao())

        val factory = ProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        adapter = SavedProductAdapter(emptyList()) { product ->
            viewModel.deleteProductById(product.id)
        }
        binding.savedProductRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.savedProductRecyclerView.adapter = adapter

        viewModel.fetchSavedProducts()

        lifecycleScope.launch {
            viewModel.savedProducts.collectLatest { products ->
                adapter.updateProducts(products)

                if (products.isEmpty()){
                    binding.savedProductRecyclerView.visibility = View.GONE
                    binding.noProductTextView.visibility = View.VISIBLE
                }else{
                    binding.savedProductRecyclerView.visibility = View.VISIBLE
                    binding.noProductTextView.visibility = View.GONE
                }
            }
        }
    }
}