package com.example.reader.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.reader.components.InputField
import com.example.reader.components.ReaderAppBar
import com.example.reader.model.Item
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun SearchScreen(navController: NavController,
                 viewModel: BookSearchViewModel = hiltViewModel() ) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Search Books",
            showProfile = false,
            navController = navController,
            icon = Icons.Default.ArrowBack
        ) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {
        Surface() {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel
                ) { query ->
                    viewModel.searchBooks(query)
                 }
                Spacer(modifier = Modifier.height(14.dp))
                BookList(navController, viewModel)
            }
        }
    }
}

@Composable
private fun BookList(navController: NavController,
                     viewModel: BookSearchViewModel = hiltViewModel()) {

    val listOfBooks = viewModel.list
    if (viewModel.isLoading) {
        LinearProgressIndicator()
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listOfBooks) { book ->
                RowInSearch(book, navController)
            }
        }
    }
}

@Composable
fun RowInSearch(book: Item, navController: NavController) {
    Surface(
        modifier = Modifier
            .padding(
                horizontal = 26.dp,
                vertical = 6.dp
            )
            .fillMaxWidth()
            .clickable {

            },
        elevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl: String = if(book.volumeInfo.imageLinks == null)
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else {
                book.volumeInfo.imageLinks.smallThumbnail
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
                Text(
                    text = book.volumeInfo.title,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Author: " + book.volumeInfo.authors,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = "Date: " + book.volumeInfo.publishedDate,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = "[${book.volumeInfo.categories}]",
                    style = MaterialTheme.typography.caption,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    viewModel: BookSearchViewModel,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
) {
    Column {
        val searchQueryState = rememberSaveable() { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState) {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(valueState = searchQueryState, labelId = "Search", enabled = true, onAction = KeyboardActions {
            onSearch(searchQueryState.value.trim())
            searchQueryState.value = ""
            keyboardController?.hide()
        } )
    }

}