package com.khealth.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khealth.KHealth
import com.khealth.sample.sampleCheckAllPerms
import com.khealth.sample.sampleRequestAllPerms
import com.khealth.sample.sampleWriteData

class MainActivity : ComponentActivity() {
    private val kHealth = KHealth(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kHealth.initialise()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Button(onClick = { sampleCheckAllPerms(kHealth) }) {
                            Text("Check All Permissions")
                        }

                        Button(onClick = { sampleRequestAllPerms(kHealth) }) {
                            Text("Request All Permissions")
                        }

                        Button(onClick = { sampleWriteData(kHealth) }) {
                            Text("Write Data")
                        }
                    }
                }
            }
        }
    }
}
