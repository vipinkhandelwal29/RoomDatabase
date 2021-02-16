package com.example.roomdatabase

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.adapter.StudentListAdapter
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var adapter: StudentListAdapter? = null
    private var itemSelect: MenuItem? = null
    val dataList = ArrayList<SampleTable>()
    private var database: AppDatabase?=null

    override fun getLayoutId() = R.layout.activity_main
    override fun initControl() {



        val rv = findViewById<View>(R.id.recyclerview) as RecyclerView


        rv.layoutManager = LinearLayoutManager(this)


        val database = AppDatabase.getInstance(this)
        val studentLiveData = database.sampleDao().getData()

        studentLiveData.observe(this, androidx.lifecycle.Observer { studentList ->
            dataList.clear()
            dataList.addAll(studentList)
            adapter?.notifyDataSetChanged()
        })

        adapter = StudentListAdapter(dataList, callback = { position ->
            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("data", dataList[position])
            startActivity(intent)
            /*position ->

            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("name", dataList)
            startActivity(intent)*/
        }, callback2 = {
            database.sampleDao().delete(dataList[it].id)
            dataList.removeAt(it)
            adapter?.notifyDataSetChanged()
        })
        rv.adapter = adapter


        binding.fab.setOnClickListener {
            val intent = Intent(this, FormDetailActivity::class.java)
            startActivity(intent)
        }
        setSupportActionBar(binding.iToolbar.toolbar)

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else {
            super.onOptionsItemSelected(item)
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && resultCode == 101) {
            val updateData = data!!.getParcelableExtra<SampleTable>("data")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        itemSelect = menu!!.findItem(R.id.action_search)

        val searchView = itemSelect!!.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //queryStr = newText
/*val result = database!!.sampleDao().search(newText!!)*/
                val result = database!!.sampleDao().search().filter { it.name.toLowerCase().contains((newText ?: "").toLowerCase()) }
                dataList.clear()
                dataList.addAll(result)
                adapter!!.notifyDataSetChanged()
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
}