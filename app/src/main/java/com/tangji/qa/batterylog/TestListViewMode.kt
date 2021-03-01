package com.tangji.qa.batterylog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.tangji.qa.batterylog.database.BatteryInfo
import com.tangji.qa.batterylog.database.BatteryInfoRepository

class TestListViewMode(private val repository: BatteryInfoRepository) : ViewModel(){
    suspend fun delete(lo: Long) {
        repository.delete(lo)
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }

    val allTestTitle: LiveData<List<Long>> = repository.getAllTestTitle().asLiveData()

}

class TestListViewModeFactory(private val repository: BatteryInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestListViewMode::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestListViewMode(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}