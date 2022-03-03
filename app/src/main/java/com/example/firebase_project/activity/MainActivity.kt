package com.example.firebase_project.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_project.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_box_select_adds.*

class MainActivity : AppCompatActivity() {
    private var interstitialAdd: InterstitialAd? = null
    private var bannerAdCheck = false
    private var interstitialAdsAdCheck = false
    private var chbInterstitialAds: MaterialCheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
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
    }

    private fun shoDialogBox() {
        val addDialog = Dialog(this)
        addDialog.setContentView(R.layout.dialog_box_select_adds)
        addDialog.setTitle("Select Ads")
        val chbBannerAds = addDialog.chbBannerAds
        chbInterstitialAds = addDialog.chbInterstitialAds
        val btnOk = addDialog.btnOk
        val btnCancel = addDialog.btnCancel
        chbBannerAds.setOnClickListener {
            bannerAdCheck = !bannerAdCheck
        }
        chbInterstitialAds?.setOnClickListener {
            interstitialAdsAdCheck = !interstitialAdsAdCheck
        }
        btnCancel.setOnClickListener {
            addDialog.dismiss()
        }
        btnOk.setOnClickListener {
            if (interstitialAdsAdCheck) {
                showInterstitialAds()
            } else {
                goToNextScreen()
            }
        }
        addDialog.show()
    }

    private fun loadAdd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAdd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAdd = interstitialAd
                    interstitialAdd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                goToNextScreen()
                                interstitialAdd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Log.d(TAG, "Ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Ad showed fullscreen content.")
                                interstitialAdd = null
                            }
                        }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        loadAdd()
    }

    private fun goToNextScreen() {
        val intentAdMob = Intent(this, AdMobActivity::class.java)
        intentAdMob.putExtra("bannerAds", bannerAdCheck)
        intentAdMob.putExtra("interstitialAds", interstitialAdsAdCheck)
        startActivity(intentAdMob)
    }

    private fun showInterstitialAds() {
        if (interstitialAdd != null) {
            interstitialAdd?.show(this)
        }
    }
}