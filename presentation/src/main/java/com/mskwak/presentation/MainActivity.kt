package com.mskwak.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.mskwak.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backKeyPressedTime: Long = 0
    private var finishToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        initAds()
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragmentDest -> {
                    navController.navigate(R.id.action_global_homeFragmentDest)
                    true
                }
                R.id.diaryFragmentDest -> {
                    navController.navigate(R.id.action_global_diaryFragmentDest)
                    true
                }
                R.id.settingFragmentDest -> {
                    navController.navigate(R.id.action_global_settingFragmentDest)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime.plus(2000)) {
            backKeyPressedTime = System.currentTimeMillis()
            finishToast =
                Toast.makeText(this, getString(R.string.message_finish_app), Toast.LENGTH_SHORT)
            finishToast?.show()
        } else {
            this.finish()
            finishToast?.cancel()
        }
    }

    private fun initAds() {
        MobileAds.initialize(this)
    }
}