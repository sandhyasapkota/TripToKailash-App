package com.example.triptokailash.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triptokailash.model.PackageModel
import com.example.triptokailash.repository.PackageRepo
import com.example.triptokailash.repository.PackageRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackageViewModel() : ViewModel() {

    private val repo: PackageRepo = PackageRepoImpl()

    private val _package = MutableStateFlow<PackageModel?>(null)
    val `package`: StateFlow<PackageModel?> = _package.asStateFlow()

    private val _allPackages = MutableStateFlow<List<PackageModel>>(emptyList())
    val allPackages: StateFlow<List<PackageModel>> = _allPackages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _imageUrl = MutableStateFlow<String>("")
    val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    init {
        getAllPackages()
    }

    fun getPackage(packageId: String) {
        viewModelScope.launch {
            _loading.value = true
            repo.getPackage(packageId) { success, _, packageModel ->
                if (success) {
                    _package.value = packageModel
                    _imageUrl.value = packageModel?.imageUrl ?: "" // Pre-fill image URL for editing
                }
                _loading.value = false
            }
        }
    }

    fun getAllPackages() {
        viewModelScope.launch {
            _loading.value = true
            repo.getAllPackages { success, _, packageList ->
                if (success) {
                    _allPackages.value = packageList ?: emptyList()
                }
                _loading.value = false
            }
        }
    }

    fun addPackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repo.addPackage(packageModel, callback)
        }
    }

    fun updatePackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repo.updatePackage(packageModel, callback)
        }
    }

    fun deletePackage(packageId: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repo.deletePackage(packageId) { success, message ->
                if (success) {
                    getAllPackages() // Refresh the list after deletion
                }
                callback(success, message)
            }
        }
    }

    fun uploadImage(context: Context, imageUri: Uri) {
        Log.d("PackageViewModel", "uploadImage called with URI: $imageUri")
        viewModelScope.launch {
            _loading.value = true
            repo.uploadImage(context, imageUri) { uploadedUrl ->
                Log.d("PackageViewModel", "Upload callback received. URL: $uploadedUrl")
                if (uploadedUrl != null) {
                    Log.d("PackageViewModel", "Setting imageUrl to: $uploadedUrl")
                    _imageUrl.value = uploadedUrl // Update the state with the new URL
                } else {
                    Log.e("PackageViewModel", "Upload failed - URL is null")
                }
                _loading.value = false
            }
        }
    }

    fun setImageUrl(url: String) {
        _imageUrl.value = url
    }
}