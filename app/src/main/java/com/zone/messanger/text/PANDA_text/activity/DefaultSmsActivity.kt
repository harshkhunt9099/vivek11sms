package com.zone.messanger.text.PANDA_text.activity

import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zone.messanger.text.databinding.ActivityDefaultSmsBinding
import com.zone.messanger.text.PANDA_Activity.Spidy_MainActivity

/** Screen 2 of onboarding: ask the user to make this app their default SMS app. */
class DefaultSmsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDefaultSmsBinding
    private var navigated = false

    private val roleRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Cancel કરે તો આગળ ન જાય, ફક્ત default બન્યા પછી જ જાય
            if (isDefaultSmsApp()) {
                goToMain()
            } else {
                Toast.makeText(
                    this,
                    "Please set this app as default SMS app to continue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // પહેલેથી default હોય તો layout બતાવ્યા વગર સીધા આગળ
        if (isDefaultSmsApp()) {
            goToMain()
            return
        }

        binding = ActivityDefaultSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSetDefault.setOnClickListener { requestDefaultSmsRole() }

        // તમારું Skip button (blog/privacy link ખોલે છે) જેમ છે તેમ
        binding.btnSkip.setOnClickListener {
            val url = "https://smspandmessages.blogspot.com/"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Settings માંથી default કરીને પાછા આવે તો પણ આગળ જાય
        if (::binding.isInitialized && isDefaultSmsApp()) goToMain()
    }

    private fun isDefaultSmsApp(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java)
                ?.isRoleHeld(RoleManager.ROLE_SMS) == true
        } else {
            Telephony.Sms.getDefaultSmsPackage(this) == packageName
        }
    }

    private fun requestDefaultSmsRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    roleRequestLauncher.launch(
                        roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                    )
                }
                return
            }
        }
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        roleRequestLauncher.launch(intent)
    }

    private fun goToMain() {
        if (navigated) return
        navigated = true
        startActivity(Intent(this, Spidy_MainActivity::class.java))
        finish()
    }
}
