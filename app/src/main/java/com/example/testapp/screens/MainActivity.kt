package com.example.testapp.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.testapp.adapter.CircleIndicatorAdapter
import com.example.testapp.adapter.ColorAdapter
import com.example.testapp.adapter.ImageSliderAdapter
import com.example.testapp.database.AppDatabase
import com.example.testapp.databinding.ActivityMainBinding
import com.example.testapp.model.ProductDetailResponse
import com.example.testapp.network.RetrofitClient
import com.example.testapp.repository.ProductRepository
import com.example.testapp.viewModel.ProductViewModel
import com.example.testapp.viewModel.ProductViewModelFactory
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var circleIndicatorAdapter: CircleIndicatorAdapter
    private lateinit var colorAdapter: ColorAdapter

    private var currentPosition: Int = 0
    private var count: Int = 1

    private var isProductInCart = false
    private var productDetailResponse: ProductDetailResponse? = null

    private var productId: Int = 6701
    private var variationId: Int = 253620

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_database"
        ).build()

        val repository = ProductRepository(database.productDao())

        val factory = ProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        observeViewModel()

        viewModel.fetchProductDetails(productId, variationId)
    }

    private fun setupListeners() {
        binding.minusImageButton.setOnClickListener {
            if (count > 1) {
                count--
                binding.countTextView.text = count.toString()
            }
        }

        binding.plusImageButton.setOnClickListener {
            count++
            binding.countTextView.text = count.toString()
        }

        var isExpanded = false

        binding.moreImageButton.setOnClickListener {
            isExpanded = !isExpanded
            binding.descriptionTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.grayLineView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.moreImageButton.rotation = if (isExpanded) 90f else 270f
        }

        binding.shareImageButton.setOnClickListener {
            openShareBottomSheet()
        }

        binding.shareButton.setOnClickListener {
            openShareBottomSheet()
        }

        binding.backImageButton.setOnClickListener {
            val dialog = AlertDialog.Builder(binding.root.context)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                .setNegativeButton("No", null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(android.graphics.Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(android.graphics.Color.BLACK)
            }

            dialog.show()
        }

        binding.addToBagButton.setOnClickListener {
            if (isProductInCart) {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
                productDetailResponse?.let { response ->
                    viewModel.addProduct(response.data, count)
                    Toast.makeText(this, "Added to bag", Toast.LENGTH_SHORT).show()
                    isProductInCart = true
                    binding.addToBagButton.text = "Go to bag"
                } ?: Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.cartImageButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productData.collect { response ->
                response?.let {
                    productDetailResponse = it
                    setValues(it)
                    checkIfProductInCart()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    binding.errorTextView.text = "Error: $it"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                if (loading) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                    binding.progressOverlay.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = ProgressBar.GONE
                    binding.progressOverlay.visibility = View.GONE
                }
            }
        }
    }

    private fun openShareBottomSheet() {
        val baseUrl = RetrofitClient.BASE_URL.toString()
        val url = "rest/V1/productdetails/$productId/$variationId"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$baseUrl$url")
        }

        val chooser = Intent.createChooser(shareIntent, "Share via")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(chooser)
    }

    private fun setValues(it: ProductDetailResponse) {
        binding.nameTextView.text = it.data.name
        binding.productNameTextView.text = it.data.name
        binding.brandNameTextView.text = it.data.brand_name.uppercase()
        binding.skuTextView.text = "SKU: ${it.data.sku}"

        val rawPrice = it.data.price.toDoubleOrNull() ?: 0.0
        val decimalFormat = DecimalFormat("#.00")
        val formattedPrice = decimalFormat.format(rawPrice)
        binding.priceTextView.text = "$formattedPrice KWD"

        imageSliderAdapter = ImageSliderAdapter(it.data.images)
        binding.viewPager.adapter = imageSliderAdapter

        circleIndicatorAdapter = CircleIndicatorAdapter(it.data.images, currentPosition)
        binding.circleIndicators.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.circleIndicators.adapter = circleIndicatorAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                circleIndicatorAdapter.updateSelectedPosition(position)
            }
        })

        colorAdapter = ColorAdapter(it.data.configurable_option)
        binding.colorRecyclerView.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.colorRecyclerView.adapter = colorAdapter

        val plainText = it.data.description
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("(?m)^[ \t]*\r?\n"), "")
        binding.descriptionTextView.text = plainText
    }

    private fun checkIfProductInCart() {
        lifecycleScope.launch {
            productDetailResponse?.data?.let { product ->
                val existingProduct = viewModel.getProductById(product.id)
                isProductInCart = existingProduct != null
                if (isProductInCart) {
                    binding.addToBagButton.text = "Go to bag"
                } else {
                    binding.addToBagButton.text = "Add to bag"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkIfProductInCart()
    }
}