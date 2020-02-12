package com.luckyboy.jetpacklearn

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var locationObserver: LocationObserver

    // 获取权限
    private val REQUEST_CODE_PERMISSIONS = 101

    private val KEY_PERMISSION_REQUEST_COUNT = "KEY_PERMISSION_REQUEST_COUNT"
    private val MAX_NUMBER_REQUEST_PERMISSIONS = 2

    private val permissions = Arrays.asList(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private var permissionRequestCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.let {
            permissionRequestCount = it.getInt(KEY_PERMISSION_REQUEST_COUNT, 0)
        }
        // 获取权限
        requestPermissionsIfNecessary()
        locationObserver = LocationObserver()
        lifecycle.addObserver(locationObserver)
    }


    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                Toast.makeText(
                    this, R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun checkAllPermissions(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            hasPermission = hasPermission and isPermissionGranted(permission)
        }
        return hasPermission
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary()
        }
    }


}
