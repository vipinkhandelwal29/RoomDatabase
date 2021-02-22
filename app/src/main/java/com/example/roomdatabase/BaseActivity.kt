package com.example.roomdatabase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.roomdatabase.database.bean.SampleTable
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: T
    abstract fun getLayoutId(): Int
    abstract fun initControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        initControl()
    }

    protected lateinit var myRef: DatabaseReference

    protected fun initData()
    {
        myRef = Firebase.database.getReference("students table")
    }



}
