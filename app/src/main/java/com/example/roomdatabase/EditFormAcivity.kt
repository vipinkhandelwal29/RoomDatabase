package com.example.roomdatabase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.roomdatabase.database.adapter.StudentListAdapter
import com.example.roomdatabase.database.bean.StudentTable
import com.example.roomdatabase.database.retrofit.ApiClient
import com.example.roomdatabase.database.retrofit.ApiInterface
import com.example.roomdatabase.databinding.ActivityFormDetailBinding
import com.example.roomdatabase.databinding.DailogImagePickerBinding
import com.example.roomdatabase.databinding.DailogProgressBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditFormAcivity : BaseActivity<ActivityFormDetailBinding>() {
    override fun getLayoutId() = R.layout.activity_form_detail

    private lateinit var studentData: StudentTable
    private var photo: String? = null
    private var tempFile: File? = null
    private lateinit var token: String
    private var adapter: StudentListAdapter? = null

    @SuppressLint("SimpleDateFormat")
    override fun initControl() {


        setSupportActionBar(binding.iToolbar.toolbar)
        title = "Student Update"

        val pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        token = pref.getString("token", null).toString()


        val cal = Calendar.getInstance()

        studentData = intent.getParcelableExtra("data")!!

        binding.etName.setText(studentData.name)
        binding.etAddress.setText(studentData.address)
        binding.tvDatePicker.text = studentData.dob.toString()

        cal.timeInMillis = studentData.dob
        binding.tvDatePicker.text = SimpleDateFormat("dd MMM, yyyy").format(Date(cal.timeInMillis))
        photo = studentData.image
        Glide.with(this)
            .load(photo)
            .circleCrop()
            .into(binding.ivUserImage)

        if (studentData.gender == "Male") binding.rbMale.isChecked =
            true else if (studentData.gender == "Female")
            binding.rbFemale.isChecked =
                true

        Glide.with(this)

        binding.tvDatePicker.setOnClickListener {
            //closeKeyBoard()
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
                        messageShow("Your age 18+ required")
                    } else {
                        binding.tvDatePicker.text =
                            SimpleDateFormat("dd MMM, yyyy").format(Date(cal.timeInMillis))
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding.ivUserImage.setOnClickListener {
            val dialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
            val dialogBinding = DailogImagePickerBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.tvGallery.setOnClickListener {
                dialog.dismiss()
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permission, 101)
            }

            dialogBinding.tvCamera.setOnClickListener {
                dialog.dismiss()
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 102)
            }


            dialog.show()
        }

        /* fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
             inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
         }*/

        binding.btnSubmit.setOnClickListener {
            val progressDialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
            val progressBinding = DailogProgressBinding.inflate(layoutInflater)
            progressDialog.setContentView(progressBinding.root)
            progressDialog.show()
            progressBinding.btnOk.setOnClickListener {
                progressDialog.dismiss()
            }
            //closeKeyBoard()

            val name = binding.etName.text.toString()
            val gender =
                if (binding.rbMale.isChecked) "Male"
                else if (binding.rbFemale.isChecked) "Female" else null
            val date = binding.tvDatePicker.text.toString()
            val address = binding.etAddress.text.toString()


            if (name.isBlank()) {
                messageShow("please enter your name")
            } else if (address.isBlank()) {
                messageShow("please enter your address")
            } else if (gender.isNullOrBlank()) {
                messageShow("please enter your gender")
            } else if (date.isBlank()) {
                messageShow("please enter your date")
            } else {
                if (tempFile != null) {
                    initFirebaseStorage()
                    uploadImage {
                        val dataF = StudentTable(
                            id = studentData.id,
                            name = name,
                            gender = gender,
                            dob = cal.timeInMillis,
                            address = address,
                            image = it,
                            token = token
                        )
                        initFirebaseDatabase()
                        editRetrofitData(dataF,dataF.id)

                        /*
                        databaseReference.child(dataF.id.toString()).setValue(dataF)
                            .addOnCompleteListener {
                                progressDialog.dismiss()
                                finish()
                            }.addOnFailureListener {
                                progressBinding.progressBar.visibility = View.GONE
                                progressBinding.btnOk.visibility = View.VISIBLE
                                progressBinding.tvError.text = it.localizedMessage
                                messageShow(it.localizedMessage)
                            }*/
                    }

                } else {
                    initFirebaseDatabase()
                    val dataF = StudentTable(
                        id = studentData.id,
                        name = name,
                        gender = gender,
                        dob = cal.timeInMillis,
                        address = address,
                        image = studentData.image,
                        token = token

                    )
                    editRetrofitData(dataF,dataF.id)
                   /* databaseReference.child(dataF.id.toString()).setValue(dataF)
                        .addOnCompleteListener {
                            progressDialog.dismiss()
                            finish()
                        }.addOnFailureListener {
                            progressBinding.progressBar.visibility = View.GONE
                            progressBinding.btnOk.visibility = View.VISIBLE
                            progressBinding.tvError.text = it.localizedMessage
                            messageShow(it.localizedMessage)
                        }*/
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }


    private fun editRetrofitData(data: StudentTable, id: Long) {
        val progressDialog = BottomSheetDialog(this, R.style.NoWiredStrapInNavigationBar)
        val progressBinding = DailogProgressBinding.inflate(layoutInflater)
        progressDialog.setContentView(progressBinding.root)
        progressDialog.show()
        progressBinding.btnOk.setOnClickListener {
            progressDialog.dismiss()
        }
        val call = ApiClient.getApiClient().create(ApiInterface::class.java)
            .putData(data = data, id = id.toString())
        call.enqueue(object : Callback<StudentTable> {
            override fun onFailure(call: Call<StudentTable>, t: Throwable) {
                progressBinding.progressBar.visibility = View.GONE
                progressBinding.btnOk.visibility = View.VISIBLE
                progressBinding.tvError.text = t.localizedMessage
            }
            override fun onResponse(call: Call<StudentTable>, response: Response<StudentTable>) {
                progressDialog.dismiss()
                val intent = Intent()
                intent.putExtra("data",response.body().toString())
                setResult(555, intent)
                adapter?.notifyItemChanged(id.toInt())
            }

        })
    }


    private fun uploadImage(callImage: (Image: String) -> Unit) {
        val ref = storageReference.child("student/${tempFile!!.name}")
        val uploadTask = ref.putStream(FileInputStream(tempFile))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d("exception", "${it}")
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callImage(downloadUri.toString())
                Log.d("exception", "${task}")

            }
        }
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
            this.tempFile = tempFile
            Glide.with(this).load(tempFile).circleCrop().into(binding.ivUserImage)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        } else {

            return super.onOptionsItemSelected(item)
        }
    }
}