package com.example.firebase_project.activity

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.adapter.RecyclerStudentListAdapter
import com.example.firebase_project.model.StudentResultModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_student_info.*


class StudentInfoActivity : AppCompatActivity() {

    private lateinit var sheetDialog: BottomSheetDialog
    private lateinit var tetStudentName: TextInputEditText
    private lateinit var tetMobileNumber: TextInputEditText
    private lateinit var tetTotalMarks: TextInputEditText
    private lateinit var spinnerGrade: Spinner
    private val ref = FirebaseDatabase.getInstance().getReference("Student")
    private val studentResultList = ArrayList<StudentResultModel>()
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        if (Utility().checkForInternet(this)) {
            fetchData()
        } else {
            Toast.makeText(this, getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show()
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
        sheetDialog = BottomSheetDialog(this)
        sheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sheetDialog.setContentView(R.layout.sheet_layout_student_registration)
        sheetDialog.show()
        val btnSubmit: Button? = sheetDialog.findViewById(R.id.btnAddOrUpdate)
        tetStudentName = sheetDialog.findViewById(R.id.tetStudentName)!!
        tetMobileNumber = sheetDialog.findViewById(R.id.tetMobileNumber)!!
        tetTotalMarks = sheetDialog.findViewById(R.id.tetTotalMark)!!
        spinnerGrade = sheetDialog.findViewById(R.id.spinnerGrade)!!
        val gradeSelectionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.Grade)
        )
        spinnerGrade.adapter = gradeSelectionAdapter

        btnSubmit?.setOnClickListener {
            if (Utility().checkForInternet(this)) {
                if (validateStudentData()) {
                    storeDataInFirebase()
                }
            } else {
                Toast.makeText(this, getString(R.string.turn_on_internet), Toast.LENGTH_SHORT)
                    .show()
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
                id = snapshot.childrenCount + 1
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        ref.child(id.toString()).setValue(
            StudentResultModel(
                id,
                tetStudentName.text.toString(),
                tetMobileNumber.text.toString(),
                tetTotalMarks.text.toString().toDouble(),
                spinnerGrade.selectedItem.toString()
            )
        ).addOnSuccessListener {
            sheetDialog.dismiss()
            Toast.makeText(this, getString(R.string.student_data_stored), Toast.LENGTH_SHORT).show()
            fetchData()
        }
    }

    private fun fetchData() {
        studentResultList.clear()
        ref.get().addOnSuccessListener {
            if (it.exists()) {
                for (student in it.children) {
                    val studentId: Long = student.child("id").getValue(Long::class.java) ?: 0
                    val studentName = student.child("studentName").getValue(String::class.java)
                    val studentNumber: String = student.child("studentNumber").getValue(String::class.java) ?: ""
                    val studentTotalMark: Double = student.child("studentTotalMark").getValue(Double::class.java) ?: 0.0
                    val studentGrade = student.child("studentGrade").getValue(String::class.java)
                    studentResultList.add(
                        StudentResultModel(
                            studentId, studentName.toString(),
                            studentNumber,
                            studentTotalMark.toString().toDouble(),
                            studentGrade.toString()
                        )
                    )
                }
            } else {
                Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show()
            }
            val studentInfoAdapter =
                RecyclerStudentListAdapter(this@StudentInfoActivity, studentResultList)
            rvStudentInfo.layoutManager = LinearLayoutManager(this@StudentInfoActivity)
            rvStudentInfo.adapter = studentInfoAdapter
        }
    }
}
