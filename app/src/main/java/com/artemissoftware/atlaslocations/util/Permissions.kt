package com.artemissoftware.atlaslocations.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.artemissoftware.atlaslocations.MainActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


object Permissions {

    /**
     * Return the current state of the permissions needed.
     */
    fun checkPermissions(context : Context) = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun requestPermissions(context : Context, listener : PermissionListener) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(listener)
            .check()
    }

    fun requestPermissions_(context: Context, listener: MultiplePermissionsListener) {
        Dexter.withContext(context)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(listener)
            .check()
    }
}