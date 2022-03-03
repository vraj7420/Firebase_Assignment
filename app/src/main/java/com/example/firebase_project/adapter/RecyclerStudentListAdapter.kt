package com.example.firebase_project.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.model.StudentResultModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_student_list.view.*

class RecyclerStudentListAdapter(
    private var ctx: Context,
    private var studentList: ArrayList<StudentResultModel>
) : RecyclerView.Adapter<RecyclerStudentListAdapter.StudentListViewHolder>() {
    private lateinit var sheetDialog: BottomSheetDialog
    private lateinit var tetStudentName: TextInputEditText
    private lateinit var tetMobileNumber: TextInputEditText
    private lateinit var tetTotalMarks: TextInputEditText
    private lateinit var spinnerGrade: Spinner
    private val ref = FirebaseDatabase.getInstance().getReference("Student")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        val recyclerInflater = LayoutInflater.from(ctx)
        val recyclerView = recyclerInflater.inflate(R.layout.item_student_list, parent, false)
        return StudentListViewHolder(recyclerView)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: StudentListViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvContactNumber.text =
            ctx.getString(R.string.contact_number) + " " + student.studentNumber
        holder.tvStudentName.text = ctx.getString(R.string.student_name) + " " + student.studentName
        holder.tvGrade.text = ctx.getString(R.string.student_grade) + " " + student.StudentGrade
        holder.tvTotalMark.text =
            ctx.getString(R.string.student_total_mark) + " " + student.studentTotalMark.toString()
        holder.btnUpdate.setOnClickListener {
            showAddStudentDataBottomSheet(student.id)
        }
        holder.btnDelete.setOnClickListener {
            deleteStudentData(student.id)
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


    private fun  showAddStudentDataBottomSheet(id: Long?) {
        sheetDialog = BottomSheetDialog(ctx)
        sheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sheetDialog.setContentView(R.layout.sheet_layout_student_registration)
        sheetDialog.show()
        if (Utility().checkForInternet(ctx)) {
            setDataForUpdate(id)
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show()
        }

        val btnSubmit: Button? = sheetDialog.findViewById(R.id.btnAddOrUpdate)
        btnSubmit?.setOnClickListener {
            if (Utility().checkForInternet(ctx)) {
                if (validateStudentData()) {
                    updateStudentData(id)
                }
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show()
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
    fun fetchData() {
        studentList.clear()
        ref.get().addOnSuccessListener {
            if (it.exists()) {
                for (student in it.children) {
                    val studentId: Long? = student.child("id").getValue(Long::class.java)
                    val studentName = student.child("studentName").getValue(String::class.java)
                    val studentNumber = student.child("studentNumber").getValue(String::class.java)
                    val studentTotalMark =
                        student.child("studentTotalMark").getValue(Int::class.java)
                    val studentGrade = student.child("studentGrade").getValue(String::class.java)
                    studentList.add(
                        StudentResultModel(
                            studentId, studentName.toString(),
                            studentNumber.toString(),
                            studentTotalMark.toString().toDouble(),
                            studentGrade.toString()
                        )
                    )
                }
                notifyDataSetChanged()
            } else {
                Toast.makeText(ctx,ctx.getString(R.string.no_data_found), Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun validateStudentData(): Boolean {
        when {
            (tetStudentName.text.toString().trim().isEmpty()) -> {
                tetStudentName.error = ctx.getString(R.string.error_full_name)
                tetStudentName.requestFocus()
                return false
            }
            (tetMobileNumber.text.toString().trim().isEmpty()) -> {
                tetMobileNumber.error = ctx.getString(R.string.error_phone_number_is_empty)
                tetMobileNumber.requestFocus()
                return false
            }
            (tetMobileNumber.text.toString().length != 10) -> {
                tetMobileNumber.error = ctx.getString(R.string.error_phone_number_valid)
                tetMobileNumber.requestFocus()
                return false
            }
            (tetTotalMarks.text.toString().trim().isEmpty()) -> {
                tetTotalMarks.error = ctx.getString(R.string.error_mark)
                tetTotalMarks.requestFocus()
                return false
            }
        }
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStudentData(id: Long?) {
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
            fetchData()
        }
    }

    private fun deleteStudentData(id: Long?) {
        val alertDialog = AlertDialog.Builder(ctx)
        alertDialog.setTitle("Delete Data")
        alertDialog.setMessage("Are you  Sure  Delete This Data ?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            if (Utility().checkForInternet(ctx)) {
                ref.child(id.toString()).removeValue().addOnSuccessListener {
                    fetchData()
                }
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT)
                    .show()
            }

        }
        alertDialog.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return studentList.size
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