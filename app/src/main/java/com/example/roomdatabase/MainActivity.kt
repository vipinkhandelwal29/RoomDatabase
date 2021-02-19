package com.example.roomdatabase

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.adapter.StudentListAdapter
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityMainBinding
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var adapter: StudentListAdapter? = null
    private var itemSearch: MenuItem? = null
    val dataList = ArrayList<SampleTable?>()
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
            database.sampleDao().delete(dataList[it]!!.id)
            dataList.removeAt(it)
            adapter?.notifyDataSetChanged()


        })

        binding.recyclerview.adapter = adapter
        var isProgressBar = false




        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val postion = manager.findLastVisibleItemPosition()
                super.onScrolled(recyclerView, dx, dy)

                if (postion == dataList.size - 1 && !isProgressBar) {
                    dataList.add(null)
                    isProgressBar = true
                    adapter!!.notifyItemInserted(dataList.size - 1)
                    Thread {

                        sleep(2000)
                        dataList.remove(null)
                        dataList.addAll(dataList)
                        runOnUiThread {
                            adapter!!.notifyDataSetChanged()
                            isProgressBar = false
                        }
                        /*for (i in 0..10) {
                            if (dataList.size < 10) {
                            dataList.addAll(dataList)
                            runOnUiThread { adapter!!.notifyDataSetChanged() }
                        }else {
                                dataList.addAll(dataList)
                            }
                    }*/


                    }.start()
                }
            }

            /*if (position == dataStudentList.size - 1 && !progressAdd) {
                dataStudentList.add(null)
                adapter!!.notifyItemInserted(dataStudentList.size - 1)
                progressAdd = true
                Thread {
                    sleep(5000)
                    dataStudentList.remove(null)
                    if (dataStudentList.size >10)
                        dataStudentList.addAll(dataStudentList.subList(0,10))
                    else
                        dataStudentList.addAll(dataStudentList)
                    runOnUiThread { adapter!!.notifyDataSetChanged()
                    }
                    progressAdd = false
                }.start()
            }*/


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }
        })


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
/*        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setColorFilter(Color.WHITE)
        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn).setColorFilter(Color.WHITE)
        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).setColorFilter(Color.WHITE)
        searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button).setColorFilter(Color.WHITE)*/
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
            finish()
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