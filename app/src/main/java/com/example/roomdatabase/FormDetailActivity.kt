package com.example.roomdatabase

import android.Manifest.permission.*
import android.app.Activity
import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.roomdatabase.database.AppDatabase
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.databinding.ActivityFormDetailBinding
import com.example.roomdatabase.databinding.DailogImagePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormDetailActivity() : BaseActivity<ActivityFormDetailBinding>(), View.OnClickListener {

    private var imageFilePath: String? = null
    private var dataList = ArrayList<SampleTable>()
    private var id = 0
    private var photo: String? = null
    private var studentData: SampleTable? = null

    override fun getLayoutId() =
        R.layout.activity_form_detail


    override fun initControl() {

        val database = AppDatabase.getInstance(this)
        val sampleDao = database.sampleDao()
        val cal = Calendar.getInstance()
        /* sampleDao.getData().forEach {
             sampleDao.getData().forEach {
                 Log.i("==>id: ${it.id}", "==>: ${it.name}")
             }*/
        studentData = intent.getParcelableExtra("data")

        if (null != studentData) {
            binding.etName.setText(studentData!!.name)
            binding.etAddress.setText(studentData!!.address)
            binding.tvDatePicker.setText(studentData!!.date.toString())

            cal!!.timeInMillis = studentData!!.date
            binding.tvDatePicker.setText(
                SimpleDateFormat("dd MMM, yyyy").format(Date(cal!!.timeInMillis))
            )


            studentData = intent.getParcelableExtra("data")
            if (null != studentData) {
                photo = studentData!!.image
                Glide.with(this)
                    .load(photo)
                    .circleCrop()
                    .into(binding.ivUserImage)
            }


            if (studentData!!.gender == "Male") binding.rbMale.isChecked =
                true else if (studentData!!.gender == "Female") binding.rbFemale.isChecked =
                true else null

            setTitle("Updated")

        }



        binding.tvDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal!!.set(Calendar.YEAR, year)
                    cal!!.set(Calendar.MONTH, monthOfYear)
                    cal!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.tvDatePicker.setText(
                        SimpleDateFormat("dd MMM, yyyy").format(Date(cal!!.timeInMillis))
                    )

                },
                cal!!.get(Calendar.YEAR),
                cal!!.get(Calendar.MONTH),
                cal!!.get(Calendar.DAY_OF_MONTH)


            )
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show()


        }



        //val sampleDao = AppDatabase.getInstance(this).sampleDao()


        binding.ivUserImage.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val dialogBinding = DailogImagePickerBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.tvCamera.setOnClickListener {
                dialog.dismiss()
                val permission = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 101)
            }

            dialogBinding.tvGallery.setOnClickListener {
                dialog.dismiss()
                val permission = arrayOf(READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 102)
            }


            dialog.show()
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
                    id = if (studentData == null) 0 else studentData!!.id,
                    name = name,
                    gender = gender,
                    date = cal.timeInMillis,
                    address = address,
                    image = imageFilePath!!
                )


                /*sampleDao.insertData(data)
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                binding.etName.text = null
                binding.rgGender.clearCheck()
                binding.tvDatePicker.text = null
                binding.etAddress.text = null
                finish()*/


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
                intent.putExtra("date", data)
                startActivity(intent)
                Log.d("result", "name: $name adr: $address gender: $gender  date: $date id :$id")

            }

        }
        setSupportActionBar(binding.iToolbar.toolbar)
        setTitle("Student Form")
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
        } else if (requestCode == 101) {
            val status1 = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val status2 = grantResults[1] == PackageManager.PERMISSION_GRANTED
            Log.d("==>status1", "==>$status1 status2:$status2")
            if (status1 && status2) {
                val cameraFile = File(
                    Environment.getExternalStorageDirectory(),
                    "camera_${System.currentTimeMillis()}.jpg"
                )
                val tempUri = Uri.fromFile(cameraFile)

                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
                startActivityForResult(captureIntent, 203)
            }
        }
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

