/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tangji.qa.batterylog.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class BatteryInfoRepository constructor(batteryInfoDao: BatteryInfoDao) {
    private val batteryInfoDao:BatteryInfoDao=batteryInfoDao
    private var title:Long = 0

    constructor(title: Long,batteryInfoDao: BatteryInfoDao):this(batteryInfoDao){
        this.title=title
    }

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.


    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(batteryInfo: BatteryInfo) {
        batteryInfoDao.insert(batteryInfo)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getBatteryInfo(): BatteryInfo {
        return batteryInfoDao.getBatteryInfo()
    }


    fun getAllTestTitle(): Flow<List<Long>> {
        return batteryInfoDao.getAllTestTitle()
    }

    fun getTestLog(title: Long):Flow<List<BatteryInfo>>{
        return batteryInfoDao.getTestLog(title)
    }

    fun getTestLog1(title: Long):List<BatteryInfo>{
        return batteryInfoDao.getTestLog1(title)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(lo: Long) {
        batteryInfoDao.delete(lo)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        batteryInfoDao.deleteAll()
    }
}
