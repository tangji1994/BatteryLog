package com.tangji.qa.batterylog

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tangji.qa.batterylog.database.BatteryInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*


class LineListActivity : AppCompatActivity() {

    private lateinit var list:List<BatteryInfo>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:LineListRecyclerviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_list)
//        window.statusBarColor=getColor(R.color.button_color)
//        window.navigationBarColor=getColor(R.color.button_color)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title=longToDate(intent.getLongExtra("testTitle", 0))
        recyclerView = findViewById<View>(R.id.lineListActivity_recyclerView) as RecyclerView

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        list= listOf()
        adapter = LineListRecyclerviewAdapter(list)
        recyclerView.adapter=adapter
        val lineCharViewMode : LineChartViewMode by viewModels {
            LineChartViewModeFactory(
                (application as BatteryLogApplication).repository, intent.getLongExtra(
                    "testTitle",
                    0
                )
            )
        }
        lineCharViewMode.testLog.observe(owner = this){ testLog ->
            testLog.let {
                adapter.setData(it)
                list=it
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.linelistactivity, menu) // ???main?????????MainActivity???
        return true // ??????true ????????????????????????????????????????????????false?????????????????????????????????
    }
    @SuppressLint("NewApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.save_as -> {
                (application as BatteryLogApplication).applicationScope.launch {

                    if (withContext(this.coroutineContext) {
                            saveAsCSV("$title.csv", list)
                        }) {
                        Snackbar.make(recyclerView, getString(R.string.save_data_as)+
                                File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "$title.csv"
            ).path, Snackbar.LENGTH_LONG)
//                            .setAction(
//                                "??????????????????"
//                            ) {
//                                //???????????????????????????????????????????????????
//                                openAssignFolder()
//                            }
                            .show()
                    }
                }
            }
        }
        return true
    }
//    private fun openAssignFolder() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.addCategory(Intent.CATEGORY_DEFAULT)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        val contentUri = FileProvider.getUriForFile(
//            applicationContext,
//            "com.tangji.qa.batterylog.provider",
//            File(
//                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
//                "$title.csv"
//            )
//        )
//        intent.setDataAndType(contentUri, "file/*")
//        try {
//            startActivity(intent)
//            //            startActivity(Intent.createChooser(intent,"??????????????????"));
//        } catch (e: ActivityNotFoundException) {
//            e.printStackTrace()
//        }
//    }
    private fun saveAsCSV(fileName: String, dataList: List<BatteryInfo>) : Boolean{
        val file = File(
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            fileName
        )
        var isSuccessful: Boolean
        var bufferedWriter: BufferedWriter? =null
        try {
            val fileOutputStream = FileOutputStream(file)
            bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))
            for (item in dataList) {
                val line =
                    item.time.toString() + "," + item.level.toString() + "," + item.temperature.toString() + "," + item.voltage.toString() + "," + item.status.toString() + "\r\n"
                bufferedWriter.write(line)
            }
            isSuccessful=true
        }catch (e: Exception) {
            e.printStackTrace()
            isSuccessful = false
        }finally{
            try{
                bufferedWriter?.close()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return isSuccessful
    }

    private fun longToDate(lo: Long): String {
        val date = Date(lo)
        val sd = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sd.format(date)
    }
}