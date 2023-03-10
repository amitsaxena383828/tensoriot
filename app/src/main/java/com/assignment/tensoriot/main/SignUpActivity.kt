package com.assignment.tensoriot.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.assignment.tensoriot.R
import com.assignment.tensoriot.databinding.ActivitySignUpBinding
import com.assignment.tensoriot.model.user.User
import com.assignment.tensoriot.utility.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : AppCompatActivity() {
    lateinit var bindingSignUpActivity: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1

    var mCurrentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignUpActivity = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bindingSignUpActivity.root)
        auth = Firebase.auth
        database = Firebase.database.reference
        bindingSignUpActivity.btnSignUp.setOnClickListener {
            if (validate()) {
                if (bindingSignUpActivity.etPassword.text.toString()
                        .equals(bindingSignUpActivity.etConfirmPassword.text.toString())
                ) {
                    bindingSignUpActivity.pb.visibility = View.VISIBLE
                    signUpUser(
                        bindingSignUpActivity.etEmail.text.toString(),
                        bindingSignUpActivity.etPassword.text.toString()
                    )
                } else {
                    Toast.makeText(this, "Password is not match", Toast.LENGTH_LONG).show()
                }
            }
        }

        bindingSignUpActivity.ivProfilePic.setOnClickListener {
            clickprofilePic()
        }
    }

    private fun clickprofilePic() {
        captureImage()
    }


    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this,
                            "com.assignment.tensoriot.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            bindingSignUpActivity.ivProfilePic.setImageBitmap(myBitmap)
            if (photoFile!= null) {
                val fileName = photoFile!!.name
                uploadFile(fileName)
            }

        } else {
            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }


    fun uploadFile(fileName: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$fileName")
        storageRef.putFile(Uri.parse(fileName))
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        Log.d("Amit", "Url$imageUrl")
                    }
                })

            .addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }


    fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Amit", "createUserWithEmail:success")
                    val user = auth.currentUser
                    startProfileActivity(user)
                    createUser(user)
                } else {
                    Log.w("Amit", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed." + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                bindingSignUpActivity.pb.visibility = View.GONE
            }


    }

    private fun createUser(userFb: FirebaseUser?) {
        val user = User(
            userFb?.email,
            bindingSignUpActivity.etUsername.text.toString(),
            bindingSignUpActivity.etBio.text.toString(),
            userFb?.uid
        )
        database.child("users").child(userFb?.uid.toString())
            .setValue(user);
    }


    private fun startProfileActivity(user: FirebaseUser?) {
        val userString = Tensoriot.getGsonInstance().toJson(user)
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_PARAM_USER_PROFILE, userString)
        startActivity(intent)
    }


    private fun validate(): Boolean {
        var check = true
        if (!hasText(bindingSignUpActivity.etEmail, resources.getString(R.string.required))) check =
            false
        if (!hasText(
                bindingSignUpActivity.etUsername,
                resources.getString(R.string.required)
            )
        ) check = false
        if (!hasText(bindingSignUpActivity.etBio, resources.getString(R.string.required))) check =
            false
        return check
    }

    fun hasText(editText: EditText, error_message: String?): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null
        if (text.length == 0) {
            editText.error = error_message
            return false
        }
        return true
    }


    fun checkPermission() {

    }
}