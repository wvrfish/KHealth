package com.khealth.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.khealth.KHPermission
import com.khealth.KHealth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val kHealth = KHealth(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kHealth.initialise()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val allPermissions = remember {
                        arrayOf(
                            KHPermission.HeartRate(read = true, write = false),
                            KHPermission.StepCount(read = false, write = true),
                        )
                    }

                    Column {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val response = kHealth.checkPermissions(*allPermissions)
                                    println("Response: $response")
                                }
                            },
                        ) {
                            Text("Check All Health Permissions")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val response = kHealth.requestPermissions(*allPermissions)
                                    println("Response: $response")
                                }
                            },
                        ) {
                            Text("Request All Health Permissions")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
