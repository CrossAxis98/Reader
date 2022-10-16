package com.example.reader.screens.details

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.reader.components.ReaderAppBar
import com.example.reader.components.RoundedButton
import com.example.reader.data.Resource
import com.example.reader.model.Item
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Details",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        val scrollState = rememberScrollState()
        Surface(modifier = Modifier
            .padding(3.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)) {
            Column(modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

                val bookInfo: Resource<Item> = produceState<Resource<Item>>(initialValue = Resource.Loading()){
                    value = viewModel.getBookInfo(bookId)
                }.value
                if (bookInfo.data == null) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Loading...")
                        LinearProgressIndicator()
                    }
                } else {
                    ShowBookDetails(bookInfo, navController)
                }
            }
        }
    }

}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    val scrollState = rememberScrollState()
    val description = if (bookInfo.data!!.volumeInfo.description == null) {
        "No description provided"
    } else {
        HtmlCompat.fromHtml(bookInfo.data.volumeInfo.description,
            HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }
    val imageUrl: String = if (bookInfo.data?.volumeInfo?.imageLinks == null)
        "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
    else {
        bookInfo.data.volumeInfo.imageLinks.smallThumbnail
    }
    Surface(modifier = Modifier
        .padding(15.dp)
        .fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookImage(imageUrl)
            Text(
                text = bookInfo.data.volumeInfo.title,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = "Author: " + bookInfo.data.volumeInfo.authors,
                style = MaterialTheme.typography.caption,
                overflow = TextOverflow.Clip
            )
            Text(
                text = "Page Count: " + bookInfo.data.volumeInfo.pageCount,
                style = MaterialTheme.typography.caption,
                overflow = TextOverflow.Clip
            )
            Text(
                text = "[${bookInfo.data.volumeInfo.categories}]",
                overflow = TextOverflow.Clip
            )
            Text(
                text = "Published: ${bookInfo.data.volumeInfo.publishedDate}]",
                style = MaterialTheme.typography.caption,
                overflow = TextOverflow.Clip
            )

            val localDims = LocalContext.current.resources.displayMetrics

            Surface(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(localDims.heightPixels.dp.times(0.09f)),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Gray
                )
            ) {
                LazyColumn(Modifier.padding(3.dp)) {
                    item {
                        Text(text = description)
                    }
                }
            }
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            RoundedButton(label = "Save") {
                val categories = if (bookInfo.data.volumeInfo.categories == null) {
                    "No category provided"
                } else {
                    bookInfo.data.volumeInfo.categories.toString()
                }
                val book = MBook(
                    title = bookInfo.data.volumeInfo.title,
                    authors = bookInfo.data.volumeInfo.authors.toString(),
                    description = bookInfo.data.volumeInfo.description,
                    categories = categories,
                    notes = "",
                    photoUrl = imageUrl,
                    publishedDate = bookInfo.data.volumeInfo.publishedDate,
                    pageCount = bookInfo.data.volumeInfo.pageCount.toString(),
                    rating = 0.0,
                    googleBookId = bookInfo.data.id,
                    userId = FirebaseAuth.getInstance().currentUser?.uid
                )
                saveToFirebase(book, navController)
            }
            Spacer(modifier = Modifier.width(50.dp))
            RoundedButton(label = "Cancel")
        }
        }
    }
}

fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                        }
                    }.addOnFailureListener {
                        Log.w("TAG", "SaveToFirebase: Error updating doc ", it)
                    }
            }
    }
}

@Composable
private fun BookImage(
    imageUrl: String
) {
    Card(
        shape = CircleShape,
        elevation = 4.dp,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .width(100.dp)
            .height(100.dp)
    ) {

        Image(
            painter = rememberImagePainter(data = imageUrl),
            contentDescription = "Book Image"
        )
    }
}
