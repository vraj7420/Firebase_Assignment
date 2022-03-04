package com.example.firebase_project.activity

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.adapter.RecyclerStudentListAdapter
import com.example.firebase_project.model.StudentResultModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_student_info.*


class StudentInfoActivity : AppCompatActivity() {

    private lateinit var sheetDialog: BottomSheetDialog
    private lateinit var tetStudentName: TextInputEditText
    private lateinit var tetMobileNumber: TextInputEditText
    private lateinit var tetTotalMarks: TextInputEditText
    private lateinit var spinnerGrade: Spinner
    private lateinit var studentInfoAdapter: RecyclerStudentListAdapter
    private val ref = FirebaseDatabase.getInstance().getReference("Student")

    companion object {
        val studentResultList = ArrayList<StudentResultModel>()
    }

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        setAdapter()
        init()
    }

    private fun setAdapter() {
        studentInfoAdapter = RecyclerStudentListAdapter()
        rvStudentInfo.layoutManager = LinearLayoutManager(this@StudentInfoActivity)
        rvStudentInfo.adapter = studentInfoAdapter
    }

    private fun init() {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.student_info)
        if (Utility().checkForInternet(this)) {
            Utility().fetchData(this,studentInfoAdapter)
        } else {
            Snackbar.make(rootViewStudentInfoActivity,getString(R.string.turn_on_internet),Snackbar.LENGTH_SHORT).show()
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
                if (Utility().validateStudentData(tetStudentName,tetMobileNumber,tetTotalMarks,this)) {
                    storeDataInFirebase()
                }
            } else {
                Snackbar.make(rootViewStudentInfoActivity,getString(R.string.turn_on_internet),Snackbar.LENGTH_SHORT).show()
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
            Snackbar.make(rootViewStudentInfoActivity,getString(R.string.student_data_stored),Snackbar.LENGTH_SHORT).show()
            Utility().fetchData(this,studentInfoAdapter)
        }
    }
}

