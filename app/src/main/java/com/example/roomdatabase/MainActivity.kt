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
        getData()


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
            getData()
        }
    }

    private fun getData() {
        val call = ApiClient.getApiClient().create(ApiInterface::class.java).fetchAllPosts()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("==>", "onFailure: ${call}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //Log.d("==>onResponse: ", response.body()!!.string())
                val jsonData = response.body()!!.string()
                if (response.isSuccessful) {

                    binding.refreshLayout.isRefreshing = true
                    val jsonObject = JSONObject(jsonData)
                    val keys: Iterator<String> = jsonObject.keys()

                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (jsonObject.get(key) is JSONObject) {

                            Log.d("==>jsonObject.get(key)", "${key} ")
                            val childData = jsonObject.getJSONObject(key)
                            Log.d("==>childData", "$childData ")

                            val data1 = Gson().fromJson<StudentTable>(childData.toString(), StudentTable::class.java)
                            Log.d("data1", "$data1 ")

                            dataList.add(data1)
                            /*dataList.add(
                                StudentTable(
                                    id = childData.getString("id").toLong(),
                                    image = childData.getString("image"),
                                    name = childData.getString("name"),
                                    gender = childData.getString("gender"),
                                    dob = childData.getLong("dob"),
                                    address = childData.getString("address"),
                                    token = childData.getString("token")
                                ))*/
                            Log.d("==>dataList", "$dataList ")
                        }
                        adapter!!.notifyDataSetChanged()
                        binding.refreshLayout.isRefreshing = false
                    }


                } else {

                }
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
                Log.e("==>", "onChildChanged: ${snapshot.value}")
                for (ds in snapshot.children) {

                    Log.d("==>Key", "${ds.key} ")
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //Log.d("onChildAdded", "onChildAdded: ${snapshot}")
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

    /*private fun toGson(data: String): String {
        return Gson().toJson(data)
    }*/
    /*private fun fromJson(data: String): StudentTable {
        return Gson().fromJson(data, StudentTable::class.java)
    }*/

    private fun readFromAsset(): String {
        val filename = "student.json"
        val bufferReader = application.assets.open(filename).bufferedReader()
        val data = bufferReader.use { it.readText() }
        Log.d("readFromAsset: ", data)
        return data

    }

}





















































/*
private fun getRetrofitdata() {
    val call = ApiClient.getApiClient().create(ApiInterface::class.java).getData()
    call.enqueue(object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.d("==>onFailure", call.toString())
            Toast.makeText(this@MainActivity, "Something went wrong $t", Toast.LENGTH_SHORT)
                .show()
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val jsonData = response.body()!!.string()
            Log.d("==>jsonData", jsonData)

            if (response.isSuccessful) {


                val jsonObject = JSONObject(jsonData)
                Log.d("==>jsonObject", jsonObject.toString())


                */
/*val root = JSONObject()
                val container = root.getJSONObject(jsonData)
                Log.d("====>","$container")*//*


                val keys = jsonObject.keys()

                while (keys.hasNext()) {
                    val key = keys.next()
                    Log.d(">>key", "$key")
                    val childData = jsonObject.getJSONObject(key)
                    val gson = Gson()
                    val itemObject = gson.fromJson<StudentTableFirebase>(
                        childData.toString(),
                        StudentTableFirebase::class.java
                    )
                    dataList.add(itemObject)

                    */
/*dataList.add(
                        StudentTableFirebase(
                            id = childData.getString("id").toLong(),
                            image = childData.getString("image"),
                            name = childData.getString("name"),
                            gender = childData.getString("gender"),
                            dob = childData.getLong("dob"),
                            address = childData.getString("address"),
                            token = childData.getString("token")
                        )
                    )*//*


                    Log.d(">>dataList", "${childData}")// do something with jsonObject here

                }
                */
/* var json = JSONArray(jsonData)
// ...

// ...
                 for (i in 0 until json.length()) {
                     val map =
                         HashMap<String, String>()
                     val e: JSONObject = json.getJSONObject(i)
                     map["id"] = i.toString()
                     map["name"] =  e.getString("name")
                     map["gender"] = e.getString("gender")
                     map["dob"] =  e.getString("dob")
                     map["address"] = e.getString("address")
                     map["image"] = e.getString("image")
                     map["token"] =  e.getString("token")

                     Log.d("====>","$map")


                     //dataList.add(map)
                 }*//*

                */
/*  try {
                    val jsonObject = JSONObject(jsonData)
                    val users = jsonObject.getJSONArray("student.json")
                    for (i in 0 until users.length()) {
                        val obj = users.getJSONObject(i)
                        val name = obj.get("student").toString()
                        Log.d("=====", name)

                    }
                } catch (e: JSONException) {
                }*//*



            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong ${response.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            */
/* val jsonObject = JSONObject(call.toString())*//*


            //Log.d("==>>>", jsonObject.toString())
            //val jsonObject = JSONObject(Gson().toJson(response.body()))

        }

    })
}*/
