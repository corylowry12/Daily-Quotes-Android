package com.cory.dailyquotes.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissions(private val activity: Activity, private val list: List<String>, private val code:Int) {

    // Check permissions at runtime
    fun checkPermissions(context: Context) : Boolean {
        return isPermissionsGranted() == PackageManager.PERMISSION_GRANTED
    }

    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(context : Context): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }

    // Show alert dialog to request permissions
    @SuppressLint("InflateParams")
    fun showAlert(context: Context) {
            requestPermissions(context)
        }

    // Request the permissions at run time
    private fun requestPermissions(context: Context) {
        val permission = deniedPermission(context)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        } else {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }
}