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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var adapter: StudentListAdapter? = null
    private var itemSearch: MenuItem? = null
    private val dataList = ArrayList<StudentTable?>()
    private var token: String? = null
    private lateinit var database: AppDatabase
    private var queryStr: String? = null


    override fun getLayoutId() = R.layout.activity_main

    override fun initControl() {

        initFirebaseDatabase()
        setFirebaseEvent()
        //getRetrofitData()


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
            deleteRetrofitData(position)

            /* databaseReference.child(dataList[position]!!.id.toString()).removeValue()
                 .addOnCompleteListener {
                     progressDialog.dismiss()
                 }
                 .addOnFailureListener {
                     progressBinding.progressBar.visibility = View.GONE
                     progressBinding.btnOk.visibility = View.VISIBLE
                     progressBinding.tvError.text = it.localizedMessage
                 }*/
            adapter?.notifyDataSetChanged()

        })

       /* if (dataList.isEmpty()) {
            binding.recyclerview.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        }
        else {
            binding.recyclerview.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }*/

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
            startActivityForResult(intent, 555)

        }

        binding.refreshLayout.setOnRefreshListener()
        {
            getRetrofitData()
        }

        getRetrofitData()
        binding.refreshLayout.isRefreshing = true
    }


    private fun deleteRetrofitData(positon: Int) {
        val progressDialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val progressBinding = DailogProgressBinding.inflate(layoutInflater)
        progressDialog.setContentView(progressBinding.root)
        progressDialog.show()
        progressBinding.btnOk.setOnClickListener {
            progressDialog.dismiss()
        }
        val call = ApiClient.getApiClient().create(ApiInterface::class.java)
            .deleteData(dataList[positon]!!.id.toString())
        call.enqueue(object : Callback<StudentTable> {
            override fun onFailure(call: Call<StudentTable>, t: Throwable) {
                progressBinding.progressBar.visibility = View.GONE
                progressBinding.btnOk.visibility = View.VISIBLE
                progressBinding.tvError.text = "Check Connectivity"

            }

            override fun onResponse(call: Call<StudentTable>, response: Response<StudentTable>) {
                dataList.removeAt(positon)
                progressDialog.dismiss()
                adapter?.notifyDataSetChanged()
            }

        })
    }


    private fun getRetrofitData() {
        binding.refreshLayout.isRefreshing = true
        dataList.clear()
        val call = ApiClient.getApiClient().create(ApiInterface::class.java).fetchAllPosts()
        call.enqueue(object : Callback<HashMap<String, StudentTable>> {
            override fun onResponse(
                call: Call<HashMap<String, StudentTable>>,
                response: Response<HashMap<String, StudentTable>>
            ) {
                if (response.isSuccessful) {
                    if (response.body() == null) {
                        binding.emptyView.setVisibility(View.VISIBLE)
                        binding.refreshLayout.isRefreshing = false
                        adapter!!.notifyDataSetChanged()
                        return
                    } else {
                        binding.refreshLayout.isRefreshing = false
                        dataList.addAll(response.body()!!.values)
                        binding.emptyView.setVisibility(View.GONE)
                        binding.refreshLayout.isRefreshing = false
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    binding.refreshLayout.isRefreshing = false
                    messageShow(response.message())
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<HashMap<String, StudentTable>>, t: Throwable) {
                binding.emptyView.setVisibility(View.VISIBLE)
                binding.refreshLayout.isRefreshing = false
                messageShow("Please check internet connection")
            }
        })
    }


    /* private fun getFirebaseData() {
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
     }*/

    private fun setFirebaseEvent() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
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
        if (requestCode == 555 && resultCode == 555) {
            val intent =data!!.getParcelableExtra<StudentTable>("name")
            val dataPosition = dataList.find { it!!.id == intent!!.id }
            dataList.add(dataPosition)

            Log.d("==>",dataPosition!!.id.toString())

            val positon = dataList.indexOf(dataPosition)
            dataList.add(positon,intent)
            adapter!!.notifyDataSetChanged()
        }
        else if (requestCode == 552 && resultCode == 552)
        {
            val intent =data?.data
        }
    }

}


