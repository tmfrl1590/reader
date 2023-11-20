package com.json.reader.screen.detail

import androidx.lifecycle.ViewModel
import com.json.reader.data.Resource
import com.json.reader.model.Item
import com.json.reader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel(){

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}