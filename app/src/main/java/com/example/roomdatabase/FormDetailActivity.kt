package com.example.roomdatabase

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityFormDetailBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FormDetailActivity() : BaseActivity<ActivityFormDetailBinding>(), View.OnClickListener {


    override fun getLayoutId() =
        R.layout.activity_form_detail


    override fun initControl() {

        val database = AppDatabase.getInstance(this)
        val sampleDao = database.sampleDao()

        /* sampleDao.getData().forEach {
             sampleDao.getData().forEach {
                 Log.i("==>id: ${it.id}", "==>: ${it.name}")
             }*/


        val cal = Calendar.getInstance()
        binding.tvDatePicker.text =
            SimpleDateFormat("dd MMM, yyyy").format(Date(cal.timeInMillis))

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvDatePicker.text = sdf.format(cal.time)

            }

        binding.tvDatePicker.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString()
            val date = binding.tvDatePicker.text.toString()
            val address = binding.etAddress.text.toString()
            val gender =
                if (binding.rbMale.isChecked) "Male"
                else if (binding.rbFemale.isChecked) "Female" else null

            if (name.isNullOrBlank()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else if (gender.isNullOrBlank()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else if (date.isNullOrBlank()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else if (address.isNullOrBlank()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else {

                val data = SampleTable(
                    id = 0,
                    name = name,
                    gender = gender,
                    date =cal.timeInMillis,
                    address = address
                )

                val id = sampleDao.insertData(data)
                Toast.makeText(this, "$id: $name: $gender $date  $address", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Log.d("result", "name: $name adr: $address gender: $gender  date: $date id :$id")

            }

        }



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

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    fun Date.toSimpleString(): String {
        val format = SimpleDateFormat("dd/MM/yyy")
        return format.format(this)
    }
}