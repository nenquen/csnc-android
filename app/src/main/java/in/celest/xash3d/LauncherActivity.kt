package `in`.celest.xash3d

import android.graphics.Color
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import android.provider.DocumentsContract
import android.os.Environment
import android.net.Uri
import android.provider.Settings
import `in`.celest.xash3d.csbtem.R
import `in`.celest.xash3d.ui.LauncherScreen
import `in`.celest.xash3d.ui.theme.CSBootstrapTheme
import su.xash.fwgslib.CertCheck
import su.xash.fwgslib.FWGSLib

class LauncherActivity : ComponentActivity() {
    private lateinit var mPref: SharedPreferences
    
    // State variables for Compose
    private var cmdArgs by mutableStateOf("")
    private var useVolume by mutableStateOf(true)
    private var pixelFormat by mutableStateOf(0)
    private var resizeWorkaround by mutableStateOf(true)
    private var immersiveMode by mutableStateOf(true)
    private var resolution by mutableStateOf(false)
    private var isCustomResolution by mutableStateOf(false)
    private var resWidth by mutableStateOf("640")
    private var resHeight by mutableStateOf("480")
    private var resScale by mutableStateOf("1.0")
    private var resPath by mutableStateOf("")
    
    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            val path = getPathFromUri(it)
            if (path != null) {
                resPath = path
                mPref.edit().putString("basedir", path).apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        
        if (CertCheck.dumbAntiPDALifeCheck(this)) {
            finish()
            return
        }

        mPref = getSharedPreferences("engine", 0)
        loadSettings()

		ensureStorageAccess()

        setContent {
            CSBootstrapTheme {
                LauncherScreen(
                    cmdArgs = cmdArgs,
                    onCmdArgsChange = { cmdArgs = it },
                    useVolume = useVolume,
                    onUseVolumeChange = { useVolume = it },
                    pixelFormat = pixelFormat,
                    onPixelFormatChange = { pixelFormat = it },
                    resizeWorkaround = resizeWorkaround,
                    onResizeWorkaroundChange = { resizeWorkaround = it },
                    immersiveMode = immersiveMode,
                    onImmersiveModeChange = { immersiveMode = it },
                    resolution = resolution,
                    onResolutionChange = { resolution = it },
                    isCustomResolution = isCustomResolution,
                    onIsCustomResolutionChange = { isCustomResolution = it },
                    resWidth = resWidth,
                    onResWidthChange = { resWidth = it },
                    resHeight = resHeight,
                    onResHeightChange = { resHeight = it },
                    resScale = resScale,
                    onResScaleChange = { resScale = it },
                    resPath = resPath,
                    onPathClick = { folderPickerLauncher.launch(null) },
                    onLaunchClick = { startXash() }
                )
            }
        }
    }

    private fun loadSettings() {
        cmdArgs = mPref.getString("argv", "") ?: ""
        useVolume = mPref.getBoolean("usevolume", true)
        pixelFormat = mPref.getInt("pixelformat", 0)
        resizeWorkaround = mPref.getBoolean("enableResizeWorkaround", true)
        immersiveMode = mPref.getBoolean("immersive_mode", true)
        resolution = mPref.getBoolean("resolution_fixed", false)
        isCustomResolution = mPref.getBoolean("resolution_custom", false)
        resWidth = mPref.getInt("resolution_width", 640).toString()
        resHeight = mPref.getInt("resolution_height", 480).toString()
        resScale = mPref.getFloat("resolution_scale", 1.0f).toString()
        resPath = mPref.getString("basedir", FWGSLib.getDefaultXashPath(this)) ?: ""
    }

    private fun startXash() {
		ensureStorageAccess()
        val intent = Intent(this, XashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        mPref.edit().apply {
            putString("argv", cmdArgs)
            putBoolean("usevolume", useVolume)
            putInt("pixelformat", pixelFormat)
            putBoolean("enableResizeWorkaround", resizeWorkaround)
            putBoolean("resolution_fixed", resolution)
            putBoolean("resolution_custom", isCustomResolution)
            putInt("resolution_width", resWidth.toIntOrNull() ?: 640)
            putInt("resolution_height", resHeight.toIntOrNull() ?: 480)
            putFloat("resolution_scale", resScale.toFloatOrNull() ?: 1.0f)
            apply()
        }
        startActivity(intent)
    }

	private fun ensureStorageAccess() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				try {
					val intent = Intent(
						Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
						Uri.parse("package:$packageName")
					)
					startActivity(intent)
				} catch (e: Exception) {
					val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
					startActivity(intent)
				}
			}
		}
	}

    private fun getPathFromUri(uri: android.net.Uri): String? {
        return try {
            val docId = DocumentsContract.getTreeDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                if (split.size > 1) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    Environment.getExternalStorageDirectory().toString() + "/"
                }
            } else {
                "/storage/" + type + (if (split.size > 1) "/" + split[1] else "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun aboutXash() {
        // Handled by Compose state in LauncherScreen
    }
}
