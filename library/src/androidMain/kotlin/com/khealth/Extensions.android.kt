package com.khealth

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
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Power
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import androidx.health.connect.client.units.Velocity
import androidx.health.connect.client.units.Volume
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
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlin.reflect.KClass


internal fun Record.toKHRecord(request: KHReadRequest): KHRecord = when (this) {
    is ActiveCaloriesBurnedRecord -> {
        val unit = (request as KHReadRequest.ActiveCaloriesBurned).unit
        KHRecord.ActiveCaloriesBurned(
            unit = unit,
            value = this.energy toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant()
        )
    }

    is BasalMetabolicRateRecord -> {
        val unit = (request as KHReadRequest.BasalMetabolicRate).unit
        KHRecord.BasalMetabolicRate(
            unit = unit,
            value = this.basalMetabolicRate toDoubleValueFor unit.left,
            time = this.time.toKotlinInstant()
        )
    }

    is BloodGlucoseRecord -> {
        val unit = (request as KHReadRequest.BloodGlucose).unit
        KHRecord.BloodGlucose(
            unit = unit,
            value = this.level toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BloodPressureRecord -> {
        val unit = (request as KHReadRequest.BloodPressure).unit
        KHRecord.BloodPressure(
            unit = unit,
            systolicValue = this.systolic toDoubleValueFor unit,
            diastolicValue = this.diastolic toDoubleValueFor unit,
            time = this.time.toKotlinInstant(),
        )
    }

    is BodyFatRecord -> KHRecord.BodyFat(
        percentage = this.percentage.value,
        time = this.time.toKotlinInstant(),
    )

    is BodyTemperatureRecord -> {
        val unit = (request as KHReadRequest.BodyTemperature).unit
        KHRecord.BodyTemperature(
            unit = unit,
            value = this.temperature toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BodyWaterMassRecord -> {
        val unit = (request as KHReadRequest.BodyWaterMass).unit
        KHRecord.BodyWaterMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BoneMassRecord -> {
        val unit = (request as KHReadRequest.BoneMass).unit
        KHRecord.BoneMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is CervicalMucusRecord -> KHRecord.CervicalMucus(
        appearance = when (this.appearance) {
            CervicalMucusRecord.APPEARANCE_CREAMY -> KHCervicalMucusAppearance.Watery
            CervicalMucusRecord.APPEARANCE_DRY -> KHCervicalMucusAppearance.Dry
            CervicalMucusRecord.APPEARANCE_EGG_WHITE -> KHCervicalMucusAppearance.EggWhite
            CervicalMucusRecord.APPEARANCE_STICKY -> KHCervicalMucusAppearance.Sticky
            CervicalMucusRecord.APPEARANCE_WATERY -> KHCervicalMucusAppearance.Watery
            else -> throw Exception("Unknown Cervical Mucus appearance!")
        },
        time = this.time.toKotlinInstant()
    )

    is CyclingPedalingCadenceRecord -> KHRecord.CyclingPedalingCadence(
        samples = this.samples.map { sample ->
            KHCyclingPedalingCadenceSample(
                revolutionsPerMinute = sample.revolutionsPerMinute,
                time = sample.time.toKotlinInstant(),
            )
        },
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    is DistanceRecord -> {
        val unit = (request as KHReadRequest.Distance).unit
        KHRecord.Distance(
            unit = unit,
            value = this.distance toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is ElevationGainedRecord -> {
        val unit = (request as KHReadRequest.ElevationGained).unit
        KHRecord.ElevationGained(
            unit = unit,
            value = this.elevation toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is FloorsClimbedRecord -> KHRecord.FloorsClimbed(
        floors = this.floors,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant(),
    )

    is HeartRateRecord -> KHRecord.HeartRate(
        samples = this.samples.map { sample ->
            KHHeartRateSample(
                beatsPerMinute = sample.beatsPerMinute,
                time = sample.time.toKotlinInstant()
            )
        }
    )

    is HeartRateVariabilityRmssdRecord -> KHRecord.HeartRateVariability(
        heartRateVariabilityMillis = this.heartRateVariabilityMillis,
        time = this.time.toKotlinInstant()
    )

    is HeightRecord -> {
        val unit = (request as KHReadRequest.Height).unit
        KHRecord.Height(
            unit = unit,
            value = this.height toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is HydrationRecord -> {
        val unit = (request as KHReadRequest.Hydration).unit
        KHRecord.Hydration(
            unit = unit,
            value = this.volume toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is IntermenstrualBleedingRecord -> KHRecord.IntermenstrualBleeding(
        time = this.time.toKotlinInstant()
    )

    is MenstruationPeriodRecord -> KHRecord.MenstruationPeriod(
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant(),
    )

    is LeanBodyMassRecord -> {
        val unit = (request as KHReadRequest.LeanBodyMass).unit
        KHRecord.LeanBodyMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is MenstruationFlowRecord -> {
        KHRecord.MenstruationFlow(
            type = when (this.flow) {
                MenstruationFlowRecord.FLOW_UNKNOWN -> KHMenstruationFlowType.Unknown
                MenstruationFlowRecord.FLOW_LIGHT -> KHMenstruationFlowType.Light
                MenstruationFlowRecord.FLOW_MEDIUM -> KHMenstruationFlowType.Medium
                MenstruationFlowRecord.FLOW_HEAVY -> KHMenstruationFlowType.Heavy
                else -> throw Exception("Unknown menstruation flow type!")
            },
            time = this.time.toKotlinInstant(),
        )
    }

    is OvulationTestRecord -> KHRecord.OvulationTest(
        result = when (this.result) {
            OvulationTestRecord.RESULT_HIGH -> KHOvulationTestResult.High
            OvulationTestRecord.RESULT_NEGATIVE -> KHOvulationTestResult.Negative
            OvulationTestRecord.RESULT_POSITIVE -> KHOvulationTestResult.Positive
            OvulationTestRecord.RESULT_INCONCLUSIVE -> KHOvulationTestResult.Inconclusive
            else -> throw Exception("Unknown ovulation test result!")
        },
        time = this.time.toKotlinInstant()
    )

    is OxygenSaturationRecord -> KHRecord.OxygenSaturation(
        percentage = this.percentage.value,
        time = this.time.toKotlinInstant()
    )

    is PowerRecord -> {
        val unit = (request as KHReadRequest.Power).unit
        KHRecord.Power(
            samples = this.samples.map { sample ->
                KHPowerSample(
                    unit = unit,
                    value = sample.power toDoubleValueFor unit,
                    time = sample.time.toKotlinInstant(),
                )
            }
        )
    }

    is RespiratoryRateRecord -> KHRecord.RespiratoryRate(
        rate = this.rate,
        time = this.time.toKotlinInstant()
    )

    is RestingHeartRateRecord -> KHRecord.RestingHeartRate(
        beatsPerMinute = this.beatsPerMinute,
        time = this.time.toKotlinInstant()
    )

    is SexualActivityRecord -> KHRecord.SexualActivity(
        didUseProtection = this.protectionUsed == SexualActivityRecord.PROTECTION_USED_PROTECTED,
        time = this.time.toKotlinInstant()
    )

    is SleepSessionRecord -> KHRecord.SleepSession(
        samples = this.stages.map { sample ->
            KHSleepStageSample(
                stage = when (sample.stage) {
                    SleepSessionRecord.STAGE_TYPE_AWAKE -> KHSleepStage.Awake
                    SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> KHSleepStage.AwakeInBed
                    SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> KHSleepStage.AwakeOutOfBed
                    SleepSessionRecord.STAGE_TYPE_DEEP -> KHSleepStage.Deep
                    SleepSessionRecord.STAGE_TYPE_LIGHT -> KHSleepStage.Light
                    SleepSessionRecord.STAGE_TYPE_REM -> KHSleepStage.REM
                    SleepSessionRecord.STAGE_TYPE_SLEEPING -> KHSleepStage.Sleeping
                    SleepSessionRecord.STAGE_TYPE_UNKNOWN -> KHSleepStage.Unknown
                    else -> throw Exception("Unknown sleep stage!")
                },
                startTime = this.startTime.toKotlinInstant(),
                endTime = this.endTime.toKotlinInstant(),
            )
        }
    )

    is SpeedRecord -> {
        val unit = (request as KHReadRequest.Speed).unit
        KHRecord.Speed(
            samples = this.samples.map { sample ->
                KHSpeedSample(
                    unit = unit,
                    value = sample.speed toDoubleValueFor unit,
                    time = sample.time.toKotlinInstant()
                )
            }
        )
    }

    is StepsRecord -> KHRecord.StepCount(
        count = this.count,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    is Vo2MaxRecord -> KHRecord.Vo2Max(
        vo2MillilitersPerMinuteKilogram = this.vo2MillilitersPerMinuteKilogram,
        time = this.time.toKotlinInstant()
    )

    is WeightRecord -> {
        val unit = (request as KHReadRequest.Weight).unit
        KHRecord.Weight(
            unit = unit,
            value = this.weight toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is WheelchairPushesRecord -> KHRecord.WheelChairPushes(
        count = this.count,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    else -> throw Exception("Unknown record type!")
}

internal fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
    val entry = this@toPermissions
    return buildSet {
        entry.dataType.toRecordClass()?.let { safeRecord ->
            if (entry.read) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (entry.write) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
    }
}

internal fun Array<out KHPermission>.toPermissionsWithStatuses(
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

internal fun KHDataType.toRecordClass(): KClass<out Record>? = when (this) {
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
    KHDataType.Speed -> SpeedRecord::class
    KHDataType.RunningSpeed, KHDataType.CyclingSpeed -> null
    KHDataType.StepCount -> StepsRecord::class
    KHDataType.Vo2Max -> Vo2MaxRecord::class
    KHDataType.Weight -> WeightRecord::class
    KHDataType.WheelChairPushes -> WheelchairPushesRecord::class
}

internal fun KHRecord.toHCRecord(): Record? {
    return when (this) {
        is KHRecord.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord(
            startTime = startTime.toJavaInstant(),
            endTime = endTime.toJavaInstant(),
            startZoneOffset = null,
            endZoneOffset = null,
            energy = unit toNativeEnergyFor value,
        )

        is KHRecord.BasalMetabolicRate -> BasalMetabolicRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            basalMetabolicRate = unit.left toNativePowerFor value,
        )

        is KHRecord.BloodGlucose -> BloodGlucoseRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            level = unit toNativeBloodGlucoseFor value,
        )

        is KHRecord.BloodPressure -> BloodPressureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            systolic = unit toNativePressureFor systolicValue,
            diastolic = unit toNativePressureFor diastolicValue,
        )

        is KHRecord.BodyFat -> BodyFatRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            percentage = percentage.percent,
        )

        is KHRecord.BodyTemperature -> BodyTemperatureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            temperature = unit toNativeTemperatureFor value
        )

        is KHRecord.BodyWaterMass -> BodyWaterMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value
        )

        is KHRecord.BoneMass -> BoneMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value
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
            distance = unit toNativeLengthFor value
        )

        is KHRecord.ElevationGained -> ElevationGainedRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            elevation = unit toNativeLengthFor value
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
            height = unit toNativeLengthFor value,
        )

        is KHRecord.Hydration -> HydrationRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            volume = unit toNativeVolumeFor value
        )

        is KHRecord.IntermenstrualBleeding -> IntermenstrualBleedingRecord(
            time = time.toJavaInstant(),
            zoneOffset = null
        )

        is KHRecord.LeanBodyMass -> LeanBodyMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value,
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
            flow = when (type) {
                KHMenstruationFlowType.Unknown -> MenstruationFlowRecord.FLOW_UNKNOWN
                KHMenstruationFlowType.Light -> MenstruationFlowRecord.FLOW_LIGHT
                KHMenstruationFlowType.Medium -> MenstruationFlowRecord.FLOW_MEDIUM
                KHMenstruationFlowType.Heavy -> MenstruationFlowRecord.FLOW_HEAVY
            },
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
                    power = sample.unit toNativePowerFor sample.value
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

        is KHRecord.Speed -> SpeedRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                SpeedRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    speed = sample.unit toNativeVelocityFor sample.value,
                )
            },
        )

        is KHRecord.RunningSpeed -> null

        is KHRecord.CyclingSpeed -> null

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
            weight = unit toNativeMassFor value,
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

internal infix fun KHUnit.BloodGlucose.toNativeBloodGlucoseFor(value: Double): BloodGlucose {
    return when (this) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> BloodGlucose.milligramsPerDeciliter(value)
        KHUnit.BloodGlucose.MillimolesPerLiter -> BloodGlucose.millimolesPerLiter(value)
    }
}

internal infix fun BloodGlucose.toDoubleValueFor(bloodGlucose: KHUnit.BloodGlucose): Double {
    return when (bloodGlucose) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> this.inMilligramsPerDeciliter
        KHUnit.BloodGlucose.MillimolesPerLiter -> this.inMillimolesPerLiter
    }
}

internal infix fun KHUnit.Energy.toNativeEnergyFor(value: Double): Energy = when (this) {
    KHUnit.Energy.Calorie -> value.calories
    KHUnit.Energy.Joule -> value.joules
    KHUnit.Energy.KiloCalorie -> value.kilocalories
    KHUnit.Energy.KiloJoule -> value.kilojoules
}

internal infix fun Energy.toDoubleValueFor(energy: KHUnit.Energy): Double = when (energy) {
    KHUnit.Energy.Calorie -> this.inCalories
    KHUnit.Energy.Joule -> this.inJoules
    KHUnit.Energy.KiloCalorie -> this.inKilocalories
    KHUnit.Energy.KiloJoule -> this.inKilojoules
}

internal infix fun KHUnit.Length.toNativeLengthFor(value: Double): Length = when (this) {
    is KHUnit.Length.Inch -> value.inches
    is KHUnit.Length.Meter -> value.meters
    is KHUnit.Length.Mile -> value.miles
}

internal infix fun Length.toDoubleValueFor(length: KHUnit.Length): Double = when (length) {
    KHUnit.Length.Inch -> this.inInches
    KHUnit.Length.Meter -> this.inMeters
    KHUnit.Length.Mile -> this.inMiles
}

internal infix fun KHUnit.Mass.toNativeMassFor(value: Double): Mass = when (this) {
    is KHUnit.Mass.Gram -> value.grams
    is KHUnit.Mass.Ounce -> value.ounces
    is KHUnit.Mass.Pound -> value.pounds
}

internal infix fun Mass.toDoubleValueFor(mass: KHUnit.Mass): Double = when (mass) {
    KHUnit.Mass.Gram -> this.inGrams
    KHUnit.Mass.Ounce -> this.inOunces
    KHUnit.Mass.Pound -> this.inPounds
}

internal infix fun KHUnit.Power.toNativePowerFor(value: Double): Power = when (this) {
    is KHUnit.Power.KilocaloriePerDay -> value.kilocaloriesPerDay
    is KHUnit.Power.Watt -> value.watts
}

internal infix fun Power.toDoubleValueFor(power: KHUnit.Power): Double = when (power) {
    KHUnit.Power.KilocaloriePerDay -> this.inKilocaloriesPerDay
    KHUnit.Power.Watt -> this.inWatts
}

internal infix fun KHUnit.Temperature.toNativeTemperatureFor(value: Double): Temperature {
    return when (this) {
        KHUnit.Temperature.Celsius -> value.celsius
        KHUnit.Temperature.Fahrenheit -> value.fahrenheit
    }
}

internal infix fun Temperature.toDoubleValueFor(temperature: KHUnit.Temperature): Double {
    return when (temperature) {
        KHUnit.Temperature.Celsius -> this.inCelsius
        KHUnit.Temperature.Fahrenheit -> this.inFahrenheit
    }
}

internal infix fun KHUnit.Velocity.toNativeVelocityFor(value: Double): Velocity = when (this) {
    is KHUnit.Velocity.KilometersPerHour -> value.kilometersPerHour
    is KHUnit.Velocity.MetersPerSecond -> value.metersPerSecond
    is KHUnit.Velocity.MilesPerHour -> value.milesPerHour
}

internal infix fun Velocity.toDoubleValueFor(velocity: KHUnit.Velocity): Double = when (velocity) {
    KHUnit.Velocity.KilometersPerHour -> this.inKilometersPerHour
    KHUnit.Velocity.MetersPerSecond -> this.inMetersPerSecond
    KHUnit.Velocity.MilesPerHour -> this.inMilesPerHour
}

internal infix fun KHUnit.Pressure.toNativePressureFor(value: Double): Pressure = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury -> value.millimetersOfMercury
}

internal infix fun Pressure.toDoubleValueFor(pressure: KHUnit.Pressure): Double = when (pressure) {
    KHUnit.Pressure.MillimeterOfMercury -> this.inMillimetersOfMercury
}

internal infix fun KHUnit.Volume.toNativeVolumeFor(value: Double): Volume = when (this) {
    KHUnit.Volume.FluidOunceUS -> value.fluidOuncesUs
    KHUnit.Volume.Liter -> value.liters
}

internal infix fun Volume.toDoubleValueFor(volume: KHUnit.Volume): Double = when (volume) {
    KHUnit.Volume.FluidOunceUS -> this.inFluidOuncesUs
    KHUnit.Volume.Liter -> this.inLiters
}
