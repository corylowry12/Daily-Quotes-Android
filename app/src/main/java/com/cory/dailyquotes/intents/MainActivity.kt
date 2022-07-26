package com.cory.dailyquotes.intents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.dailyquotes.R
import com.cory.dailyquotes.fragments.HomeFragment
import com.cory.dailyquotes.fragments.SettingsFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val homeFragment : HomeFragment = HomeFragment()
    val settingsFragment: SettingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        GlobalScope.launch(Dispatchers.Main) {
            MobileAds.initialize(context)
            val adView = AdView(context)
            adView.adUnitId = "ca-app-pub-4546055219731501/6930432673"
            val mAdView = findViewById<AdView>(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        }

        replaceFragment(homeFragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.people -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.settings -> {
                    replaceFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

        bottomNav.itemActiveIndicatorColor =
            ContextCompat.getColorStateList(this, R.color.itemIndicatorColor)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        transaction.replace(R.id.fragment_container_main, fragment).addToBackStack(null)
        transaction.commit()

    }

    fun peopleTextViewVisibility() {
        homeFragment.textViewVisibility()
    }
}