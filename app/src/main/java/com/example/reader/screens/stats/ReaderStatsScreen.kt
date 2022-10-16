package com.example.reader.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.reader.components.ReaderAppBar
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.home.HomeScreenViewModel
import com.example.reader.utils.formatDate
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun ReaderStatsScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Stats",
            navController = navController,
            icon = Icons.Default.ArrowBack,
            showProfile = false
        ) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {
        Surface() {
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }
            Column {
                Row {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)) {
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }
                    Text(text = "Hi, ${
                        currentUser?.email.toString().split("@")[0 ]
                            .uppercase(Locale.getDefault())
                    }")
                }
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = CircleShape,
                elevation = 5.dp) {
                    val readBook: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                        books.filter { mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }
                    } else {
                        emptyList()
                    }
                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }

                    Column(
                        modifier = Modifier.padding(
                            start = 25.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Your Stats",
                            style = MaterialTheme.typography.h5
                        )
                        Divider()

                        Text(text = "You're reading: ${readingBooks.size} book/books")
                        Text(text = "You've read: ${readBook.size} book/books")
                    }
                }
                if (viewModel.data.value.loading == true) {
                    LinearProgressIndicator()
                } else {
                    Divider()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val readBoks: List<MBook> =
                            if (!viewModel.data.value.data.isNullOrEmpty()) {
                            viewModel.data.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }
                        items(items = readBoks) { book ->
                            BookRowStats(book = book)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookRowStats(book: MBook) {
    Surface(
        modifier = Modifier
            .padding(
                horizontal = 26.dp,
                vertical = 6.dp
            )
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl: String = if(book.photoUrl.toString() == null)
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else {
                book.photoUrl.toString()
            }
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "Book Image",
                modifier = Modifier
                    .height(140.dp)
                    .width(100.dp)
                    .padding(4.dp)
            )
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text(
                        text = book.title.toString(),
                        overflow = TextOverflow.Ellipsis
                    )
                    if (book.rating!! >= 4) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Thumb up",
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else {
                        Box {

                        }
                    }
                }
                Text(
                    text = "Author: " + book.authors,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = "Started: " + formatDate(book.startedReading!!),
                    softWrap = true,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = "Finished: " + formatDate(book.finishedReading!!),
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}
