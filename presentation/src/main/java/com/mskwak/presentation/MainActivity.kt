package com.mskwak.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mskwak.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
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
}