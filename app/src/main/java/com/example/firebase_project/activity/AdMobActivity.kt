package com.example.firebase_project.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_project.R
import com.example.firebase_project.Utility
import com.example.firebase_project.adapter.AdmobStudentListAdapter
import com.example.firebase_project.model.StudentResultModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_ad_mob.*

class AdMobActivity : AppCompatActivity() {
    private val ref = FirebaseDatabase.getInstance().getReference("Student")
    private val studentResultList = ArrayList<StudentResultModel>()
    private var interstitialAdd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_mob)
        init()
    }

    private fun init() {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.admob)
        MobileAds.initialize(this) {}
        setAds()
        if (Utility().checkForInternet(this)) {
            fetchData()
        } else {
            Snackbar.make(rootViewAdMob,getString(R.string.turn_on_internet),Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setAds() {
        val bannerAdsCheck = intent.getBooleanExtra("bannerAds", false)
        val  interstitialAdsCheck= intent.getBooleanExtra("interstitialAds", false)
        if (bannerAdsCheck) {
            adView.visibility = View.VISIBLE
             val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
      if(interstitialAdsCheck){
          loadAdd()
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
                Snackbar.make(rootViewAdMob,getString(R.string.no_data_found),Snackbar.LENGTH_LONG).show()
            }
            val studentInfoAdapter = AdmobStudentListAdapter( studentResultList)
            rvStudentInfo.layoutManager = LinearLayoutManager(this)
            rvStudentInfo.adapter = studentInfoAdapter
        }
    }
    private fun loadAdd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError.message)
                    interstitialAdd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAdd = interstitialAd
                    showInterstitialAds()
                    interstitialAdd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAdd = null
                            }
                            override fun onAdShowedFullScreenContent() {
                                interstitialAdd = null
                            }
                        }
                }
            })
    }
    private fun showInterstitialAds() {
        interstitialAdd.let {
            interstitialAdd?.show(this)
        }
    }
}
