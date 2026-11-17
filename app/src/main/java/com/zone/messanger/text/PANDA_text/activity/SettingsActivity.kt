package com.zone.messanger.text.PANDA_text.activity

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import com.zone.messanger.text.databinding.ActivitySettingsBinding
import com.zone.messanger.text.Repository.SmsRepository

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var repository: SmsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SmsRepository(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.rowDefaultSms.setOnClickListener { openDefaultSmsFlow() }
    }

    override fun onResume() {
        super.onResume()
        binding.txtDefaultSmsStatus.text =
            if (repository.isDefaultSmsApp()) "Messenger is your default SMS app"
            else "Not set as default — tap to change"
    }

    private fun openDefaultSmsFlow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                startActivity(roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS))
                return
            }
        }
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        startActivity(intent)
    }
}
