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
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
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
import androidx.health.connect.client.units.BloodGlucose
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Power
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Velocity
import androidx.health.connect.client.units.calories
import androidx.health.connect.client.units.celsius
import androidx.health.connect.client.units.fahrenheit
import androidx.health.connect.client.units.fluidOuncesUs
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.inches
import androidx.health.connect.client.units.joules
import androidx.health.connect.client.units.kilocalories
import androidx.health.connect.client.units.kilocaloriesPerDay
import androidx.health.connect.client.units.kilojoules
import androidx.health.connect.client.units.kilometersPerHour
import androidx.health.connect.client.units.liters
import androidx.health.connect.client.units.meters
import androidx.health.connect.client.units.metersPerSecond
import androidx.health.connect.client.units.miles
import androidx.health.connect.client.units.milesPerHour
import androidx.health.connect.client.units.millimetersOfMercury
import androidx.health.connect.client.units.ounces
import androidx.health.connect.client.units.percent
import androidx.health.connect.client.units.pounds
import androidx.health.connect.client.units.watts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaInstant
import kotlin.reflect.KClass

actual class KHealth {
    constructor(activity: ComponentActivity) {
        this.activity = activity
        this.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        this.permissionsChannel = Channel()
    }

    internal constructor(
        client: HealthConnectClient,
        coroutineScope: CoroutineScope,
        isHealthStoreAvailable: Boolean,
        permissionsChannel: Channel<Set<String>>
    ) {
        this.client = client
        this.coroutineScope = coroutineScope
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
        this.permissionsChannel = permissionsChannel
    }

    private var activity: ComponentActivity? = null

    private lateinit var client: HealthConnectClient
    private val coroutineScope: CoroutineScope
    private var testIsHealthStoreAvailable: Boolean? = null
    private val permissionsChannel: Channel<Set<String>>

    private lateinit var permissionsLauncher: ActivityResultLauncher<Set<String>>

    actual fun initialise() {
        if (!::client.isInitialized) client = HealthConnectClient.getOrCreate(activity!!)
        if (!::permissionsLauncher.isInitialized) {
            val permissionContract = PermissionController.createRequestPermissionResultContract()
            permissionsLauncher = activity!!.registerForActivityResult(permissionContract) {
                coroutineScope.launch {
                    permissionsChannel.send(it)
                }
            }
        }
    }

    actual val isHealthStoreAvailable: Boolean
        get() = testIsHealthStoreAvailable
            ?: (HealthConnectClient.getSdkStatus(activity!!) == HealthConnectClient.SDK_AVAILABLE)

    internal actual fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        verifyHealthStoreAvailability()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        verifyHealthStoreAvailability()
        val permissionSets = permissions.map { entry -> entry.toPermissions() }

        if (::permissionsLauncher.isInitialized) {
            permissionsLauncher.launch(permissionSets.flatten().map { it.first }.toSet())
        } else {
            logError(HealthStoreNotInitialisedException)
        }

        val grantedPermissions = permissionsChannel.receive()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun writeData(vararg records: KHRecord): KHWriteResponse {
        try {
            verifyHealthStoreAvailability()
            val hcRecords = records.map { record -> record.toHCRecord() }
            logDebug("Inserting ${hcRecords.size} records...")
            val responseIDs = client.insertRecords(hcRecords).recordIdsList
            logDebug("Inserted ${responseIDs.size} records")
            return when {
                responseIDs.size != hcRecords.size && responseIDs.isEmpty() -> {
                    KHWriteResponse.Failed(Exception("No records were written!"))
                }

                responseIDs.size != hcRecords.size -> KHWriteResponse.SomeFailed

                else -> KHWriteResponse.Success
            }
        } catch (t: Throwable) {
            val parsedThrowable = when (t) {
                is SecurityException -> NoWriteAccessException(t.message?.extractHealthPermission())
                else -> t
            }
            logError(t)
            return KHWriteResponse.Failed(parsedThrowable)
        }
    }
}

private fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
    val entry = this@toPermissions
    return buildSet {
        entry.dataType.toRecordClass().let { safeRecord ->
            if (entry.read) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (entry.write) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
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

private fun KHDataType.toRecordClass(): KClass<out Record> = when (this) {
    KHDataType.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
    KHDataType.BasalMetabolicRate -> BasalMetabolicRateRecord::class
    KHDataType.BloodGlucose -> BloodGlucoseRecord::class
    KHDataType.BloodPressure -> BloodPressureRecord::class
    KHDataType.BodyFat -> BodyFatRecord::class
    KHDataType.BodyTemperature -> BodyTemperatureRecord::class
    KHDataType.BodyWaterMass -> BodyWaterMassRecord::class
    KHDataType.BoneMass -> BoneMassRecord::class
    KHDataType.CervicalMucus -> CervicalMucusRecord::class
    KHDataType.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
    KHDataType.Distance -> DistanceRecord::class
    KHDataType.ElevationGained -> ElevationGainedRecord::class
    KHDataType.FloorsClimbed -> FloorsClimbedRecord::class
    KHDataType.HeartRate -> HeartRateRecord::class
    KHDataType.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
    KHDataType.Height -> HeightRecord::class
    KHDataType.Hydration -> HydrationRecord::class
    KHDataType.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
    KHDataType.MenstruationPeriod -> MenstruationPeriodRecord::class
    KHDataType.LeanBodyMass -> LeanBodyMassRecord::class
    KHDataType.MenstruationFlow -> MenstruationFlowRecord::class
    KHDataType.OvulationTest -> OvulationTestRecord::class
    KHDataType.OxygenSaturation -> OxygenSaturationRecord::class
    KHDataType.Power -> PowerRecord::class
    KHDataType.RespiratoryRate -> RespiratoryRateRecord::class
    KHDataType.RestingHeartRate -> RestingHeartRateRecord::class
    KHDataType.SexualActivity -> SexualActivityRecord::class
    KHDataType.SleepSession -> SleepSessionRecord::class
    KHDataType.RunningSpeed, KHDataType.CyclingSpeed -> SpeedRecord::class
    KHDataType.StepCount -> StepsRecord::class
    KHDataType.Vo2Max -> Vo2MaxRecord::class
    KHDataType.Weight -> WeightRecord::class
    KHDataType.WheelChairPushes -> WheelchairPushesRecord::class
}

private fun KHRecord.toHCRecord(): Record {
    return when (this) {
        is KHRecord.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord(
            startTime = startTime.toJavaInstant(),
            endTime = endTime.toJavaInstant(),
            startZoneOffset = null,
            endZoneOffset = null,
            energy = when (energy) {
                is KHUnit.Energy.Calorie -> energy.value.calories
                is KHUnit.Energy.Joule -> energy.value.joules
                is KHUnit.Energy.KiloCalorie -> energy.value.kilocalories
                is KHUnit.Energy.KiloJoule -> energy.value.kilojoules
            },
        )

        is KHRecord.BasalMetabolicRate -> BasalMetabolicRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            basalMetabolicRate = rateAndroid.toNativePower(),
        )

        is KHRecord.BloodGlucose -> BloodGlucoseRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            level = when (level) {
                is KHUnit.BloodGlucose.MillimolesPerLiter ->
                    BloodGlucose.millimolesPerLiter(level.value)

                is KHUnit.BloodGlucose.MilligramsPerDeciliter ->
                    BloodGlucose.milligramsPerDeciliter(level.value)
            },
        )

        is KHRecord.BloodPressure -> BloodPressureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            systolic = systolic.toNativePressure(),
            diastolic = diastolic.toNativePressure(),
        )

        is KHRecord.BodyFat -> BodyFatRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            percentage = percentage.percent,
        )

        is KHRecord.BodyTemperature -> BodyTemperatureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            temperature = when (temperature) {
                is KHUnit.Temperature.Celsius -> temperature.value.celsius
                is KHUnit.Temperature.Fahrenheit -> temperature.value.fahrenheit
            }
        )

        is KHRecord.BodyWaterMass -> BodyWaterMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = mass.toNativeMass()
        )

        is KHRecord.BoneMass -> BoneMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = mass.toNativeMass()
        )

        is KHRecord.CervicalMucus -> CervicalMucusRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            appearance = when (appearance) {
                KHCervicalMucusAppearance.Creamy -> CervicalMucusRecord.APPEARANCE_CREAMY
                KHCervicalMucusAppearance.Dry -> CervicalMucusRecord.APPEARANCE_DRY
                KHCervicalMucusAppearance.EggWhite -> CervicalMucusRecord.APPEARANCE_EGG_WHITE
                KHCervicalMucusAppearance.Sticky -> CervicalMucusRecord.APPEARANCE_STICKY
                KHCervicalMucusAppearance.Watery -> CervicalMucusRecord.APPEARANCE_WATERY
            },
        )

        is KHRecord.CyclingPedalingCadence -> CyclingPedalingCadenceRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                CyclingPedalingCadenceRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    revolutionsPerMinute = sample.revolutionsPerMinute,
                )
            },
        )

        is KHRecord.Distance -> DistanceRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            distance = distance.toNativeLength()
        )

        is KHRecord.ElevationGained -> ElevationGainedRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            elevation = elevation.toNativeLength()
        )

        is KHRecord.FloorsClimbed -> FloorsClimbedRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            floors = floors
        )

        is KHRecord.HeartRate -> HeartRateRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                HeartRateRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    beatsPerMinute = sample.beatsPerMinute,
                )
            }
        )

        is KHRecord.HeartRateVariability -> HeartRateVariabilityRmssdRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            heartRateVariabilityMillis = heartRateVariabilityMillis
        )

        is KHRecord.Height -> HeightRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            height = height.toNativeLength(),
        )

        is KHRecord.Hydration -> HydrationRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            volume = when (volume) {
                is KHUnit.Volume.FluidOunceUS -> volume.value.fluidOuncesUs
                is KHUnit.Volume.Liter -> volume.value.liters
            }
        )

        is KHRecord.IntermenstrualBleeding -> IntermenstrualBleedingRecord(
            time = time.toJavaInstant(),
            zoneOffset = null
        )

        is KHRecord.LeanBodyMass -> LeanBodyMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = mass.toNativeMass(),
        )

        is KHRecord.MenstruationPeriod -> MenstruationPeriodRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
        )

        is KHRecord.MenstruationFlow -> MenstruationFlowRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            flow = when (flowType) {
                KHMenstruationFlowType.Unknown -> MenstruationFlowRecord.FLOW_UNKNOWN
                KHMenstruationFlowType.Light -> MenstruationFlowRecord.FLOW_LIGHT
                KHMenstruationFlowType.Medium -> MenstruationFlowRecord.FLOW_MEDIUM
                KHMenstruationFlowType.Heavy -> MenstruationFlowRecord.FLOW_HEAVY
            }
        )

        is KHRecord.OvulationTest -> OvulationTestRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            result = when (result) {
                KHOvulationTestResult.High -> OvulationTestRecord.RESULT_HIGH
                KHOvulationTestResult.Negative -> OvulationTestRecord.RESULT_NEGATIVE
                KHOvulationTestResult.Positive -> OvulationTestRecord.RESULT_POSITIVE
                KHOvulationTestResult.Inconclusive -> OvulationTestRecord.RESULT_INCONCLUSIVE
            }
        )

        is KHRecord.OxygenSaturation -> OxygenSaturationRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            percentage = percentage.percent
        )

        is KHRecord.Power -> PowerRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                PowerRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    power = sample.power.toNativePower()
                )
            },
        )

        is KHRecord.RespiratoryRate -> RespiratoryRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            rate = rate
        )

        is KHRecord.RestingHeartRate -> RestingHeartRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            beatsPerMinute = beatsPerMinute,
        )

        is KHRecord.SexualActivity -> SexualActivityRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            protectionUsed = if (didUseProtection) SexualActivityRecord.PROTECTION_USED_PROTECTED
            else SexualActivityRecord.PROTECTION_USED_UNPROTECTED
        )

        is KHRecord.SleepSession -> SleepSessionRecord(
            startTime = samples.first().startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().endTime.toJavaInstant(),
            endZoneOffset = null,
            stages = samples.map { sample ->
                SleepSessionRecord.Stage(
                    startTime = sample.startTime.toJavaInstant(),
                    endTime = sample.endTime.toJavaInstant(),
                    stage = when (sample.stage) {
                        KHSleepStage.Awake -> SleepSessionRecord.STAGE_TYPE_AWAKE
                        KHSleepStage.AwakeInBed -> SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED
                        KHSleepStage.AwakeOutOfBed -> SleepSessionRecord.STAGE_TYPE_OUT_OF_BED
                        KHSleepStage.Deep -> SleepSessionRecord.STAGE_TYPE_DEEP
                        KHSleepStage.Light -> SleepSessionRecord.STAGE_TYPE_LIGHT
                        KHSleepStage.REM -> SleepSessionRecord.STAGE_TYPE_REM
                        KHSleepStage.Sleeping -> SleepSessionRecord.STAGE_TYPE_SLEEPING
                        KHSleepStage.Unknown -> SleepSessionRecord.STAGE_TYPE_UNKNOWN
                    }
                )
            },
        )

        is KHRecord.RunningSpeed -> SpeedRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                SpeedRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    speed = sample.speed.toNativeVelocity(),
                )
            },
        )

        is KHRecord.CyclingSpeed -> SpeedRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                SpeedRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    speed = sample.speed.toNativeVelocity(),
                )
            },
        )

        is KHRecord.StepCount -> StepsRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            count = count,
        )

        is KHRecord.Vo2Max -> Vo2MaxRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            vo2MillilitersPerMinuteKilogram = vo2MillilitersPerMinuteKilogram,
        )

        is KHRecord.Weight -> WeightRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            weight = weight.toNativeMass(),
        )

        is KHRecord.WheelChairPushes -> WheelchairPushesRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            count = count,
        )
    }
}

private fun KHUnit.Length.toNativeLength(): Length = when (this) {
    is KHUnit.Length.Inch -> value.inches
    is KHUnit.Length.Meter -> value.meters
    is KHUnit.Length.Mile -> value.miles
}

private fun KHUnit.Mass.toNativeMass(): Mass = when (this) {
    is KHUnit.Mass.Gram -> value.grams
    is KHUnit.Mass.Ounce -> value.ounces
    is KHUnit.Mass.Pound -> value.pounds
}

private fun KHUnit.Power.toNativePower(): Power = when (this) {
    is KHUnit.Power.KilocaloriePerDay -> value.kilocaloriesPerDay
    is KHUnit.Power.Watt -> value.watts
}

private fun KHUnit.Velocity.toNativeVelocity(): Velocity = when (this) {
    is KHUnit.Velocity.KilometersPerHour -> value.kilometersPerHour
    is KHUnit.Velocity.MetersPerSecond -> value.metersPerSecond
    is KHUnit.Velocity.MilesPerHour -> value.milesPerHour
}

private fun KHUnit.Pressure.toNativePressure(): Pressure = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury -> value.millimetersOfMercury
}

private enum class KHPermissionType { Read, Write }
