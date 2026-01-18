package com.example.triptokailash.repository

import android.content.Context
import android.net.Uri
import com.example.triptokailash.model.PackageModel

interface PackageRepo {
    fun addPackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit)
    fun getPackage(packageId: String, callback: (Boolean, String, PackageModel?) -> Unit)
    fun getAllPackages(callback: (Boolean, String, List<PackageModel>?) -> Unit)
    fun updatePackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit)
    fun deletePackage(packageId: String, callback: (Boolean, String) -> Unit)

    // Image Upload Functionality
    fun uploadImage(context: Context, imageUri: Uri, callback:(String?)-> Unit)
}
