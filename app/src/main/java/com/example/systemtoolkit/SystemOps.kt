package com.example.systemtoolkit

import android.app.ActivityManager
import android.content.Context
import android.os.PowerManager
import android.provider.Settings

/** Shell, root and settings helpers. Root commands only work on a rooted
    phone or a platform-signed build; without them each call fails cleanly. */
object SystemOps {
    data class ShellResult(val ok: Boolean, val output: String)

    fun shell(vararg command: String): ShellResult {
        return try {
            val process = ProcessBuilder(*command).redirectErrorStream(true).start()
            val text = process.inputStream.bufferedReader().use { it.readText() }
            val code = process.waitFor()
            ShellResult(code == 0, text.trim())
        } catch (e: Exception) {
            ShellResult(false, e.message ?: "shell failed")
        }
    }

    fun runAsRoot(command: String): ShellResult = shell("su", "-c", command)

    fun isRooted(): Boolean {
        val probe = runAsRoot("id")
        return probe.ok && probe.output.contains("uid=0")
    }

    fun writeSetting(context: Context, key: String, value: String): ShellResult {
        return try {
            val ok = Settings.System.putString(context.contentResolver, key, value)
            ShellResult(ok, key + " = " + value)
        } catch (e: Exception) {
            ShellResult(false, e.message ?: "WRITE_SETTINGS denied")
        }
    }

    fun killBackground(context: Context, target: String): ShellResult {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(target)
            ShellResult(true, "killed " + target)
        } catch (e: Exception) {
            ShellResult(false, e.message ?: "kill failed")
        }
    }

    fun batteryExempt(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
