package com.launcher.launcherpackage.Codebizz_helper

import android.R
import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.launcher.launcherpackage.databinding.PmDialogLoadingRewardedBinding
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsManager.sendAdImpressionRevenueToFirebase

object Codebizz_RewardedAdLoader {
    private var rewardedInterstitial: RewardedAd? = null
    private var onRewardAdAction: OnRewardAdAction? = null

    private const val TAG = "RewardedAdLoader"

    interface OnRewardAdAction {
        fun onRewarded()
        fun onAdShowing()
        fun onDismiss()
        fun onRewardNotEarned()
    }


    fun loadAndShowRewardAd(
        activity: Activity,
        adId: String,
        onRewardAdAction: OnRewardAdAction,
    ) {
        this@Codebizz_RewardedAdLoader.onRewardAdAction = onRewardAdAction
        if (rewardedInterstitial != null) {
            onRewardAdAction.onAdShowing()
            rewardedInterstitial?.show(activity) { rewardItem ->
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type

                onRewardAdAction.onRewarded()
                Log.d(TAG, "User earned the reward: $rewardType $rewardAmount")
            }
        } else {


            Dialog(activity).apply {
                val binding = PmDialogLoadingRewardedBinding.inflate(activity.layoutInflater)
                setContentView(binding.root)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                val request = AdRequest.Builder().build()
                RewardedAd.load(
                    activity,
                    adId,
                    request,
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) {
                            super.onAdLoaded(ad)
                            Log.e(TAG, "Reward onAdLoaded: ")

                            ad.setOnPaidEventListener { adValue ->
                                sendAdImpressionRevenueToFirebase(
                                    adValue = adValue, adUnitId = ad.adUnitId
                                )
                            }

                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent()
                                    Log.e(TAG, "onAdDismissedFullScreenContent: ")
                                    onRewardAdAction.onDismiss()
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                    Log.e(TAG, "onAdClicked: ")
                                }

                                override fun onAdImpression() {
                                    super.onAdImpression()
                                    Log.e(TAG, "onAdImpression: ")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent()
                                    Log.e(TAG, "onAdShowedFullScreenContent: ")
                                }

                                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                    super.onAdFailedToShowFullScreenContent(error)
                                    Log.e(
                                        TAG,
                                        "onAdFailedToShowFullScreenContent: ${error.message}"
                                    )
                                    onRewardAdAction.onRewardNotEarned()
                                }
                            }


                            Handler(Looper.getMainLooper()).postDelayed({
                                try {
                                    dismiss()
                                } catch (e: Exception) {
                                    Log.e(TAG, "showInterstitial: ${e.message}")
                                }
                            }, 800)
                            onRewardAdAction.onAdShowing()
                            ad.show(
                                activity
                            ) { _ ->


                                onRewardAdAction.onRewarded()

                                Log.d("TAG", "User earned the reward.")
                            }

                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            super.onAdFailedToLoad(error)
                            Log.e(TAG, "Reward onAdFailedToLoad: ${error.message}")
                            rewardedInterstitial = null
                            try {
                                dismiss()
                            } catch (e: Exception) {
                                Log.e(TAG, "showInterstitial: ${e.message}")
                            }
                            onRewardAdAction.onRewardNotEarned()
                        }
                    })

                window?.apply {
                    setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundDrawableResource(R.color.transparent)
                    setGravity(Gravity.CENTER)
                }

                show()
            }
        }

    }
}