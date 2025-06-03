package com.example.testapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testapp.dao.ProductDao
import com.example.testapp.model.ProductEntity

@Database(entities = [ProductEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}