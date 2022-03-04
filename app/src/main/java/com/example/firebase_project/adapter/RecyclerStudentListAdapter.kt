package com.example.firebase_project.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.activity.StudentInfoActivity
import com.example.firebase_project.model.StudentResultModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_student_list.view.*

class RecyclerStudentListAdapter : RecyclerView.Adapter<RecyclerStudentListAdapter.StudentListViewHolder>() {
    private lateinit var sheetDialog: BottomSheetDialog
    private lateinit var tetStudentName: TextInputEditText
    private lateinit var tetMobileNumber: TextInputEditText
    private lateinit var tetTotalMarks: TextInputEditText
    private lateinit var spinnerGrade: Spinner
    private lateinit var ctx: Context
    private val ref = FirebaseDatabase.getInstance().getReference("Student")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        ctx=parent.context
        val recyclerInflater = LayoutInflater.from(parent.context)
        val recyclerView = recyclerInflater.inflate(R.layout.item_student_list, parent, false)
        return StudentListViewHolder(recyclerView)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: StudentListViewHolder, position: Int) {
        val student =StudentInfoActivity.studentResultList[position]
        holder.tvContactNumber.text = ctx.getString(R.string.contact_number) + " " + student.studentNumber
        holder.tvStudentName.text = ctx.getString(R.string.student_name) + " " + student.studentName
        holder.tvGrade.text = ctx.getString(R.string.student_grade) + " " + student.StudentGrade
        holder.tvTotalMark.text =
            ctx.getString(R.string.student_total_mark) + " " + student.studentTotalMark.toString()
        holder.btnUpdate.setOnClickListener {
            showUpdateStudentDataBottomSheet(student.id)
        }
        holder.btnDelete.setOnClickListener {
           Utility().deleteStudentData(ctx,student.id,this)
        }
    }


    private fun setDataForUpdate(id: Long?) {
        ref.child(id.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val studentName = snapshot.child("studentName").getValue(String::class.java)
                    val studentNumber = snapshot.child("studentNumber").getValue(String::class.java)
                    val studentTotalMark =
                        snapshot.child("studentTotalMark").getValue(Int::class.java)
                    tetStudentName.setText(studentName)
                    tetMobileNumber.setText(studentNumber)
                    tetTotalMarks.setText(studentTotalMark.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun  showUpdateStudentDataBottomSheet(id: Long?) {
        sheetDialog = BottomSheetDialog(ctx)
        sheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sheetDialog.setContentView(R.layout.sheet_layout_student_registration)
        sheetDialog.show()
        if (Utility().checkForInternet(ctx)) {
            setDataForUpdate(id)
        } else {
            val view=(ctx as Activity).window.decorView.findViewById<View>(android.R.id.content)
            Snackbar.make(view,ctx.getString(R.string.turn_on_internet), Snackbar.LENGTH_SHORT).show()
        }
        val btnSubmit: Button? = sheetDialog.findViewById(R.id.btnAddOrUpdate)
        btnSubmit?.setOnClickListener {
            if (Utility().checkForInternet(ctx)) {
                if (Utility().validateStudentData(tetStudentName,tetMobileNumber,tetTotalMarks,ctx)) {
                    updateStudentData(id)
                }
            } else {
                val view=(ctx as Activity).window.decorView.findViewById<View>(android.R.id.content)
                Snackbar.make(view,ctx.getString(R.string.turn_on_internet), Snackbar.LENGTH_SHORT).show()
            }
        }
        tetStudentName = sheetDialog.findViewById(R.id.tetStudentName)!!
        tetMobileNumber = sheetDialog.findViewById(R.id.tetMobileNumber)!!
        tetTotalMarks = sheetDialog.findViewById(R.id.tetTotalMark)!!
        spinnerGrade = sheetDialog.findViewById(R.id.spinnerGrade)!!
        val gradeSelectionAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, ctx.resources.getStringArray(R.array.Grade))
        spinnerGrade.adapter = gradeSelectionAdapter
        sheetDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        sheetDialog.window?.attributes!!.windowAnimations = R.style.sheetAnimation
        sheetDialog.window!!.setGravity(Gravity.BOTTOM)
    }




    @SuppressLint("NotifyDataSetChanged")
    private fun updateStudentData(id: Long?) {
        ref.child(id.toString()).setValue(
            StudentResultModel(
                id,
                tetStudentName.text.toString(),
                tetMobileNumber.text.toString(),
                tetTotalMarks.text.toString().toDouble(),
                spinnerGrade.selectedItem.toString())
        ).addOnSuccessListener {
            sheetDialog.dismiss()
            Utility().fetchData(ctx,this)
        }
    }

    override fun getItemCount(): Int {
        return StudentInfoActivity.studentResultList.size
    }

    inner class StudentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        var tvGrade: TextView = itemView.findViewById(R.id.tvStudentGrade)
        var tvContactNumber: TextView = itemView.findViewById(R.id.tvContactNumber)
        var tvTotalMark: TextView = itemView.findViewById(R.id.tvStudentMark)
        var btnUpdate: ImageView = itemView.btnUpdate
        var btnDelete: ImageView = itemView.btnDelete
    }
}