package com.reedsloan.beekeepingapp.presentation.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.reedsloan.beekeepingapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {
    private var mInterstitialAd: InterstitialAd? = null

    // flow to emit ad state
    private val _adState = MutableStateFlow(AdState(isAdPlaying = false))
    val adState = _adState.asStateFlow()

    // Create a full screen content callback.
    private val fullScreenContentCallback: FullScreenContentCallback =
        object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                // Proceed to the next level.
                Log.d(this::class.simpleName, "Ad was dismissed.")
                _adState.update {
                    AdState(
                        isAdFinished = true,
                        isAdLoaded = false,
                        isAdFailedToPlay = false,
                        isAdPlaying = false
                    )
                }
            }
        }

    fun loadAd() {
        viewModelScope.launch {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                app,
                app.resources.getString(R.string.admob_interstitial_ad),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(this::class.simpleName, it) }
                        mInterstitialAd = null
                        _adState.update {
                            AdState(
                                isAdFinished = false,
                                isAdLoaded = false,
                                isAdFailedToPlay = false,
                                isAdPlaying = false
                            )
                        }
                        Log.d(this::class.simpleName, "Ad failed to load.")
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(this::class.simpleName, "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                        _adState.update {
                            AdState(
                                isAdFinished = false,
                                isAdLoaded = true,
                                isAdFailedToPlay = false,
                                isAdPlaying = false,
                            )
                        }
                        Log.d(this::class.simpleName, "Ad was loaded.")
                    }
                })
        }
    }

    fun showAd(activity: Activity) {
        if (adState.value.isAdLoaded) {
            mInterstitialAd?.fullScreenContentCallback = fullScreenContentCallback
            mInterstitialAd?.show(activity)
            _adState.update {
                AdState(
                    isAdLoaded = true,
                    isAdFailedToPlay = false,
                    isAdFinished = false,
                    isAdPlaying = true
                )
            }
        } else {
            _adState.update {
                AdState(
                    isAdLoaded = true,
                    // we update this so the LaunchedEffect will simply navigate back
                    isAdFailedToPlay = true,
                    isAdFinished = false,
                    isAdPlaying = false
                )
            }
            Log.d(this::class.simpleName, "The interstitial ad wasn't ready yet.")
        }
    }
}