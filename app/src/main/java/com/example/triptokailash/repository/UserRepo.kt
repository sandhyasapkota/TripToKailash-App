package com.example.triptokailash.repository

import android.content.Context
import android.net.Uri
import com.example.triptokailash.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepo {
//    {
//       "success: false,
    //       "message": "Invalid email or password"
//    }
    fun login(email: String, password: String , callback: (Boolean, String?) -> Unit)
    
    fun uploadProfileImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun register(
        email: String,password: String, callback: (Boolean, String, String) -> Unit
    )

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )
    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    )
    fun getUserById(
        userId: String,
        callback: (Boolean,String, UserModel?) -> Unit
    )
    fun getAllUser(
        callback: (Boolean, String, List<UserModel>) -> Unit
    )
    fun resetPassword(
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )
    fun getCurrentUser(): FirebaseUser?

    fun logout(
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )
}
