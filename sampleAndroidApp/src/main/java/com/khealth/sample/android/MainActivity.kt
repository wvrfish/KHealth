/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth.sample.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khealth.KHealth
import com.khealth.sample.sampleCheckAllPerms
import com.khealth.sample.sampleReadData
import com.khealth.sample.sampleRequestAllPerms
import com.khealth.sample.sampleWriteData

class MainActivity : ComponentActivity() {
    private val kHealth = KHealth(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // This is REQUIRED for the library to work properly (on Android only)
        kHealth.initialise()

        setContent {
            val context = LocalContext.current
            val isSystemInDarkTheme = isSystemInDarkTheme()
            MaterialTheme(
                colorScheme = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isSystemInDarkTheme ->
                        dynamicDarkColorScheme(context)

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isSystemInDarkTheme ->
                        dynamicLightColorScheme(context)

                    isSystemInDarkTheme -> darkColorScheme()

                    else -> lightColorScheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Button(onClick = { sampleCheckAllPerms(kHealth) }) {
                            Text("Check All Permissions")
                        }

                        Button(onClick = { sampleRequestAllPerms(kHealth) }) {
                            Text("Request All Permissions")
                        }

                        Button(onClick = { sampleWriteData(kHealth) }) {
                            Text("Write Data")
                        }
                        Button(onClick = { sampleReadData(kHealth) }) {
                            Text("Read Data")
                        }
                    }
                }
            }
        }
    }
}
