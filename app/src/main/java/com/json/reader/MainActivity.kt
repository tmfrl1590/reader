package com.json.reader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.json.reader.navigation.ReaderNavigation
import com.json.reader.ui.theme.ReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReaderTheme {

                ReaderApp()

                val db = FirebaseFirestore.getInstance()
                val user: MutableMap<String, Any> = HashMap()
                user["firstName"] = "Jeo"
                user["lastName"] = "James"

                /*db.collection("users")
                            .add(user)
                            .addOnSuccessListener {
                                Log.i("FB", "onCreate: ${it.id}")
                            }
                            .addOnFailureListener {
                                Log.i("FB", "onCreate: $it")
                            }*/


            }
        }
    }
}

@Composable
fun ReaderApp(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReaderNavigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ReaderTheme {

    }
}