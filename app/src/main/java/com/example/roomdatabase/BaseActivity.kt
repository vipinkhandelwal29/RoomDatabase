package com.example.roomdatabase

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.roomdatabase.databinding.DailogProgressBinding
import com.example.roomdatabase.databinding.DailogToastMsgBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {


    protected lateinit var binding: T
    abstract fun getLayoutId(): Int
    abstract fun initControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        initControl()
    }

    protected lateinit var databaseReference: DatabaseReference
    protected fun initFirebaseDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("student")
    }

    protected lateinit var storageReference: StorageReference
    protected fun initFirebaseStorage() {
        storageReference = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://roomdatabase-eb297.appspot.com")
    }

    protected fun messageShow(Message: String) {
        val dialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val dialogValidation = DailogToastMsgBinding.inflate(layoutInflater)
        dialog.setContentView(dialogValidation.root)
        dialogValidation.tvMsg.text = Message
        dialogValidation.btnSubmit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    protected fun dialogProgress(Message: String)
    {
        val progressDialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val progressBinding = DailogProgressBinding.inflate(layoutInflater)
        progressDialog.setContentView(progressBinding.root)
        progressDialog.show()
        progressBinding.btnOk.setOnClickListener {
            progressDialog.dismiss()
        }
        progressBinding.progressBar.visibility = View.GONE
        progressBinding.btnOk.visibility = View.VISIBLE
        progressBinding.tvError.text = Message

    }

    protected fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }




}
