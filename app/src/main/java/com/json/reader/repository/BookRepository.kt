package com.json.reader.repository

import android.util.Log
import com.json.reader.data.Resource
import com.json.reader.model.Item
import com.json.reader.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: BooksApi
) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {

        return try {
            Resource.Loading(data = true)

            val itemList = api.getAllBooks(searchQuery).items
            if (itemList.isNotEmpty()) Resource.Loading(data = false)
            Resource.Success(data = itemList)

        }catch (exception: Exception) {
            Resource.Error(message = exception.message.toString())
        }

    }

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        val response = try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId)

        }catch (exception: Exception){
            return Resource.Error(message = "An error occurred ${exception.message.toString()}")
        }
        Resource.Loading(data = false)
        return Resource.Success(data = response)
    }
}