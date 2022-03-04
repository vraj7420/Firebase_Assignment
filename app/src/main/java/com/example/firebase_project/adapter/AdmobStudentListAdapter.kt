package com.example.firebase_project.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.model.StudentResultModel

class AdmobStudentListAdapter(private var studentList: ArrayList<StudentResultModel>) : RecyclerView.Adapter<AdmobStudentListAdapter.StudentListViewHolder>() {
    private lateinit var ctx: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        ctx=parent.context
        val recyclerInflater = LayoutInflater.from(parent.context)
        val recyclerView = recyclerInflater.inflate(R.layout.item_student_list_ad_mob_screen, parent, false)
        return StudentListViewHolder(recyclerView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudentListViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvContactNumber.text = ctx.getString(R.string.contact_number) + " " + student.studentNumber
        holder.tvStudentName.text = ctx.getString(R.string.student_name) + " " + student.studentName
        holder.tvGrade.text = ctx.getString(R.string.student_grade) + " " + student.StudentGrade
        holder.tvTotalMark.text = ctx.getString(R.string.student_total_mark) + " " + student.studentTotalMark.toString()
    }
    override fun getItemCount(): Int {
        return studentList.size
    }

    inner class StudentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        var tvGrade: TextView = itemView.findViewById(R.id.tvStudentGrade)
        var tvContactNumber: TextView = itemView.findViewById(R.id.tvContactNumber)
        var tvTotalMark: TextView = itemView.findViewById(R.id.tvStudentMark)
    }
}