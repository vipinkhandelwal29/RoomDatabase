package com.example.roomdatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.databinding.RecyclerviewItemBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun getLayoutId() =
        R.layout.activity_main


    override fun initControl() {

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, FormDetailActivity::class.java)
            startActivity(intent)
        }

        val myAdapter = MyAdapter()
        val linearLayoutInflater = LinearLayoutManager(this)
        binding.recyclerview.apply {
            adapter = myAdapter;
            layoutManager = linearLayoutInflater
        }

        val appDb:AppDatabase = AppDatabase.getInstance(this)
        appDb.sampleDao().getData().observe(this, Observer {
            myAdapter.setData(it)
        })
        setSupportActionBar(binding.iToolbar.toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
            /*  Toast.makeText(this, "ActionClicked", Toast.LENGTH_LONG).show()
              Log.d("btn", "menuBtnAdd  ")*/
        } else {

            return super.onOptionsItemSelected(item)
        }
    }

    fun Date.toSimpleString(): String {
        val format = SimpleDateFormat("dd/MM/yyy")
        return format.format(this)
    }

    companion object{
        class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
            private var _items: List<SampleTable> = emptyList()
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val viewBinding:RecyclerviewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.recyclerview_item,parent,false)
                return MyViewHolder(viewBinding)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                holder.bind(_items?.get(position))
            }

            override fun getItemCount(): Int {
                return _items.size
            }

            fun setData(items: List<SampleTable>) {
                _items = items
                notifyDataSetChanged()
            }

            class MyViewHolder(val itemBinding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
                fun bind(item: SampleTable) = with(itemBinding) {
                    itemBinding.studentDetail = item
//                    root.setOnClickListener { listener(item) }
                }
            }

        }
    }

}