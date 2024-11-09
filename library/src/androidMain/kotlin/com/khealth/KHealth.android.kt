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
        if (!isHealthStoreAvailable) return emptySet()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        try {
            if (!isHealthStoreAvailable) return emptySet()

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
        val record: KClass<out Record>? = when (entry) {
            is KHPermission.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
            is KHPermission.BasalMetabolicRate -> BasalMetabolicRateRecord::class
            is KHPermission.BloodGlucose -> BloodGlucoseRecord::class

            is KHPermission.BloodPressureSystolic,
            is KHPermission.BloodPressureDiastolic -> BloodPressureRecord::class

            is KHPermission.BodyFat -> BodyFatRecord::class
            is KHPermission.BodyTemperature -> BodyTemperatureRecord::class
            is KHPermission.BodyWaterMass -> BodyWaterMassRecord::class
            is KHPermission.BoneMass -> BoneMassRecord::class
            is KHPermission.CervicalMucus -> CervicalMucusRecord::class
            is KHPermission.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
            is KHPermission.Distance -> DistanceRecord::class
            is KHPermission.ElevationGained -> ElevationGainedRecord::class
            is KHPermission.ExerciseSession -> ExerciseSessionRecord::class
            is KHPermission.FloorsClimbed -> FloorsClimbedRecord::class
            is KHPermission.HeartRate -> HeartRateRecord::class
            is KHPermission.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
            is KHPermission.Height -> HeightRecord::class
            is KHPermission.Hydration -> HeartRateVariabilityRmssdRecord::class
            is KHPermission.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
            is KHPermission.Menstruation -> MenstruationPeriodRecord::class
            is KHPermission.LeanBodyMass -> LeanBodyMassRecord::class
            is KHPermission.MenstruationFlow -> MenstruationFlowRecord::class
            is KHPermission.OvulationTest -> OvulationTestRecord::class
            is KHPermission.OxygenSaturation -> OxygenSaturationRecord::class
            is KHPermission.Power -> PowerRecord::class
            is KHPermission.RespiratoryRate -> RespiratoryRateRecord::class
            is KHPermission.RestingHeartRate -> RestingHeartRateRecord::class
            is KHPermission.SexualActivity -> SexualActivityRecord::class
            is KHPermission.SleepSession -> SleepSessionRecord::class

            is KHPermission.RunningSpeed,
            is KHPermission.CyclingSpeed -> SpeedRecord::class

            is KHPermission.StepCount -> StepsRecord::class
            is KHPermission.Vo2Max -> Vo2MaxRecord::class
            is KHPermission.Weight -> WeightRecord::class
            is KHPermission.WheelChairPushes -> WheelchairPushesRecord::class
        }

        record?.let { safeRecord ->
            if (entry.readRequested) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (entry.writeRequested) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
    }
}

private enum class KHPermissionType { Read, Write }
