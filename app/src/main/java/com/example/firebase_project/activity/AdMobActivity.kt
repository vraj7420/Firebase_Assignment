package com.example.firebase_project.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.adapter.AdmobStudentListAdapter
import com.example.firebase_project.model.StudentResultModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_ad_mob.*

class AdMobActivity : AppCompatActivity() {
    private val ref = FirebaseDatabase.getInstance().getReference("Student")
    private val studentResultList = ArrayList<StudentResultModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_mob)
        MobileAds.initialize(this) {}
        setAds()
        if (Utility().checkForInternet(this)) {
            fetchData()
        } else {
            Toast.makeText(this, getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAds() {
        val bannerAdsCheck = intent.getBooleanExtra("bannerAds", false)
        if (bannerAdsCheck) {
            adView.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    private fun fetchData() {
        studentResultList.clear()
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
            val studentInfoAdapter = AdmobStudentListAdapter(this, studentResultList)
            rvStudentInfo.layoutManager = LinearLayoutManager(this)
            rvStudentInfo.adapter = studentInfoAdapter
        }
    }
}
