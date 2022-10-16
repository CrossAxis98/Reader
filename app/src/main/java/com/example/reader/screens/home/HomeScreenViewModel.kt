package com.example.reader.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.DataOrException
import com.example.reader.model.MBook
import com.example.reader.repository.FireRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRepository
): ViewModel() {
    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>>
    = mutableStateOf(DataOrException(listOf(), true,Exception("")))

    init {
        getAllBooksFromDataBase()
    }

    private fun getAllBooksFromDataBase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()
            Log.d("TAG", "ViewModel: ${data.value.data}")
            if (!data.value.data.isNullOrEmpty()) data.value.loading = false
        }
        Log.d("TAG", "getAllBooksFromDataBase: ${data.value.data?.toString()}")
    }
}