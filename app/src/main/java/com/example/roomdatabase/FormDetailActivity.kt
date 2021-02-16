package com.example.roomdatabase

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityFormDetailBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FormDetailActivity() : BaseActivity<ActivityFormDetailBinding>(), View.OnClickListener {

    private var imageFilePath: String? = null
    private var id = 0
    private var sampleTable: SampleTable? = null
    private var photo: String? = null

    override fun getLayoutId() =
        R.layout.activity_form_detail


    override fun initControl() {

        val database = AppDatabase.getInstance(this)
        val sampleDao = database.sampleDao()

        /* sampleDao.getData().forEach {
             sampleDao.getData().forEach {
                 Log.i("==>id: ${it.id}", "==>: ${it.name}")
             }*/
        sampleTable = intent.getParcelableExtra("name")

        if (sampleTable != null) {
            binding.etName.setText(sampleTable!!.name)
            binding.etAddress.setText(sampleTable!!.address)
            binding.tvDatePicker.setText(sampleTable!!.date.toString())
            sampleTable!!.image
            if (sampleTable!!.gender == "Male") binding.rbMale.isChecked =
                true else if (sampleTable!!.gender == "Female") binding.rbFemale.isChecked =
                true else null
        }

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                binding.tvDatePicker.text =
                    SimpleDateFormat("dd.MM.yyyy", Locale.US).format(cal.time)
            }

        binding.tvDatePicker.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }



        binding.ivUserImage.setOnClickListener {
            val permission = arrayOf(READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permission, 101)
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString()
            val date = binding.tvDatePicker.text.toString()
            val address = binding.etAddress.text.toString()
            val gender =
                if (binding.rbMale.isChecked) "Male"
                else if (binding.rbFemale.isChecked) "Female" else null

            if (name.isBlank()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else if (gender.isNullOrBlank()) {
                Toast.makeText(this, "Please enter gender", Toast.LENGTH_LONG).show()
            } else if (date.isBlank()) {
                Toast.makeText(this, "Please enter date", Toast.LENGTH_LONG).show()
            } else if (address.isBlank()) {
                Toast.makeText(this, "Please enter address", Toast.LENGTH_LONG).show()
            } else if (imageFilePath == null) {
                Toast.makeText(this, "Please enter image", Toast.LENGTH_LONG).show()
            } else {

                val data = SampleTable(
                    id = id,
                    name = name,
                    gender = gender,
                    date = cal.timeInMillis,
                    address = address,
                    image = imageFilePath!!
                )

                setResult(Activity.RESULT_OK, intent)
                finish()
                binding.etName.text = null
                binding.etAddress.text = null
                binding.rbMale.text = null
                binding.rbFemale.text = null
                binding.tvDatePicker.text = null

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 101) {
            val status = grantResults[0] == PackageManager.PERMISSION_GRANTED
            Log.d("==>", "onRequestPermissionsResult: $status")
            if (status) {
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, 201)
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201 && resultCode == Activity.RESULT_OK) {
            val intentDate = data?.data
            val tempFile = File(cacheDir, "${System.currentTimeMillis()},jpg")
            val out = FileOutputStream(tempFile)
            val inputStream = contentResolver.openInputStream(intentDate!!)

            try {
                try {
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream!!.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                } finally {
                    out.close()
                }
            } finally {
                inputStream!!.close()
            }
            imageFilePath = tempFile.absolutePath
            Glide.with(this).load(imageFilePath).circleCrop().into(binding.ivUserImage)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_search) {

        }

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