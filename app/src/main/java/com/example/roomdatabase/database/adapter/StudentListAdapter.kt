package com.example.roomdatabase.database.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roomdatabase.database.bean.StudentTable
import com.example.roomdatabase.databinding.ProgressBarBinding
import com.example.roomdatabase.databinding.RecyclerviewItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StudentListAdapter(
    private val studentList: ArrayList<StudentTable?>,
    private val callEdit: (position: Int) -> Unit,
    private val callDelete: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) StudentListHolder(
            RecyclerviewItemBinding.inflate(
                (parent.context as Activity).layoutInflater,
                parent,
                false
            )
        ) else (ProgressViewHolder(
            ProgressBarBinding.inflate(
                (parent.context as Activity).layoutInflater,
                parent,
                false
            )
        ))

    }

    override fun getItemViewType(position: Int): Int {
        return if (studentList[position] == null) 2 else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StudentListHolder) {

            holder.itemCellBinding.studentDetail = this.studentList[position]
            Glide.with(holder.itemCellBinding.ivUserImage).load(studentList[position]!!.image)
                .circleCrop().into(holder.itemCellBinding.ivUserImage)

            holder.itemCellBinding.txtDate.text =
                SimpleDateFormat("dd.MM.yyyy", Locale.US).format(Date(studentList[position]!!.date))



            holder.itemCellBinding.btnEdit.setOnClickListener {
                callEdit(holder.adapterPosition)
            }


            holder.itemCellBinding.btnDelete.setOnClickListener {
                callDelete(holder.adapterPosition)
            }


        }
    }

    override fun getItemCount() = studentList.size

    class StudentListHolder(val itemCellBinding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(itemCellBinding.root)

    class ProgressViewHolder(val itemProgressBarBinding: ProgressBarBinding) :
        RecyclerView.ViewHolder(itemProgressBarBinding.root)

}




