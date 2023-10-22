package com.mskwak.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mskwak.presentation.databinding.ActivityMainBinding
import com.mskwak.presentation.util.showPermissionDeniedSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backKeyPressedTime: Long = 0
    private var finishToast: Toast? = null

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showPermissionDeniedSnackbar(binding.root, R.string.message_notification_permission)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED -> Unit

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                this.showPermissionDeniedSnackbar(
                    binding.root,
                    R.string.message_notification_permission
                )
            }

            else -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}