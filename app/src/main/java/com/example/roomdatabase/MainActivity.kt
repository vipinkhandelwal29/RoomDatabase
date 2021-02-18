package com.example.roomdatabase

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.adapter.StudentListAdapter
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.database.util.timeStampToDate
import com.example.roomdatabase.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var adapter: StudentListAdapter? = null
    private var itemSearch: MenuItem? = null
    val dataList = ArrayList<SampleTable>()
    private lateinit var database: AppDatabase
    private var queryStr: String? = null


    override fun getLayoutId() = R.layout.activity_main
    override fun initControl() {


        /*val rv = findViewById<View>(R.id.recyclerview) as RecyclerView
        rv.layoutManager = LinearLayoutManager(this)*/

        val manager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = manager

        database = AppDatabase.getInstance(this)
        val studentLiveData = database.sampleDao().getData()

        studentLiveData.observe(this, Observer {
            dataList.clear()
            dataList.addAll(it)
            adapter?.notifyDataSetChanged()
        })

        adapter = StudentListAdapter(dataList, callEdit = { position ->
            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("data", dataList[position])
            startActivity(intent)
        }, callDelete = {
            database.sampleDao().delete(dataList[it].id)
            dataList.removeAt(it)
            adapter?.notifyDataSetChanged()


        })

        binding.recyclerview.adapter = adapter


        binding.fab.setOnClickListener {
            val intent = Intent(this, FormDetailActivity::class.java)
            startActivity(intent)

        }
        setSupportActionBar(binding.iToolbar.toolbar)
        setTitle("Student List")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        itemSearch = menu!!.findItem(R.id.action_search)
        val searchView = itemSearch!!.actionView as SearchView
//        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setColorFilter(Color.WHITE)
//        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn).setColorFilter(Color.WHITE)
//        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).setColorFilter(Color.WHITE)
//        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button).setColorFilter(Color.WHITE)
        searchView.apply {
            searchView.setQueryHint("Search")
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    queryStr = newText
                    //val calendar = Calendar.getInstance()
                    //calendar.add(Calendar.YEAR, -18)
                    //Log.d("==>", "==>${timeStampToDate(calendar.timeInMillis)}")
                    val result = database.sampleDao().search().filter {
                        //it.name.toUpperCase().contains((newText ?: " "))
                        it.name.toLowerCase().contains((newText ?: ""))
                        // it.date >= calendar.timeInMillis
                    }
                    dataList.clear()
                    dataList.addAll(result)
                    adapter!!.notifyDataSetChanged()
                    return true
                }

            })

        }
        return true

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId

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
            data!!.getParcelableExtra<SampleTable>("name")
        }
    }


}