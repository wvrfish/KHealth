package com.khealth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BodyWaterMassRecord
import androidx.health.connect.client.records.BoneMassRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.IntermenstrualBleedingRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SexualActivityRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

actual class KHealth(private val activity: ComponentActivity) : CoroutineScope {
    private val permissionsChannel = Channel<Set<String>>()
    private lateinit var permissionsLauncher: ActivityResultLauncher<Set<String>>

    private val client get() = HealthConnectClient.getOrCreate(activity)

    override val coroutineContext: CoroutineContext get() = SupervisorJob()

    actual fun initialise() {
        val permissionContract = PermissionController.createRequestPermissionResultContract()
        permissionsLauncher = activity.registerForActivityResult(permissionContract) {
            launch {
                permissionsChannel.send(it)
            }
        }
    }

    actual val isHealthStoreAvailable: Boolean
        get() = HealthConnectClient.getSdkStatus(activity) == HealthConnectClient.SDK_AVAILABLE

    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        check(isHealthStoreAvailable) { "HealthConnect is not available for the current device!" }
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        check(isHealthStoreAvailable) { "HealthConnect is not available for the current device!" }
        check(::permissionsLauncher.isInitialized) {
            "Please make sure to call kHealth.initialise() before trying to access any other " +
                    "methods!"
        }
        try {

            val permissionSets = permissions.map { entry -> entry.toPermissions() }
            permissionsLauncher.launch(permissionSets.flatten().map { it.first }.toSet())

            val grantedPermissions = permissionsChannel.receive()
            return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
        } catch (t: Throwable) {
            logError(t)
            return emptySet()
        }
    }

    private fun Array<out KHPermission>.toPermissionsWithStatuses(
        grantedPermissions: Set<String>
    ): List<KHPermissionWithStatus> = this.mapIndexed { index, entry ->
        val permissionSet = entry.toPermissions()

        val readGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Read }
            ?.first
            ?.let(grantedPermissions::contains)

        val readStatus = readGranted
            ?.let { granted ->
                if (granted) KHPermissionStatus.Granted else KHPermissionStatus.Denied
            }
            ?: KHPermissionStatus.NotDetermined

        val writeGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Write }
            ?.first
            ?.let(grantedPermissions::contains)

        val writeStatus = writeGranted
            ?.let { granted ->
                if (granted) KHPermissionStatus.Granted else KHPermissionStatus.Denied
            }
            ?: KHPermissionStatus.NotDetermined

        KHPermissionWithStatus(
            permission = this[index],
            readStatus = readStatus,
            writeStatus = writeStatus,
        )
    }
}

private fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
    val entry = this@toPermissions
    return buildSet {
        // TODO: Remove this when nullable code (which is iOS-specific) has been written
        @Suppress("RedundantNullableReturnType")
        val record: KClass<out Record>? = when (entry.dataType) {
            KHDataType.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
            KHDataType.BasalMetabolicRate -> BasalMetabolicRateRecord::class
            KHDataType.BloodGlucose -> BloodGlucoseRecord::class

            KHDataType.BloodPressureSystolic,
            KHDataType.BloodPressureDiastolic -> BloodPressureRecord::class

            KHDataType.BodyFat -> BodyFatRecord::class
            KHDataType.BodyTemperature -> BodyTemperatureRecord::class
            KHDataType.BodyWaterMass -> BodyWaterMassRecord::class
            KHDataType.BoneMass -> BoneMassRecord::class
            KHDataType.CervicalMucus -> CervicalMucusRecord::class
            KHDataType.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
            KHDataType.Distance -> DistanceRecord::class
            KHDataType.ElevationGained -> ElevationGainedRecord::class
            KHDataType.ExerciseSession -> ExerciseSessionRecord::class
            KHDataType.FloorsClimbed -> FloorsClimbedRecord::class
            KHDataType.HeartRate -> HeartRateRecord::class
            KHDataType.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
            KHDataType.Height -> HeightRecord::class
            KHDataType.Hydration -> HeartRateVariabilityRmssdRecord::class
            KHDataType.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
            KHDataType.Menstruation -> MenstruationPeriodRecord::class
            KHDataType.LeanBodyMass -> LeanBodyMassRecord::class
            KHDataType.MenstruationFlow -> MenstruationFlowRecord::class
            KHDataType.OvulationTest -> OvulationTestRecord::class
            KHDataType.OxygenSaturation -> OxygenSaturationRecord::class
            KHDataType.Power -> PowerRecord::class
            KHDataType.RespiratoryRate -> RespiratoryRateRecord::class
            KHDataType.RestingHeartRate -> RestingHeartRateRecord::class
            KHDataType.SexualActivity -> SexualActivityRecord::class
            KHDataType.SleepSession -> SleepSessionRecord::class

            KHDataType.RunningSpeed,
            KHDataType.CyclingSpeed -> SpeedRecord::class

            KHDataType.StepCount -> StepsRecord::class
            KHDataType.Vo2Max -> Vo2MaxRecord::class
            KHDataType.Weight -> WeightRecord::class
            KHDataType.WheelChairPushes -> WheelchairPushesRecord::class
        }

        record?.let { safeRecord ->
            if (entry.read) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (entry.write) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
    }
}

private enum class KHPermissionType { Read, Write }
