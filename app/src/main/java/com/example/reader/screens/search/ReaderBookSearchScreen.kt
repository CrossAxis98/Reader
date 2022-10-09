package com.example.reader.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.reader.components.InputField
import com.example.reader.components.ReaderAppBar
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens

@Preview
@Composable
fun SearchScreen(navController: NavController = NavController(LocalContext.current)) {
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
                SearchForm(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                 }
                Spacer(modifier = Modifier.height(14.dp))
                BookList(navController)
            }
        }
    }
}

@Composable
private fun BookList(navController: NavController) {
    val listOfBooks = listOf(
        MBook(id = "222", title = "W pustyni i w puszczy", authors = "Henryk Sienkiewicz", notes = null),
        MBook(id = "223", title = "Janko Muzykant", authors = "Henryk Sienkiewicz", notes = null),
        MBook(id = "224", title = "Pan Tadeusz", authors = "Adam Mickiewicz", notes = null),
        MBook(id = "225", title = "Krzyżacy", authors = "Henryk Sienkiewicz", notes = null),
        MBook(id = "226", title = "Unscripted: Życie, wolność, przedsiębiorczość", authors = "MJ DeMarco", notes = null)
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(listOfBooks) { book ->
            RowInSearch(book)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RowInSearch(book: MBook = MBook(id = "222", title = "W pustyni i w puszczy", authors = "Henryk Sienkiewicz", notes = null)) {
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
            Image(
                painter = rememberImagePainter(data = "http://books.google.com/books/content?id=JGH0DwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"),
                contentDescription = "Book Image",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .padding(4.dp)
            )
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = book.title.toString(),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Author: " + book.authors.toString(),
                    style = MaterialTheme.typography.caption,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = "Date: 2020-09-09",
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = "[Computers]",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
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