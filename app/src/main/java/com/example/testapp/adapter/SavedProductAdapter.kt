package com.example.testapp.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.testapp.databinding.SavedProductItemBinding
import com.example.testapp.model.ProductEntity

class SavedProductAdapter(
    private var products: List<ProductEntity>,
    private val onRemoveClicked: (ProductEntity) -> Unit
) : RecyclerView.Adapter<SavedProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: SavedProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
            binding.apply {
                productName.text = product.name
                productPrice.text = "Price: ${product.price}"
                productQuantity.text = "Quantity: ${product.quantity}"

                productImage.load(product.image) {
                    crossfade(true)
                    placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    error(android.R.drawable.stat_notify_error)
                }

                removeProductImageButton.setOnClickListener {
                    val dialog = AlertDialog.Builder(binding.root.context)
                        .setTitle("Remove Product")
                        .setMessage("Are you sure you want to remove this product?")
                        .setPositiveButton("Yes") { _, _ ->
                            onRemoveClicked(product)
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
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            SavedProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
