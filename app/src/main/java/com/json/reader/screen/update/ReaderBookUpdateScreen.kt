package com.json.reader.screen.update

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.json.reader.R
import com.json.reader.components.InputField
import com.json.reader.components.RatingBar
import com.json.reader.components.ReaderAppBar
import com.json.reader.components.RoundedButton
import com.json.reader.components.showToast
import com.json.reader.data.DataOrException
import com.json.reader.model.MBook
import com.json.reader.navigation.ReaderScreens
import com.json.reader.screen.home.HomeScreenViewModel
import com.json.reader.utils.formatDate

@Composable
fun ReaderBookUpdateScreen(
    navController: NavController,
    bookItemId: String,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Update Book",
                navController = navController,
                icon = Icons.Default.ArrowBack,
                showProfile = false
            ) {
                navController.popBackStack()
            }
        }
    ) {

        val bookInfo = produceState<DataOrException<List<MBook>, Boolean, Exception>>(
            initialValue = DataOrException(
                data = emptyList(),
                true,
                Exception("")
            )
        ) {
            value = viewModel.data.value
        }.value

        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                    bookInfo.loading = false
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo = viewModel.data.value, bookItemId = bookItemId)
                    }

                    ShowSimpleForm(book = viewModel.data.value.data?.first() { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController)
                }
            }
        }
    }
}

@Composable
fun ShowSimpleForm(book: MBook, navController: NavController) {

    val context = LocalContext.current

    val notesText = remember {
        mutableStateOf("")
    }
    val isStartReading = remember {
        mutableStateOf(false)
    }
    val isFinishing = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }

    SimpleForm(
        defaultValue = if (book.notes.toString()
                .isNotEmpty()
        ) book.notes.toString() else "No thoughts available"
    ) { note ->
        notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = {
                isStartReading.value = true
            },
            enabled = book.startedReading == null
        ) {
            if (book.startedReading == null) {
                if (!isStartReading.value) {
                    Text(text = "Started Reading")
                } else {
                    Text(
                        text = "Started Reading!!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started on : ${formatDate(book.startedReading!!)}")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = {
                isFinishing.value = true
            },
            enabled = book.finishedReading == null
        ) {
            if (book.finishedReading == null) {
                if (!isFinishing.value) {
                    Text(text = "Marked as Read")
                } else {
                    Text(text = "Finished Read")
                }
            } else {
                Text(text = "Finished on : ${formatDate(book.finishedReading!!)}")
            }
        }
    }

    Text(
        text = "Rating",
        modifier = Modifier.padding(bottom = 4.dp)
    )
    book.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.value = rating
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 16.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        val changeNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp = if (isFinishing.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp = if (isStartReading.value) Timestamp.now() else book.startedReading

        val bookUpdate = changeNotes || changedRating || isStartReading.value || isFinishing.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value,
        ).toMap()

        RoundedButton(
            label = "Update"
        ) {
            if (bookUpdate) {
                FirebaseFirestore
                    .getInstance().collection("books").document(book.id!!).update(bookToUpdate)
                    .addOnCompleteListener {
                        showToast(context, "Book Update Successfully")
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }.addOnFailureListener {

                    }
            }
        }
        Spacer(modifier = Modifier.width(80.dp))

        val openDialog = remember {
            mutableStateOf(false)
        }

        if (openDialog.value) {
            ShowAlertDialog(message = stringResource(id = R.string.sure) + "\n" + stringResource(id = R.string.action), openDialog){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            openDialog.value = false
                            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                        }
                    }
            }
        }

        RoundedButton(
            label = "Delete"
        ) {
            openDialog.value = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
) {
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) { textFieldValue.value.trim().isNotEmpty() }

        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelId = "Enter Your thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })

    }

}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>,
            Boolean, Exception>, bookItemId: String
) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId

                }, onPressDetails = {})

            }
        }

    }
}

@Composable
fun CardListItem(
    book: MBook,
    onPressDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(
                start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable {

            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberAsyncImagePainter(model = book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp
                    )
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp
                    )
                )
            }
        }
    }
}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Delete Book")
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = { onYesPressed.invoke() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = "No")
                }
            },
        )
    }
}