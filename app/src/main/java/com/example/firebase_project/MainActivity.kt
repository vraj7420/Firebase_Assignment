package com.example.firebase_project

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var sheetDialog: Dialog
    private lateinit var tetStudentName: TextInputEditText
    private lateinit var tetMobileNumber: TextInputEditText
    private lateinit var tetTotalMarks: TextInputEditText
    private lateinit var spinnerGrade: Spinner
    private val ref = FirebaseDatabase.getInstance().getReference("Student")
    private val studentResultList = ArrayList<StudentResultModel>()
    private var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkForInternet(this)) {
            fetchData()
        } else {
            Toast.makeText(this, "Internet Of For Fetching  Data Turn On Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_info_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItemAdd) {
            openDialogSheet()
        }
        return true
    }

    private fun validateStudentData(): Boolean {
        when {
            (tetStudentName.text.toString().trim().isEmpty()) -> {
                tetStudentName.error = getString(R.string.error_full_name)
                tetStudentName.requestFocus()
                return false
            }
            (tetMobileNumber.text.toString().trim().isEmpty()) -> {
                tetMobileNumber.error = getString(R.string.error_phone_number_is_empty)
                tetMobileNumber.requestFocus()
                return false
            }
            (tetMobileNumber.text.toString().length != 10) -> {
                tetMobileNumber.error = getString(R.string.error_phone_number_valid)
                tetMobileNumber.requestFocus()
                return false
            }
            (tetTotalMarks.text.toString().trim().isEmpty()) -> {
                tetTotalMarks.error = getString(R.string.error_mark)
                tetTotalMarks.requestFocus()
                return false
            }
        }
        return true
    }

    private fun openDialogSheet() {
        sheetDialog = Dialog(this)
        sheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sheetDialog.setContentView(R.layout.sheet_layout_student_registration)
        sheetDialog.show()
        val btnSubmit: Button = sheetDialog.findViewById(R.id.btnAddOrUpdate)
        tetStudentName = sheetDialog.findViewById(R.id.tetStudentName)
        tetMobileNumber = sheetDialog.findViewById(R.id.tetMobileNumber)
        tetTotalMarks = sheetDialog.findViewById(R.id.tetTotalMark)
        spinnerGrade = sheetDialog.findViewById(R.id.spinnerGrade)

        val gradeSelectionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.Grade)
        )
        spinnerGrade.adapter = gradeSelectionAdapter

        btnSubmit.setOnClickListener {
            if (checkForInternet(this)) {
                if (validateStudentData()) {
                    storeDataInFirebase()
                }
            } else {
                Toast.makeText(this, "Internet Off For Store Data  Turn On Internet", Toast.LENGTH_SHORT).show()
            }

        }
        sheetDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        sheetDialog.window?.attributes!!.windowAnimations = R.style.sheetAnimation
        sheetDialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun storeDataInFirebase() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                id = snapshot.childrenCount.toInt() + 1
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        ref.child(id.toString()).setValue(
            StudentResultModel(
                id,
                tetStudentName.text.toString(),
                tetMobileNumber.text.toString().toLong(),
                tetTotalMarks.text.toString().toInt(),
                spinnerGrade.selectedItem.toString()
            )
        ).addOnSuccessListener {
            sheetDialog.dismiss()
            Toast.makeText(this, "Value Added Succefully", Toast.LENGTH_SHORT).show()
            fetchData()
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchData() {
        studentResultList.clear()
        ref.get().addOnSuccessListener {
            for (student in it.children) {
                val studentId = student.child("id").getValue(Int::class.java)
                val studentName = student.child("studentName").getValue(String::class.java)
                val studentNumber = student.child("studentNumber").getValue(Long::class.java)
                val studentTotalMark = student.child("studentTotalMark").getValue(Int::class.java)
                val studentGrade = student.child("studentGrade").getValue(String::class.java)
                studentResultList.add(
                    StudentResultModel(
                        studentId, studentName.toString(),
                        studentNumber.toString().toLong(),
                        studentTotalMark.toString().toInt(),
                        studentGrade.toString()
                    )
                )
            }
            val studentInfoAdapter = RecyclerStudentListAdapter(this@MainActivity, studentResultList)
            studentInfoAdapter.notifyDataSetChanged()
            rvStudentInfo.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStudentInfo.adapter = studentInfoAdapter
        }
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
