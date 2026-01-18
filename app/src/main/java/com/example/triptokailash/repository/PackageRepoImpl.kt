package com.example.triptokailash.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.triptokailash.model.PackageModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PackageRepoImpl : PackageRepo {
    private val database = FirebaseDatabase.getInstance().getReference("packages")
    private val mainHandler = Handler(Looper.getMainLooper())

    private fun ensureMediaManagerInitialized(context: Context) {
        try {
            MediaManager.get()
        } catch (e: IllegalStateException) {
            val config = mapOf(
                "cloud_name" to "dxzef9wvx",
                "api_key" to "623733451967312",
                "api_secret" to "_-qiPO1ndj1tgOwFfWvEVUkCIBo"
            )
            MediaManager.init(context.applicationContext, config)
        }
    }

    override fun addPackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        val packageId = database.push().key ?: run {
            callback(false, "Couldn't create a new package ID.")
            return
        }
        val newPackage = packageModel.copy(packageId = packageId)
        database.child(packageId).setValue(newPackage)
            .addOnSuccessListener { callback(true, "Package added successfully!") }
            .addOnFailureListener { callback(false, "Failed to add package: ${it.message}") }
    }

    override fun getPackage(packageId: String, callback: (Boolean, String, PackageModel?) -> Unit) {
        database.child(packageId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val pkg = snapshot.getValue(PackageModel::class.java)
                callback(true, "Package found.", pkg?.copy(packageId = snapshot.key))
            } else {
                callback(false, "Package not found.", null)
            }
        }.addOnFailureListener { callback(false, "Error fetching package: ${it.message}", null) }
    }

    override fun getAllPackages(callback: (Boolean, String, List<PackageModel>?) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val packages = snapshot.children.mapNotNull { it.getValue(PackageModel::class.java)?.copy(packageId = it.key) }
                callback(true, "Packages loaded.", packages)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to load packages: ${error.message}", null)
            }
        })
    }

    override fun updatePackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        val packageId = packageModel.packageId ?: run {
            callback(false, "Cannot update package without an ID.")
            return
        }
        database.child(packageId).setValue(packageModel)
            .addOnSuccessListener { callback(true, "Package updated successfully!") }
            .addOnFailureListener { callback(false, "Failed to update package: ${it.message}") }
    }

    override fun deletePackage(packageId: String, callback: (Boolean, String) -> Unit) {
        database.child(packageId).removeValue()
            .addOnSuccessListener { callback(true, "Package deleted successfully.") }
            .addOnFailureListener { callback(false, "Failed to delete package: ${it.message}") }
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        try {
            ensureMediaManagerInitialized(context)
            val fileName = getFileNameFromUri(context, imageUri) ?: "package_image_${System.currentTimeMillis()}"
            
            Log.d("PackageRepoImpl", "Starting upload for: $fileName")

            MediaManager.get().upload(imageUri)
                .option("public_id", fileName)
                .option("folder", "package_images")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("PackageRepoImpl", "Upload started: $requestId")
                    }
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("PackageRepoImpl", "Upload progress: $bytes / $totalBytes")
                    }
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val secureUrl = resultData?.get("secure_url") as? String
                        Log.d("PackageRepoImpl", "Upload success! URL: $secureUrl")
                        mainHandler.post { callback(secureUrl) }
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("PackageRepoImpl", "Upload error: ${error?.description}")
                        mainHandler.post { callback(null) }
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.w("PackageRepoImpl", "Upload rescheduled: ${error?.description}")
                    }
                }).dispatch()
        } catch (e: Exception) {
            Log.e("PackageRepoImpl", "Upload exception: ${e.message}")
            callback(null)
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }?.substringBeforeLast(".")
    }
}
