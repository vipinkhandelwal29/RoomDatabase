package com.example.roomdatabase

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.recyclerview_item.*


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


    protected fun checkConnectivity() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        if (null == activeNetwork) {
            val dialogBuilder = AlertDialog.Builder(this)
            val intent = Intent(this, MainActivity::class.java)
// set message of alert dialog
            dialogBuilder.setMessage("Make sure that WI-FI or mobile data is turned on, then try again")
// if the dialog is cancelable
                .setCancelable(false)
// positive button text and action
                .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, id ->
                    recreate()
                })
// negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })

// create dialog box
            val alert = dialogBuilder.create()
// set title for alert dialog box
            alert.setTitle("No Internet Connection")
            alert.setIcon(R.mipmap.ic_launcher)
// show alert dialog
            alert.show()
        }
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




    /*protected fun closeKeyBoard() {

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

    }*/
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }





}
