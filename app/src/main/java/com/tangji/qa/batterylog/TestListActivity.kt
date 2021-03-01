package com.tangji.qa.batterylog

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestListActivity : AppCompatActivity() {
    private val testListViewMode: TestListViewMode by viewModels {
        TestListViewModeFactory((application as BatteryLogApplication).repository)
    }
    private lateinit var list:List<Long>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:TestListRecyclerviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById<View>(R.id.test_list_recyclerview) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        list= listOf()
        adapter = TestListRecyclerviewAdapter(list)
        adapter.setClickListener(object : TestListRecyclerviewAdapter.ItemOnClickListener {
            override fun onClickChartButton(lo: Long) {
                startLineChartActivity(lo)
            }

            override fun onClickDeleteButton(lo: Long) {


                val builder = AlertDialog.Builder(this@TestListActivity)
                builder.setTitle(getString(R.string.alert))
                builder.setIcon(R.drawable.ic_baseline_warning_24)
                builder.setMessage(R.string.alert_massage)
                builder.setPositiveButton(getString(R.string.confirm)
                ) { dialog, which ->  delete(lo)}
                builder.setNegativeButton(getString(R.string.cancel)){ dialog, which ->dialog.dismiss()}
                builder.create().show()

            }

            override fun onClickListButton(lo: Long) {
                startLineListActivity(lo)
            }

        })
        recyclerView.adapter = adapter
        testListViewMode.allTestTitle.observe(owner = this){testTitle ->
            testTitle.distinct().let { adapter.setData(it) }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.testlistactivity, menu) // 将main填充到MainActivity里
        return true // 返回true 表示允许创建的菜单显示出来；返回false，创建的菜单将无法显示
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.delete_all ->{
                val builder = AlertDialog.Builder(this@TestListActivity)
                builder.setTitle(getString(R.string.alert))
                builder.setIcon(R.drawable.ic_baseline_warning_24)
                builder.setMessage(getString(R.string.alert_massage_all))
                builder.setPositiveButton(getString(R.string.confirm)
                ) { dialog, which ->  deleteAll()}
                builder.setNegativeButton(getString(R.string.cancel)){ dialog, which ->dialog.dismiss()}
                builder.create().show()

            }
        }
        return true
    }
    private fun startLineListActivity(lo: Long) {
        val intent = Intent(this@TestListActivity, LineListActivity::class.java)
        intent.putExtra("testTitle",lo)
        startActivity(intent)
    }

    private fun delete(lo: Long) {
        (application as BatteryLogApplication).applicationScope.launch(Dispatchers.IO) { testListViewMode.delete(lo) }
    }
    private fun deleteAll() {
        (application as BatteryLogApplication).applicationScope.launch(Dispatchers.IO) { testListViewMode.deleteAll() }
    }
    private fun startLineChartActivity(long: Long) {
        val intent = Intent(this@TestListActivity, LineChartActivity::class.java)
        intent.putExtra("testTitle",long)
        startActivity(intent)
    }
}