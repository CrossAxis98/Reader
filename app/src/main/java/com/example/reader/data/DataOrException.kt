package com.example.reader.data

data class DataOrException<T, Boolean, Exception>(
    var data: T? = null,
    var loading: kotlin.Boolean? = null,
    var e: Exception? = null
)
