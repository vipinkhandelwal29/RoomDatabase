package com.example.roomdatabase

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import com.example.roomdatabase.database.bean.StudentTable
import com.example.roomdatabase.databinding.ActivityFormDetailBinding
import com.example.roomdatabase.databinding.DailogImagePickerBinding
import com.example.roomdatabase.databinding.DailogToastMsgBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormDetailActivity() : BaseActivity<ActivityFormDetailBinding>(), View.OnClickListener {

    private var imageFilePath: String? = null
    private var dataList = ArrayList<SampleTable>()
    private var id = 0
    private var photo: String? = null
    private var studentData: SampleTable? = null

    override fun getLayoutId() = R.layout.activity_form_detail


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun initControl() {

        initData()

        val database = AppDatabase.getInstance(this)
        //val sampleDao = database.sampleDao()
        val cal = Calendar.getInstance()

        val dialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val dialogValidation = DailogToastMsgBinding.inflate(layoutInflater)
        dialog.setContentView(dialogValidation.root)


        /* sampleDao.getData().forEach {
             sampleDao.getData().forEach {
                 Log.i("==>id: ${it.id}", "==>: ${it.name}")
             }*/


        studentData = intent.getParcelableExtra("data")

        if (null != studentData) {
            binding.etName.setText(studentData!!.name)
            binding.etAddress.setText(studentData!!.address)
            binding.tvDatePicker.setText(studentData!!.date.toString())

            cal.timeInMillis = studentData!!.date
            binding.tvDatePicker.setText(
                SimpleDateFormat("dd MMM, yyyy").format(Date(cal.timeInMillis))
            )
            photo = studentData!!.image
            Glide.with(this)
                .load(photo)
                .circleCrop()
                .into(binding.ivUserImage)

            if (studentData!!.gender == "Male") binding.rbMale.isChecked =
                true else if (studentData!!.gender == "Female")
                binding.rbFemale.isChecked =
                    true else null

            setSupportActionBar(binding.iToolbar.toolbar)
            setTitle("Student Update")

        }



        binding.tvDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val userAge = GregorianCalendar(year, monthOfYear, dayOfMonth)
                    val minAdultAge = GregorianCalendar()
                    minAdultAge.add(Calendar.YEAR, -18)
                    minAdultAge.add(Calendar.MONTH, -1)
                    if (minAdultAge.before(userAge)) {
                        dialogValidation.tvMsg.text = "Your Age 18+ Required"
                        dialogValidation.btnSubmit.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                        // Toast.makeText(this, "Your Age 18+ Requerd", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvDatePicker.setText(
                            SimpleDateFormat("dd MMM, yyyy").format(Date(cal.timeInMillis))
                        )
                    }

                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)


            )
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show()


        }


        //val sampleDao = AppDatabase.getInstance(this).sampleDao()


        binding.ivUserImage.setOnClickListener {
            val dialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
            val dialogBinding = DailogImagePickerBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.tvGallery.setOnClickListener {
                dialog.dismiss()
                val permission = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 101)
            }

            dialogBinding.tvCamera.setOnClickListener {
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


            if (imageFilePath == null) {
                dialogValidation.tvMsg.text = "Please enter image"
                dialogValidation.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()


            } else if (name.isBlank()) {
                dialogValidation.tvMsg.text = "Please enter name"
                dialogValidation.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

            } else if (gender.isNullOrBlank()) {
                dialogValidation.tvMsg.text = "Please enter gender"
                dialogValidation.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()


            } else if (date.isBlank()) {
                dialogValidation.tvMsg.text = "Please enter date"
                dialogValidation.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

            } else if (address.isBlank()) {
                dialogValidation.tvMsg.text = "Please enter address"
                dialogValidation.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

            } else {

                val dataF = StudentTable(
                    id = (if (id == 0 ) (System.currentTimeMillis()) else id.toLong())/*if (studentData == null) 0 else studentData!!.id*/,
                    name = name,
                    gender = gender,
                    date = cal.timeInMillis,
                    address = address,
                    image = imageFilePath!!


              /*  val data = SampleTable(
                    id = (if (id == 0 ) (System.currentTimeMillis().toInt()) else id.toLong().toInt())if (studentData == null) 0 else studentData!!.id,
                    name = name,
                    gender = gender,
                    date = cal.timeInMillis,
                    address = address,
                    image = imageFilePath!!*/
                )


                /*sampleDao.insertData(data)
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                binding.etName.text = null
                binding.rgGender.clearCheck()
                binding.tvDatePicker.text = null
                binding.etAddress.text = null
                finish()*/

                myRef.push().setValue(dataF)



                //dbReference.child("users").child(userId).setValue(user)
                /*myRef.child("name").setValue(name)
                myRef.child("address").setValue(address)
                //addUserChangeListener()
                myRef.push().setValue(if (id==0) {
                    (System.currentTimeMillis().toString())
                }else {}*/



                setResult(Activity.RESULT_OK, intent)
                finish()
                binding.etName.text = null
                binding.etAddress.text = null
                binding.rbMale.text = null
                binding.rbFemale.text = null
                binding.tvDatePicker.text = null

              /*  val id = sampleDao.insertData(data)
                Toast.makeText(this, "$id: $name: $gender $date  $address", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("date", data)
                startActivity(intent)
                Log.d("result", "name: $name adr: $address gender: $gender  date: $date id :$id")*/

            }


        }

        setSupportActionBar(binding.iToolbar.toolbar)
        setTitle("Add New Student")
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

    /*private fun addUserChangeListener() {
        // User data change listener
        myRef.child("students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(SampleTable::class.java)

                // Check for null
                if (user == null) {
                    return
                }




                // Display newly updated name and email
                binding.etName.setText(user?.name).toString()
                binding.etAddress.setText(user?.name).toString()
                //binding.gr.setText(user?.name).toString()
                // clear edit text erNameEt.setText("")
                //userMobileEt.setText("")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }*/
}




