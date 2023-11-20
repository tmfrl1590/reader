package com.json.reader.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.json.reader.components.FABContent
import com.json.reader.components.ListCard
import com.json.reader.components.ReaderAppBar
import com.json.reader.components.TitleSection
import com.json.reader.model.MBook
import com.json.reader.navigation.ReaderScreens

@Composable
fun Home(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel()) {

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "A.Reader",
                navController = navController
            )
        },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeContent(navController, viewModel)
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {

    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser
    if(!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
    }

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName =
        if(!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty())
            FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
        else "N/A"
    Column(
        Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading \n " + "activity right now")

            Spacer(modifier = Modifier.fillMaxWidth(0.7f))

            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(44.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currentUserName!!,
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Red,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )

                Divider()
            }
        }


        ReadingRightNowArea(
            listOfBooks = listOf(),
            navController = navController
        )

        TitleSection(label = "Reading List")

        BookListArea(
            listOfBooks = listOfBooks,
            navController = navController
        )
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {

    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(listOfBooks){
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ){

        if(viewModel.data.value.loading == true){
            LinearProgressIndicator()
        }else{
            if(listOfBooks.isNullOrEmpty()){
                Surface(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "No Books found. Add a Book",
                        style = TextStyle(color = Color.Red.copy(alpha = 0.4f )),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }else{
                for(book in listOfBooks){
                    ListCard(book){
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }
    }
}




@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController: NavController,
) {

    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(listOfBooks = listOfBooks){
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}






