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
import com.example.triptokailash.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl: UserRepo {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users")
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

    override fun uploadProfileImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        try {
            ensureMediaManagerInitialized(context)
            val userId = auth.currentUser?.uid ?: "unknown"
            val fileName = "profile_${userId}_${System.currentTimeMillis()}"
            
            Log.d("UserRepoImpl", "Starting upload for: $fileName")

            MediaManager.get().upload(imageUri)
                .option("public_id", fileName)
                .option("folder", "profile_images")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("UserRepoImpl", "Upload started: $requestId")
                    }
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("UserRepoImpl", "Upload progress: $bytes / $totalBytes")
                    }
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val secureUrl = resultData?.get("secure_url") as? String
                        Log.d("UserRepoImpl", "Upload success! URL: $secureUrl")
                        mainHandler.post { callback(secureUrl) }
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("UserRepoImpl", "Upload error: ${error?.description}")
                        mainHandler.post { callback(null) }
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.w("UserRepoImpl", "Upload rescheduled: ${error?.description}")
                    }
                }).dispatch()
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Upload exception: ${e.message}")
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

    override fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Login successful")
            } else {
                callback(false, task.exception?.message)
            }
        }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Registration successful", auth.currentUser?.uid ?: "")
            } else {
                callback(false, task.exception?.message ?: "", "")
            }
        }
    }

    override fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        userRef.child(userId).setValue(model).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "User added to database")
            } else {
                callback(false, task.exception?.message ?: "")
            }
        }
    }

    override fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        // Use a map to safely update only the changed fields.
        // This prevents overwriting other important data like userEmail.
        val updates = mapOf(
            "userName" to model.userName,
            "address" to model.address,
            "contactNumber" to model.contactNumber,
            "gender" to model.gender,
            "profilePictureUrl" to model.profilePictureUrl
        )

        userRef.child(userId).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Profile updated successfully!")
            } else {
                callback(false, task.exception?.message ?: "An unknown error occurred.")
            }
        }
    }

    override fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        userRef.child(userId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Account deleted")
            } else {
                callback(false, task.exception?.message ?: "")
            }
        }
    }

    override fun getUserById(userId: String, callback: (Boolean, String, UserModel?) -> Unit) {
        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true, "User fetched", user)
                } else {
                    callback(false, "User not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>) -> Unit) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserModel>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(UserModel::class.java)
                        user?.let { userList.add(it) }
                    }
                }
                callback(true, "Users fetched", userList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun resetPassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Password updated successfully")
            } else {
                callback(false, task.exception?.message ?: "An error occurred")
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.message.toString())
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Password reset email sent")
            } else {
                callback(false, task.exception?.message ?: "")
            }
        }
    }
}
