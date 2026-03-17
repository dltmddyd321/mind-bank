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
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.windrr.mindbank.presentation.ui.activity.ui.theme.MindBankTheme
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import androidx.core.net.toUri
import com.windrr.mindbank.R
import timber.log.Timber
import java.util.Base64
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
            MindBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppLockScreen(
                        isBiometricEnabled = biometricEnabled,
                        isBiometricAvailable = isBiometricAvailable(),
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
    onBiometricAuth: (onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    verifyPin: (String) -> Boolean,
    onPinVerified: () -> Unit,
) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (isBiometricEnabled && isBiometricAvailable) {
            onBiometricAuth(
                { onPinVerified() },
                { error ->
                    message = error
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title = if (isLegacyPinFallback) {
            context.getString(R.string.biometric_auth_prompt)
        } else {
            context.getString(R.string.biometric_auth_prompt)
        }
        Text(text = title)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = pin,
            onValueChange = { value ->
                pin = value.filter { it.isDigit() }.take(6)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            singleLine = true
        )

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isBiometricEnabled && isBiometricAvailable) {
                Button(
                    onClick = {
                        onBiometricAuth(
                            { onPinVerified() },
                            { error -> message = error }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.biometric_button))
                }
            }

            Button(
                onClick = {
                    if (verifyPin(pin)) {
                        onPinVerified()
                    } else {
                        message = context.getString(R.string.password_wrong)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    }
}