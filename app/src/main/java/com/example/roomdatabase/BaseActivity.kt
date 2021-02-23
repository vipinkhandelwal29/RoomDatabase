package com.example.roomdatabase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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
            .getReferenceFromUrl("gs://roomdatabase-eb297.appspot.com");
    }

    protected fun messageShow(Message: String) {
        val dialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val dialogValidation = DailogToastMsgBinding.inflate(layoutInflater)
        dialog.setContentView(dialogValidation.root)
        dialogValidation.tvMsg.text = ""
        dialogValidation.btnSubmit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}
