package com.example.habittracker.presentation.screen


import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import com.example.habittracker.presentation.component.ChooseDataDialog
import com.example.habittracker.presentation.event.LoginEvent
import com.example.habittracker.presentation.state.LoginState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    navController: NavController,
//    activity: Activity,
//    callbackManager: CallbackManager,
    context: Context,
    credentialManager: CredentialManager,
    googleIdOption: GetGoogleIdOption
) {

    LaunchedEffect(state.isLoggedIn, state.shouldShowDialog, state.isVerified) {
        if(state.isLoggedIn && !state.shouldShowDialog && state.isVerified == null){
            onEvent(LoginEvent.StartLoading)
            delay(2000)
            onEvent(LoginEvent.StopLoading)
        }
        if(state.isLoggedIn && state.isVerified == false){
            navController.navigate("verification") {
                popUpTo("login") { inclusive = true }
            }
        }
        if (state.isLoggedIn && !state.shouldShowDialog && state.isVerified == true) {
            navController.navigate("account") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    if (state.shouldShowDialog) {
        ChooseDataDialog(
            state = state,
            onEvent = onEvent
        )
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("main"){
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    onEvent(
                        LoginEvent.InputEmail(it)
                    )
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    onEvent(
                        LoginEvent.InputPassword(it)
                    )
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) com.example.habittracker.R.drawable.invisible else com.example.habittracker.R.drawable.visible
                            ),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            state.error?.let {
                Text(text = it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onEvent(LoginEvent.Login)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            TextButton(onClick = { navController.navigate("register") }) {
                Text(
                    text = "Don't have an account? Register",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                Spacer(modifier = Modifier.weight(1f))
                val scope = rememberCoroutineScope()
                Image(
                    painter = painterResource(id = com.example.habittracker.R.drawable.google),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
                            scope.launch {
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()
                                try{
                                    val result  = credentialManager.getCredential(context = context,request = request)
                                    val credential = result.credential

                                    val googleIdTokenCredential =
                                        GoogleIdTokenCredential.createFrom(
                                            credential.data
                                        )
                                    val idToken = googleIdTokenCredential.idToken
                                    onEvent(LoginEvent.LoginViaGoogle(idToken))
                                }catch (e: Exception){
                                    Log.e("GOOGLE_AUTH", e.message ?: "Error")
                                }
                            }
                        })
                )
                Spacer(modifier = Modifier.width(24.dp))
                Image(
                    painter = painterResource(id = com.example.habittracker.R.drawable.facebook),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
//                            LoginManager.getInstance().logInWithReadPermissions(
//                                activity,
//                                listOf("email", "public_profile")
//                            )
//
//                            LoginManager.getInstance().registerCallback(
//                                callbackManager,
//                                object : FacebookCallback<LoginResult> {
//
//                                    override fun onSuccess(result: LoginResult) {
//                                        val token = result.accessToken.token
//                                        onEvent(LoginEvent.LoginViaFacebook(token))
//                                    }
//                                    override fun onCancel() {
//
//                                    }
//                                    override fun onError(error: FacebookException) {
//                                        Log.e("FB_AUTH", error.message ?: "Error")
//                                    }
//                                }
//                            )
                        })
                )

                Spacer(modifier = Modifier.weight(1f))

            }
        }
    }
}

