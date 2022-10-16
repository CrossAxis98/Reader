package com.example.reader.repository

import android.util.Log
import com.example.reader.data.DataOrException
import com.example.reader.model.MBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(
    private val queryBook: Query
){
    suspend fun getAllBooksFromDatabase(): DataOrException<List<MBook>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MBook>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            val db = FirebaseFirestore.getInstance()
            db.collection("books")
                .get()
                .addOnSuccessListener { documents ->
                    dataOrException.data = documents.map { documentSnapshot ->
                        documentSnapshot.toObject(MBook::class.java)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("FireRepository", "Error getting documents: ", exception)
                }
                .await()
            if (!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        } catch (exception: FirebaseFirestoreException) {
            dataOrException.loading = false
            dataOrException.e = exception
        }
        return dataOrException
    }
}