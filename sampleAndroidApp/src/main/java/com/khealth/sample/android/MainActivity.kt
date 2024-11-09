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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
                    val allPermissions = remember {
                        arrayOf(
                            KHPermission.ActiveCaloriesBurned(read = true, write = true),
                            KHPermission.BasalMetabolicRate(read = true, write = true),
                            KHPermission.BloodGlucose(read = true, write = true),
                            KHPermission.BloodPressureSystolic(read = true, write = true),
                            KHPermission.BloodPressureDiastolic(read = true, write = true),
                            KHPermission.BodyFat(read = true, write = true),
                            KHPermission.BodyTemperature(read = true, write = true),
                            KHPermission.BodyWaterMass(read = true, write = true),
                            KHPermission.BoneMass(read = true, write = true),
                            KHPermission.CervicalMucus(read = true, write = true),
                            KHPermission.CyclingPedalingCadence(read = true, write = true),
                            KHPermission.Distance(read = true, write = true),
                            KHPermission.ElevationGained(read = true, write = true),
                            KHPermission.ExerciseSession(read = true, write = true),
                            KHPermission.FloorsClimbed(read = true, write = true),
                            KHPermission.HeartRate(read = true, write = true),
                            KHPermission.HeartRateVariability(read = true, write = true),
                            KHPermission.Height(read = true, write = true),
                            KHPermission.Hydration(read = true, write = true),
                            KHPermission.IntermenstrualBleeding(read = true, write = true),
                            KHPermission.Menstruation(read = true, write = true),
                            KHPermission.LeanBodyMass(read = true, write = true),
                            KHPermission.MenstruationFlow(read = true, write = true),
                            KHPermission.OvulationTest(read = true, write = true),
                            KHPermission.OxygenSaturation(read = true, write = true),
                            KHPermission.Power(read = true, write = true),
                            KHPermission.RespiratoryRate(read = true, write = true),
                            KHPermission.RestingHeartRate(read = true, write = true),
                            KHPermission.SexualActivity(read = true, write = true),
                            KHPermission.SleepSession(read = true, write = true),
                            KHPermission.RunningSpeed(read = true, write = true),
                            KHPermission.CyclingSpeed(read = true, write = true),
                            KHPermission.StepCount(read = true, write = true),
                            KHPermission.Vo2Max(read = true, write = true),
                            KHPermission.Weight(read = true, write = true),
                            KHPermission.WheelChairPushes(read = true, write = true),
                        )
                    }

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

private fun printResponse(response: Set<KHPermissionWithStatus>) {
    println(
        "Request Response: ${
            response.joinToString {
                "${it.permission::class.simpleName} -> " +
                        (if (it.readStatus == KHPermissionStatus.Granted) "R" else "") +
                        if (it.writeStatus == KHPermissionStatus.Granted) "+W" else ""
            }
        }"
    )
}
