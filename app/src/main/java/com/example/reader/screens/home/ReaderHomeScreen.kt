package com.example.reader.screens.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.reader.components.FABContent
import com.example.reader.components.ListCard
import com.example.reader.components.ReaderAppBar
import com.example.reader.components.TitleSection
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navController: NavController,
         viewModel: HomeScreenViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
                               FABContent() {
                                    navController.navigate(ReaderScreens.SearchScreen.name)
                               }
        },topBar = {
        ReaderAppBar(
            title = "Reader",
            navController = navController
        )
    }) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) ) {
            HomeContent(navController, viewModel)
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    var listOfBooks by remember {
        mutableStateOf(emptyList<MBook>())
    }
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (!viewModel.data.value.data.isNullOrEmpty()) {
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid
        }
    }
//    val listOfBooks = listOf(
//        MBook(id = "222", title = "W pustyni i w puszczy", authors = "Henryk Sienkiewicz", notes = null),
//        MBook(id = "223", title = "Janko Muzykant", authors = "Henryk Sienkiewicz", notes = null),
//        MBook(id = "224", title = "Pan Tadeusz", authors = "Adam Mickiewicz", notes = null),
//        MBook(id = "225", title = "Krzyżacy", authors = "Henryk Sienkiewicz", notes = null),
//        MBook(id = "226", title = "Unscripted: Życie, wolność, przedsiębiorczość", authors = "MJ DeMarco", notes = null)
//    )
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty()) {
        email.split("@")[0]
    } else "N/A"
    Column(
        Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading \n activity right now")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = currentUserName,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.overline,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Divider()
            }
        }
        ReadingRightNowArea(listOfBooks = listOfBooks, navController = navController)
        TitleSection(label = "Reading List")
        BookListArea(
            listOfBooks = listOfBooks,
            navController
        )
    }
}

@Composable
fun ReadingRightNowArea(listOfBooks: List<MBook>, navController: NavController) {
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(readingNowList) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {

    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(addedBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(280.dp)
        .horizontalScroll(scrollState)) {

        if (viewModel.data.value.loading == true) {
            LinearProgressIndicator()
        } else {
            if (listOfBooks.isNullOrEmpty()) {
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(
                        text = "No books found. Add a Book",
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                for (book in listOfBooks) {
                    ListCard(book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }


    }
    
}
