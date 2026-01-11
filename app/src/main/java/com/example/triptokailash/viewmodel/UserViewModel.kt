package com.example.triptokailash.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.triptokailash.model.UserModel
import com.example.triptokailash.repository.UserRepo
import com.example.triptokailash.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseUser

// Corrected ViewModel with a no-argument constructor.
// It creates its own repository, which is a standard and robust pattern.
class UserViewModel() : ViewModel() {

    private val repo: UserRepo = UserRepoImpl()

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers: LiveData<List<UserModel>?> get() = _allUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    
    private val _profileImageUrl = MutableLiveData<String>("")
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model, callback)
    }
    
    fun uploadProfileImage(context: Context, imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        Log.d("UserViewModel", "uploadProfileImage called with URI: $imageUri")
        _loading.postValue(true)
        repo.uploadProfileImage(context, imageUri) { uploadedUrl ->
            Log.d("UserViewModel", "Upload callback received. URL: $uploadedUrl")
            _loading.postValue(false)
            if (uploadedUrl != null) {
                Log.d("UserViewModel", "Setting profileImageUrl to: $uploadedUrl")
                _profileImageUrl.postValue(uploadedUrl)
                callback(true, uploadedUrl)
            } else {
                Log.e("UserViewModel", "Upload failed - URL is null")
                callback(false, null)
            }
        }
    }

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(userId, callback)
    }

    fun getUserById(userId: String) {
        _loading.postValue(true)
        repo.getUserById(userId) { success, _, userModel ->
            if (success) {
                _user.postValue(userModel)
            }
            _loading.postValue(false)
        }
    }

    fun getAllUser() {
        _loading.postValue(true)
        repo.getAllUser { success, _, userList ->
            if (success) {
                _allUsers.postValue(userList)
            }
            _loading.postValue(false)
        }
    }

    fun resetPassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        repo.resetPassword(newPassword, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }
}
