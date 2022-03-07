package com.example.firebase_project.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.firebase_project.R
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_ad_mob.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_box_select_adds.*

class MainActivity : AppCompatActivity() {
    private val ref = FirebaseDatabase.getInstance().getReference("AdIds")
    private var bannerAdCheck = false
    private lateinit var addDialog: Dialog
    private var interstitialAdsAdCheck = false
    private var chbInterstitialAds: MaterialCheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
    }

    private fun setListener() {
        btnStudentInfo.setOnClickListener {
            val intentStudentInfoActivity = Intent(this, StudentInfoActivity::class.java)
            startActivity(intentStudentInfoActivity)
        }
        btnAdMob.setOnClickListener {
            shoDialogBox()
        }
        btnNotification.setOnClickListener {
            val intentNotificationActivity = Intent(this, NotificationActivity::class.java)
            startActivity(intentNotificationActivity)
        }
    }

    private fun shoDialogBox() {
        addDialog = Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
        addDialog.setContentView(R.layout.dialog_box_select_adds)
        val window = addDialog.window
        window?.setLayout(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        addDialog.setTitle("Select Ads")
        val chbBannerAds = addDialog.chbBannerAds
        chbInterstitialAds = addDialog.chbInterstitialAds
        val btnOk = addDialog.btnOk
        val btnCancel = addDialog.btnCancel
        chbBannerAds.setOnClickListener {
            bannerAdCheck = chbBannerAds.isChecked
        }
        chbInterstitialAds?.setOnClickListener {
            interstitialAdsAdCheck = chbInterstitialAds!!.isChecked
            ref.get().addOnSuccessListener {
                if (it.exists()) {
                        val adUnitId=it.child("adUnitId").getValue(String::class.java)
                        val sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE)
                        val adUnitIdEditor = sharedPreferences.edit()
                        adUnitIdEditor.putString("AdUnitId",adUnitId.toString())
                        adUnitIdEditor.apply()

                } else {
                    Snackbar.make(rootViewAdMob,getString(R.string.no_data_found), Snackbar.LENGTH_LONG).show()
                }

            }
        }
        btnCancel.setOnClickListener {
            addDialog.dismiss()
        }
        btnOk.setOnClickListener {
                goToNextScreen() }
        addDialog.show()
    }


    override fun onResume() {
        super.onResume()
        interstitialAdsAdCheck = false
        bannerAdCheck = false
    }

    private fun goToNextScreen() {
        addDialog.dismiss()
        val intentAdMob = Intent(this, AdMobActivity::class.java)
        intentAdMob.putExtra("bannerAds", bannerAdCheck)
        intentAdMob.putExtra("interstitialAds", interstitialAdsAdCheck)
        startActivity(intentAdMob)
    }


}