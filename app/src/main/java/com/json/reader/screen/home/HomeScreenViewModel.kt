package com.json.reader.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.json.reader.data.DataOrException
import com.json.reader.model.MBook
import com.json.reader.repository.BookRepository
import com.json.reader.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRepository
): ViewModel(){

    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> = mutableStateOf(DataOrException(listOf(), true, Exception("")))

    init {
        getAllBooksFromDatabase()
    }

    private fun getAllBooksFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()
            if(!data.value.data.isNullOrEmpty()) data.value.loading = false
        }

    }
}