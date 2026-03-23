package com.windrr.mindbank.presentation.ui.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.windrr.mindbank.R
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceCloud
import com.windrr.mindbank.presentation.ui.theme.SpaceCoral
import com.windrr.mindbank.presentation.ui.theme.SpaceNavy
import com.windrr.mindbank.presentation.ui.theme.SpacePurple
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
import com.windrr.mindbank.presentation.ui.theme.SpaceTheme
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Base64
import java.util.concurrent.Executor
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


@AndroidEntryPoint
class PasswordActivity : FragmentActivity() {

    @SuppressLint("InlinedApi")
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else listOf(
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.POST_NOTIFICATIONS
            )

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setPermissions(*permission.toTypedArray())
                .setDeniedMessage(getString(R.string.alarm_permission_message))
                .check()
        }
    private lateinit var splash: androidx.core.splashscreen.SplashScreen
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private var legacyPassword = ""
    private var appLockEnabled = false
    private var biometricEnabled = false
    private var passwordSalt = ""
    private var passwordHash = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        fetchLockStateAndStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun fetchLockStateAndStart() {
        CoroutineScope(Dispatchers.IO).launch {
            legacyPassword = dataStoreViewModel.getPassWord()
            appLockEnabled = dataStoreViewModel.isAppLockEnabled()
            biometricEnabled = dataStoreViewModel.isBiometricEnabled()
            passwordSalt = dataStoreViewModel.getPasswordSalt()
            passwordHash = dataStoreViewModel.getPasswordHash()
            withContext(Dispatchers.Main) {
                start()
            }
        }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(
                this@PasswordActivity,
                getString(R.string.permission_granted),
                Toast.LENGTH_SHORT
            ).show()
            start()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(
                this@PasswordActivity,
                "${getString(R.string.permission_denied)}\n$deniedPermissions", Toast.LENGTH_SHORT
            ).show()
            start()
        }
    }

    private fun requestAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
                permissionLauncher.launch(intent)
            } else {
                start()
            }
        } else {
            start()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun start() {
        val deepLink = intent?.data
        val intent = Intent(this@PasswordActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data = deepLink
            intent?.extras?.let { putExtras(it) }
        }
        Timber.tag("인텐트 확인").i(intent.data?.toString())
        if (!appLockEnabled) {
            startActivity(intent)
            return
        }

        setContent {
            SpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppLockScreen(
                        isBiometricEnabled = biometricEnabled,
                        isBiometricAvailable = isBiometricAvailable(),
                        hasPinRegistered = passwordHash.isNotEmpty() || legacyPassword.isNotEmpty(),
                        onBiometricAuth = { onSuccess, onFailure ->
                            showBiometricPrompt(
                                onSuccess = onSuccess,
                                onFailure = onFailure
                            )
                        },
                        onPinVerified = {
                            startActivity(intent)
                        },
                        verifyPin = { pin ->
                            verifyPin(pin)
                        },
                        isLegacyPinFallback = passwordHash.isEmpty() && legacyPassword.isNotEmpty(),
                    )
                }
            }
        }
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onFailure(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure(getString(R.string.biometric_failed))
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun verifyPin(pin: String): Boolean {
        if (passwordHash.isEmpty() && legacyPassword.isNotEmpty()) {
            return pin == legacyPassword
        }
        if (passwordSalt.isEmpty() || passwordHash.isEmpty()) return false
        val saltBytes = Base64.getDecoder().decode(passwordSalt)
        val computed = pbkdf2Hash(pin, saltBytes)
        return computed == passwordHash
    }

    private fun pbkdf2Hash(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, 120_000, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = skf.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    private fun startSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashView ->
                val icon = splashView.iconView ?: return@setOnExitAnimationListener

                ObjectAnimator.ofPropertyValuesHolder(icon).run {
                    interpolator = AnticipateInterpolator()
                    repeatCount = 2
                    duration = 500L
                    doOnEnd {
                        splashView.remove()
                    }
                    start()
                }
            }
        }
    }
}

@Composable
private fun AppLockScreen(
    isBiometricEnabled: Boolean,
    isBiometricAvailable: Boolean,
    isLegacyPinFallback: Boolean,
    hasPinRegistered: Boolean,
    onBiometricAuth: (onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    verifyPin: (String) -> Boolean,
    onPinVerified: () -> Unit,
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val biometricActive = isBiometricEnabled && isBiometricAvailable

    LaunchedEffect(Unit) {
        if (biometricActive) {
            onBiometricAuth(
                { onPinVerified() },
                { error -> errorMessage = error }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceNavy)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = SpacePurple,
            modifier = Modifier.size(52.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SpaceCloud
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (biometricActive && !hasPinRegistered)
                stringResource(R.string.biometric_auth_prompt)
            else
                stringResource(R.string.pin_auth_prompt),
            style = MaterialTheme.typography.bodyMedium.copy(color = SpaceCloud.copy(alpha = 0.6f))
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (hasPinRegistered) {
            // 6자리 점 인디케이터
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .background(
                                if (index < pin.length) SpacePurple else SpaceBorder,
                                CircleShape
                            )
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(color = SpaceCoral)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 키패드
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(if (biometricActive) "BIO" else "", "0", "DEL")
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "" -> Box(modifier = Modifier.size(72.dp))
                                "DEL" -> KeyPadButton(onClick = {
                                    if (pin.isNotEmpty()) {
                                        pin = pin.dropLast(1)
                                        errorMessage = ""
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Delete",
                                        tint = SpaceCloud,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                "BIO" -> KeyPadButton(
                                    onClick = {
                                        onBiometricAuth(
                                            { onPinVerified() },
                                            { error -> errorMessage = error }
                                        )
                                    },
                                    accentColor = true
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Biometric",
                                        tint = SpacePurple,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                else -> KeyPadButton(onClick = {
                                    if (pin.length < 6) {
                                        pin += key
                                        errorMessage = ""
                                        if (pin.length == 6) {
                                            if (verifyPin(pin)) {
                                                onPinVerified()
                                            } else {
                                                errorMessage = context.getString(R.string.password_wrong)
                                                pin = ""
                                            }
                                        }
                                    }
                                }) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = SpaceCloud
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (biometricActive) {
            // PIN 없이 생체인증만 등록된 경우
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(color = SpaceCoral)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            KeyPadButton(
                onClick = {
                    onBiometricAuth(
                        { onPinVerified() },
                        { error -> errorMessage = error }
                    )
                },
                accentColor = true
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SpacePurple,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = stringResource(R.string.biometric_button),
                        style = MaterialTheme.typography.bodyMedium.copy(color = SpaceCloud)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyPadButton(
    onClick: () -> Unit,
    accentColor: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (accentColor) SpacePurple.copy(alpha = 0.15f) else SpaceSurface,
                CircleShape
            )
            .border(
                1.dp,
                if (accentColor) SpacePurple.copy(alpha = 0.4f) else SpaceBorder.copy(alpha = 0.4f),
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}