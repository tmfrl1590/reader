package com.json.reader.screen.login

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.json.reader.R
import com.json.reader.components.EmailInput
import com.json.reader.components.PasswordInput
import com.json.reader.components.ReaderLogo
import androidx.lifecycle.viewmodel.compose.viewModel
import com.json.reader.navigation.ReaderScreens

@Composable
fun ReaderLoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReaderLogo()

            if (showLoginForm.value) { // 파이어베이스 로그인
                UserForm(
                    loading = false,
                    isCreateAccount = false,
                ) { email, password ->
                    Log.i("Form", "ReaderLoginScreen : $email $password")
                    viewModel.signInWithEmailAndPassword(email = email, password = password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            } else {  // 파이어베이스 회원가입
                UserForm(
                    loading = false,
                    isCreateAccount = true
                ) { email, password ->
                    viewModel.createUserWithEmailAndPassword(email = email, password = password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val text = if (showLoginForm.value) "Sign Up" else "Login"
                Text(text = "NewUser?")
                Text(
                    text = text,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    val modifier = Modifier
        .height(240.dp)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(
            rememberScrollState()
        )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if(isCreateAccount) Text(text = stringResource(id = R.string.create_acct), modifier = Modifier.padding(8.dp))
        else Text(text = "")

        EmailInput(
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            }
        )

        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
            }
        )

        SubmitButton(
            textId = if (isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()  // 로그인 버튼 클릭시 키보드 숨겨주기
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape,
    ) {
        if (loading)
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
        else
            Text(text = textId, modifier = Modifier.padding(12.dp))
    }
}