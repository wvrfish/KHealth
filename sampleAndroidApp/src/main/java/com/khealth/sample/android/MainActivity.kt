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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.khealth.KHDataType
import com.khealth.KHPermission
import com.khealth.KHPermissionStatus
import com.khealth.KHPermissionWithStatus
import com.khealth.KHealth
import kotlinx.coroutines.launch

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
                    val coroutineScope = rememberCoroutineScope()
                    Column {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    printResponse(kHealth.checkPermissions(*allPermissions))
                                }
                            },
                        ) {
                            Text("Check All Health Permissions")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    printResponse(kHealth.requestPermissions(*allPermissions))
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

private val allPermissions = arrayOf(
    KHPermission(dataType = KHDataType.ActiveCaloriesBurned, read = true, write = true),
    KHPermission(dataType = KHDataType.BasalMetabolicRate, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodGlucose, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodPressureSystolic, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodPressureDiastolic, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyFat, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyTemperature, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyWaterMass, read = true, write = true),
    KHPermission(dataType = KHDataType.BoneMass, read = true, write = true),
    KHPermission(dataType = KHDataType.CervicalMucus, read = true, write = true),
    KHPermission(dataType = KHDataType.CyclingPedalingCadence, read = true, write = true),
    KHPermission(dataType = KHDataType.Distance, read = true, write = true),
    KHPermission(dataType = KHDataType.ElevationGained, read = true, write = true),
    KHPermission(dataType = KHDataType.ExerciseSession, read = true, write = true),
    KHPermission(dataType = KHDataType.FloorsClimbed, read = true, write = true),
    KHPermission(dataType = KHDataType.HeartRate, read = true, write = true),
    KHPermission(dataType = KHDataType.HeartRateVariability, read = true, write = true),
    KHPermission(dataType = KHDataType.Height, read = true, write = true),
    KHPermission(dataType = KHDataType.Hydration, read = true, write = true),
    KHPermission(dataType = KHDataType.IntermenstrualBleeding, read = true, write = true),
    KHPermission(dataType = KHDataType.Menstruation, read = true, write = true),
    KHPermission(dataType = KHDataType.LeanBodyMass, read = true, write = true),
    KHPermission(dataType = KHDataType.MenstruationFlow, read = true, write = true),
    KHPermission(dataType = KHDataType.OvulationTest, read = true, write = true),
    KHPermission(dataType = KHDataType.OxygenSaturation, read = true, write = true),
    KHPermission(dataType = KHDataType.Power, read = true, write = true),
    KHPermission(dataType = KHDataType.RespiratoryRate, read = true, write = true),
    KHPermission(dataType = KHDataType.RestingHeartRate, read = true, write = true),
    KHPermission(dataType = KHDataType.SexualActivity, read = true, write = true),
    KHPermission(dataType = KHDataType.SleepSession, read = true, write = true),
    KHPermission(dataType = KHDataType.RunningSpeed, read = true, write = true),
    KHPermission(dataType = KHDataType.CyclingSpeed, read = true, write = true),
    KHPermission(dataType = KHDataType.StepCount, read = true, write = true),
    KHPermission(dataType = KHDataType.Vo2Max, read = true, write = true),
    KHPermission(dataType = KHDataType.Weight, read = true, write = true),
    KHPermission(dataType = KHDataType.WheelChairPushes, read = true, write = true),
)

private fun printResponse(response: Set<KHPermissionWithStatus>) {
    println(
        "Request Response: ${
            response.joinToString {
                "${it.permission.dataType} -> " +
                        (if (it.readStatus == KHPermissionStatus.Granted) "R" else "") +
                        if (it.writeStatus == KHPermissionStatus.Granted) "+W" else ""
            }
        }"
    )
}
