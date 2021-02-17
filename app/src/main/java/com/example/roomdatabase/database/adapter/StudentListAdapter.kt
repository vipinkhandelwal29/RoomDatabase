package com.example.roomdatabase.database.adapter

import android.app.Activity
import android.app.DatePickerDialog
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.database.util.timeStampToDate
import com.example.roomdatabase.databinding.RecyclerviewItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StudentListAdapter(private val studentList: ArrayList<SampleTable>,
                         private val callEdit: (position: Int) -> Unit,
                         private val callDelete: (position: Int) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  StudentListHolder(RecyclerviewItemBinding.inflate((parent.context as Activity).layoutInflater,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StudentListHolder) {

            holder.itemCellBinding.studentDetail = studentList[position]
            Glide.with(holder.itemCellBinding.ivUserImage).load(studentList[position].image).circleCrop().into(holder.itemCellBinding.ivUserImage)

            holder.itemCellBinding.txtDate.text =
                SimpleDateFormat("dd.MM.yyyy", Locale.US).format(Date(studentList[position].date))



            holder.itemCellBinding.btnEdit.setOnClickListener {
                callEdit(holder.adapterPosition)
            }


            holder.itemCellBinding.btnDelete.setOnClickListener {
                callDelete(studentList[holder.adapterPosition].id)
            }


        }
    }
    override fun getItemCount() = studentList.size

    class StudentListHolder(val itemCellBinding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(itemCellBinding.root)

}




