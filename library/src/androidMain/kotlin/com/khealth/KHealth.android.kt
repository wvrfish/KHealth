package com.khealth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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

    private fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
        val entry = this@toPermissions
        return buildSet {
            val record = when (entry) {
                is KHPermission.HeartRate -> HeartRateRecord::class
                is KHPermission.StepCount -> StepsRecord::class
            }
            if (entry.readRequested) {
                add(HealthPermission.getReadPermission(record) to KHPermissionType.Read)
            }
            if (entry.writeRequested) {
                add(HealthPermission.getWritePermission(record) to KHPermissionType.Write)
            }
        }
    }
}

private enum class KHPermissionType { Read, Write }
