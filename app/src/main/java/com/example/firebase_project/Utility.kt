package com.example.firebase_project

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import com.example.firebase_project.activity.StudentInfoActivity
import com.example.firebase_project.adapter.RecyclerStudentListAdapter
import com.example.firebase_project.model.StudentResultModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase


class Utility {
    private val ref = FirebaseDatabase.getInstance().getReference("Student")

    fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
    fun fetchData(ctx: Context,adapter: RecyclerStudentListAdapter) {
        val view=(ctx as Activity).window.decorView.findViewById<View>(android.R.id.content)
        StudentInfoActivity.studentResultList.clear()
        ref.get().addOnSuccessListener {
            if (it.exists()) {
                for (student in it.children) {
                    val studentId: Long = student.child("id").getValue(Long::class.java) ?: 0
                    val studentName = student.child("studentName").getValue(String::class.java)
                    val studentNumber: String =
                        student.child("studentNumber").getValue(String::class.java) ?: ""
                    val studentTotalMark: Double =
                        student.child("studentTotalMark").getValue(Double::class.java) ?: 0.0
                    val studentGrade = student.child("studentGrade").getValue(String::class.java)
                    StudentInfoActivity.studentResultList.add(
                        StudentResultModel(
                            studentId, studentName.toString(),
                            studentNumber,
                            studentTotalMark.toString().toDouble(),
                            studentGrade.toString()
                        )
                    )
                }
            } else {
                Snackbar.make(view,ctx.getString(R.string.no_data_found),Snackbar.LENGTH_SHORT).show()
            }
            adapter.notifyDataSetChanged()
        }
    }
     fun deleteStudentData(ctx: Context,id: Long?,adapter: RecyclerStudentListAdapter) {
         val view=(ctx as Activity).window.decorView.findViewById<View>(android.R.id.content)
         val alertDialog = AlertDialog.Builder(ctx)
        alertDialog.setTitle("Delete Data")
        alertDialog.setMessage("Are you  Sure  Delete This Data ?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            if (Utility().checkForInternet(ctx)) {
                ref.child(id.toString()).removeValue().addOnSuccessListener {
                    Utility().fetchData(ctx,adapter)
                }
            } else {
                Snackbar.make(view,ctx.getString(R.string.turn_on_internet),Snackbar.LENGTH_SHORT).show()
            }

        }
        alertDialog.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        alertDialog.show()
    }
    fun validateStudentData(tetStudentName:TextInputEditText,tetMobileNumber:TextInputEditText,tetTotalMarks:TextInputEditText,ctx: Context): Boolean {
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
}