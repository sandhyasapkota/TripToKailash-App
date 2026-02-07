package com.example.triptokailash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.triptokailash.model.PackageModel
import com.example.triptokailash.repository.PackageRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class PackageManagementUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun addPackage_success_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val packageModel = PackageModel(
            packageId = "pkg123",
            packageName = "Kailash Mansarovar Tour",
            title = "Sacred Journey",
            description = "A spiritual journey to Mount Kailash",
            price = 150000.0,
            duration = "12 days",
            imageUrl = "https://example.com/image.jpg",
            category = "Pilgrimage"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Package added successfully")
            null
        }.`when`(repo).addPackage(eq(packageModel), any())

        var successResult = false
        var messageResult = ""

        viewModel.addPackage(packageModel) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Package added successfully", messageResult)

        verify(repo).addPackage(eq(packageModel), any())
    }

    @Test
    fun addPackage_failure_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val packageModel = PackageModel(
            packageId = "pkg123",
            packageName = "",  // Invalid empty name
            price = -100.0     // Invalid negative price
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Invalid package data: Name cannot be empty")
            null
        }.`when`(repo).addPackage(eq(packageModel), any())

        var successResult = true
        var messageResult = ""

        viewModel.addPackage(packageModel) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(!successResult)
        assertEquals("Invalid package data: Name cannot be empty", messageResult)

        verify(repo).addPackage(eq(packageModel), any())
    }

    @Test
    fun getPackage_success_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val packageId = "pkg123"
        val expectedPackage = PackageModel(
            packageId = packageId,
            packageName = "Kailash Mansarovar Tour",
            price = 150000.0
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, PackageModel?) -> Unit>(1)
            callback(true, "Package retrieved successfully", expectedPackage)
            null
        }.`when`(repo).getPackage(eq(packageId), any())

        var successResult = false
        var messageResult = ""
        var retrievedPackage: PackageModel? = null

        viewModel.getPackage(packageId) { success, msg, pkg ->
            successResult = success
            messageResult = msg
            retrievedPackage = pkg
        }

        assertTrue(successResult)
        assertEquals("Package retrieved successfully", messageResult)
        assertEquals(expectedPackage, retrievedPackage)

        verify(repo).getPackage(eq(packageId), any())
    }

    @Test
    fun getAllPackages_success_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val packageList = listOf(
            PackageModel(packageId = "pkg1", packageName = "Tour 1", price = 100000.0),
            PackageModel(packageId = "pkg2", packageName = "Tour 2", price = 200000.0)
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<PackageModel>?) -> Unit>(0)
            callback(true, "Packages retrieved successfully", packageList)
            null
        }.`when`(repo).getAllPackages(any())

        var successResult = false
        var messageResult = ""
        var retrievedPackages: List<PackageModel>? = null

        viewModel.getAllPackages { success, msg, packages ->
            successResult = success
            messageResult = msg
            retrievedPackages = packages
        }

        assertTrue(successResult)
        assertEquals("Packages retrieved successfully", messageResult)
        assertEquals(2, retrievedPackages?.size)
        assertEquals("Tour 1", retrievedPackages?.get(0)?.packageName)

        verify(repo).getAllPackages(any())
    }

    @Test
    fun deletePackage_success_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val packageId = "pkg123"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Package deleted successfully")
            null
        }.`when`(repo).deletePackage(eq(packageId), any())

        var successResult = false
        var messageResult = ""

        viewModel.deletePackage(packageId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Package deleted successfully", messageResult)

        verify(repo).deletePackage(eq(packageId), any())
    }

    @Test
    fun updatePackage_success_test() {
        val repo = mock<PackageRepo>()
        val viewModel = TestPackageViewModel(repo)

        val updatedPackage = PackageModel(
            packageId = "pkg123",
            packageName = "Updated Kailash Tour",
            price = 175000.0
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Package updated successfully")
            null
        }.`when`(repo).updatePackage(eq(updatedPackage), any())

        var successResult = false
        var messageResult = ""

        viewModel.updatePackage(updatedPackage) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Package updated successfully", messageResult)

        verify(repo).updatePackage(eq(updatedPackage), any())
    }
}

// Test-specific PackageViewModel for dependency injection
class TestPackageViewModel(private val repo: PackageRepo) {
    
    fun addPackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        repo.addPackage(packageModel, callback)
    }

    fun getPackage(packageId: String, callback: (Boolean, String, PackageModel?) -> Unit) {
        repo.getPackage(packageId, callback)
    }

    fun getAllPackages(callback: (Boolean, String, List<PackageModel>?) -> Unit) {
        repo.getAllPackages(callback)
    }

    fun updatePackage(packageModel: PackageModel, callback: (Boolean, String) -> Unit) {
        repo.updatePackage(packageModel, callback)
    }

    fun deletePackage(packageId: String, callback: (Boolean, String) -> Unit) {
        repo.deletePackage(packageId, callback)
    }
}