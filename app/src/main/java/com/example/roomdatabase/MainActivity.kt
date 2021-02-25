package com.example.roomdatabase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.adapter.StudentListAdapter
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.database.bean.StudentTable
import com.example.roomdatabase.database.retrofit.ApiClient
import com.example.roomdatabase.database.retrofit.ApiInterface
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.databinding.DailogProgressBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var adapter: StudentListAdapter? = null
    private var itemSearch: MenuItem? = null
    val dataList = ArrayList<StudentTable?>()
    private var token: String? = null
    private lateinit var database: AppDatabase
    private var queryStr: String? = null


    override fun getLayoutId() = R.layout.activity_main

    override fun initControl() {

        initFirebaseDatabase()
        setFirebaseEvent()
        getFirebaseData()

        val pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        token = pref.getString("token", null).toString()
        Log.d("Shared", "$token")

        setSupportActionBar(binding.iToolbar.toolbar)
        title = "Student List"


        val manager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = manager


        adapter = StudentListAdapter(dataList, callEdit = { position ->
            val intent = Intent(this, EditFormAcivity::class.java)
            intent.putExtra("data", dataList[position])
            startActivity(intent)
        }, callDelete = { position ->
            val progressDialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
            val progressBinding = DailogProgressBinding.inflate(layoutInflater)
            progressDialog.setContentView(progressBinding.root)
            progressDialog.show()
            progressBinding.btnOk.setOnClickListener {
                progressDialog.dismiss()
            }

            databaseReference.child(dataList[position]!!.id.toString()).removeValue()
                .addOnCompleteListener {
                    progressDialog.dismiss()
                }
                .addOnFailureListener {
                    progressBinding.progressBar.visibility = View.GONE
                    progressBinding.btnOk.visibility = View.VISIBLE
                    progressBinding.tvError.text = it.localizedMessage
                }
            /*databaseReference.child(dataList[it]!!.id.toString()).removeValue()
             database.sampleDao().delete(dataList[it]!!.id)
            dataList.removeAt(it)*/
            adapter?.notifyDataSetChanged()

        })

        /* databaseReference.addValueEventListener(object : ValueEventListener {
             override fun onCancelled(error: DatabaseError) {
                 
             }

             override fun onDataChange(snapshot: DataSnapshot) {
                 val studentList = ArrayList<StudentTable>()
                 snapshot.children.forEach {
                     studentList.add(
                         StudentTable(
                             id = it.child("id").value.toString().toLong(),
                             name = it.child("name").value.toString(),
                             gender = it.child("gender").value.toString(),
                             dob = it.child("dob").value.toString().toLong(),
                             address = it.child("address").value.toString(),
                             image = it.child("image").value.toString()
                         )
                     )
                 }
                 dataList.clear()
                 dataList.addAll(studentList)
                 adapter!!.notifyDataSetChanged()
             }
         })*/


        binding.recyclerview.adapter = adapter
        var isProgressBar = false


/*
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
                        for (i in 0..10) {
                            if (dataList.size < 10) {
                            dataList.addAll(dataList)
                            runOnUiThread { adapter!!.notifyDataSetChanged() }
                        }else {
                                dataList.addAll(dataList)
                            }
                    }


                    }.start()
                }
            }


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }
        })*/


        binding.fab.setOnClickListener()
        {
            //notificationClass()
            val intent = Intent(this, FormDetailActivity::class.java)
            startActivity(intent)

        }
        getData()
        binding.refreshLayout.setOnRefreshListener()
        {
            getFirebaseData()
        }


    }

    private fun getData() {
        val call = ApiClient.getApiClient().create(ApiInterface::class.java).fetchAllPosts()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("==>onFailure: ", call.toString())
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("==>onResponse: ", response.body()!!.string())

                if (response.isSuccessful) {

                }
            }

        })
    }

    private fun getFirebaseData() {
        binding.refreshLayout.isRefreshing = true
        databaseReference.get().addOnCompleteListener {
            val result = it.result
            // if (result!=null && result.childrenCount > 0){
            dataList.clear()
            result!!.children.forEach {
                //Log.d("==>", "msg ${it.child("name").value}")
                dataList.add(
                    StudentTable(
                        id = it.child("id").value.toString().toLong(),
                        name = it.child("name").value.toString(),
                        address = it.child("address").value.toString(),
                        image = it.child("image").value.toString(),
                        dob = it.child("dob").value.toString().toLong(),
                        gender = it.child("gender").value.toString(),
                        token = it.child("token").value.toString()

                    )
                )
            }
            setFirebaseEvent()
            adapter!!.notifyDataSetChanged()
        }
        binding.refreshLayout.isRefreshing = false
    }

    private fun setFirebaseEvent() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("==>", "onChildChanged: ${snapshot.value}")
                val existPosition = snapshot.child("id").value.toString()

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildAdded", "onChildAdded: ${snapshot}")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        itemSearch = menu!!.findItem(R.id.action_search)
        val searchView = itemSearch!!.actionView as SearchView
        searchView.apply {
            searchView.queryHint = "Search"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    queryStr = newText
                    database.sampleDao().search().filter {
                        //it.name.toUpperCase().contains((newText ?: " "))
                        it.name.toLowerCase(Locale.ROOT).contains((newText ?: ""))
                        // it.date >= calendar.timeInMillis
                    }
                    dataList.clear()
                    // dataList.addAll(result)
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

       private fun toGson(data: String): String {
           return Gson().toJson(data)
       }

  /*  private fun fromJson(data: String): SerializedBean {
        return Gson().fromJson(data, SerializedBean::class.java)
    }
*/
    private fun readFromAsset(): String {
        val filename = "student.json"
        val bufferReader = application.assets.open(filename).bufferedReader()
        val data = bufferReader.use { it.readText() }
        Log.d("readFromAsset: ", data)
        return data

    }
}


