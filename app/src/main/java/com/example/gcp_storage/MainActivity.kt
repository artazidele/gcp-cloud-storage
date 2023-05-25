package com.example.gcp_storage

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class MainActivity : AppCompatActivity() {

    val storage = Firebase.storage
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text_view)

        // Poga "Augšupielādēt failu"
        val uploadBtn = findViewById<Button>(R.id.upload_file_btn)
        uploadBtn.setOnClickListener {
            // Pārbauda, vai lietotnei ir atļauts piekļūt ierīces failiem
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, 1001)
                } else {
                    uploadObject()
                }
            } else {
                uploadObject()
            }
        }

        // Poga "Lejupielādēt failu"
        val downloadBtn = findViewById<Button>(R.id.download_file_btn)
        downloadBtn.setOnClickListener {
            downloadObject()
        }

        // Poga "Iegūt failu sarakstu"
        val listBtn = findViewById<Button>(R.id.list_files_btn)
        listBtn.setOnClickListener {
            getObjectList()
        }

        // Poga "Izdzēst failu"
        val deleteBtn = findViewById<Button>(R.id.delete_file_btn)
        deleteBtn.setOnClickListener {
            deleteObject()
        }

    }

    // Funkcija objekta lejupielādei
    private fun downloadObject() {
        val storageRef = storage.reference
        val fileRef = storageRef.child("files/file3.txt")
        val localFile = File.createTempFile("file4", ".txt")
        fileRef.getFile(localFile)
            .addOnSuccessListener {
                textView.text = "Object is downloaded."
            }.addOnFailureListener {
                textView.text = "Object is not downloaded."
            }
    }

    // Funkcija objektu saraksta ieguvei
    private fun getObjectList() {
        val listRef = storage.reference.child("/files/")
        listRef.listAll()
            .addOnSuccessListener { items ->
                var itemsString = "Objects: "
                for (item in items.items) {
                    if (item == items.items.last()) {
                        itemsString += item.name
                    } else {
                        itemsString += item.name + ", "
                    }
                }
                textView.text = itemsString
            }
            .addOnFailureListener {
                textView.text = "Object list is not got."
            }
    }

    // Funkcija objekta dzēšanai
    private fun deleteObject() {
        val storageRef = storage.reference
        val fileRef = storageRef.child("files/file3.txt")
        fileRef.delete().addOnSuccessListener {
            textView.text = "Object is deleted."
        }.addOnFailureListener {
            textView.text = "Object is not deleted."
        }
    }

    // Funkcija objekta augšupielādei
    private fun uploadObject() {
        val storageRef = storage.reference
        var file = Uri.fromFile(File("/sdcard/Download/file3.txt"))
        val fileRef = storageRef.child("files/${file.lastPathSegment}")
        val uploadTask = fileRef.putFile(file)
        uploadTask.addOnFailureListener {
            textView.text = "Object is not uploaded."
        }.addOnSuccessListener { taskSnapshot ->
            textView.text = "Object is uploaded."
        }
    }
}