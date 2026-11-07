package com.example.systemtoolkit

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/** Entry screen: every deep permission is requested explicitly and the
    result stays visible in the status line. */
class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)
        status = findViewById(R.id.status)
        findViewById<Button>(R.id.btnOverlay).setOnClickListener { askOverlay() }
        findViewById<Button>(R.id.btnWriteSettings).setOnClickListener { askWriteSettings() }
        findViewById<Button>(R.id.btnAdmin).setOnClickListener { askAdmin() }
        findViewById<Button>(R.id.btnService).setOnClickListener { startOverlay() }
        findViewById<Button>(R.id.btnShell).setOnClickListener { probeShell() }
        show("root: " + SystemOps.isRooted() + " · overlay: " + canOverlay())
    }

    private fun canOverlay(): Boolean = Settings.canDrawOverlays(this)

    private fun show(text: String) {
        status.text = text
    }

    private fun askOverlay() {
        if (canOverlay()) { show("overlay: មានសិទ្ធិរួចហើយ"); return }
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + packageName))
        startActivity(intent)
    }

    private fun askWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:" + packageName))
        startActivity(intent)
    }

    private fun askAdmin() {
        val admin = ComponentName(this, AdminReceiver::class.java)
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isAdminActive(admin)) { show("device admin: សកម្មរួចហើយ"); return }
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "សម្រាប់មុខងារប្រព័ន្ធ")
        startActivity(intent)
    }

    private fun startOverlay() {
        ContextCompat.startForegroundService(this, Intent(this, OverlayService::class.java))
        show("overlay service: កំពុងដំណើរការ")
    }

    private fun probeShell() {
        val rooted = SystemOps.isRooted()
        val out = if (rooted) SystemOps.runAsRoot("id") else SystemOps.shell("sh", "-c", "id")
        val battery = SystemOps.batteryExempt(this)
        show("root: " + rooted + " · id: " + out.output + " · battery exempt: " + battery)
    }
}
