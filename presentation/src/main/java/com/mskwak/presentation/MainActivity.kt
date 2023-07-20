package com.mskwak.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager
            .findFragmentById(binding.layoutFragmentContainer.id) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.plantFragment -> {
                    navController.navigate(R.id.actionGlobalHomeFragment)
                    true
                }
                R.id.diaryFragment -> {
                    navController.navigate(R.id.actionGlobalDiaryFragment)
                    true
                }
                R.id.settingFragment -> {
                    navController.navigate(R.id.actionGlobalSettingFragment)
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
}