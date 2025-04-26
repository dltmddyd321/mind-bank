package com.example.mindbank.presentation.navigation.activity

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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.example.mindbank.presentation.navigation.activity.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.DataStoreViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import androidx.core.net.toUri
import com.example.mindbank.R


@AndroidEntryPoint
class PasswordActivity : ComponentActivity() {

    @SuppressLint("InlinedApi")
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else listOf(Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.POST_NOTIFICATIONS)

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setPermissions(*permission.toTypedArray())
                .setDeniedMessage(getString(R.string.alarm_permission_message))
                .check()
        }
    private lateinit var splash: androidx.core.splashscreen.SplashScreen
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private var password = ""
    private var onCheckPassword: (() -> Unit?)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        onCheckPassword = {
            setContent {
                MindBankTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BiometricAuthScreen()
                    }
                }
            }
        }
        fetchPassword()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun fetchPassword() {
        CoroutineScope(Dispatchers.IO).launch {
            password = dataStoreViewModel.getPassWord()
            withContext(Dispatchers.Main) {
                requestAlarmPermission(this@PasswordActivity)
            }
        }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(this@PasswordActivity, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            start { onCheckPassword?.invoke() }
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(
                this@PasswordActivity,
                "${getString(R.string.permission_denied)}\n$deniedPermissions", Toast.LENGTH_SHORT
            ).show()
            start { onCheckPassword?.invoke() }
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
                start { onCheckPassword?.invoke() }
            }
        } else {
            start { onCheckPassword?.invoke() }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun start(onCheckPassword: () -> Unit) {
        val intent = Intent(this@PasswordActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if (password.isEmpty()) startActivity(intent) else { onCheckPassword.invoke() }
    }

    @SuppressLint("Recycle")
    private fun startSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashView ->
                val icon = splashView.iconView ?: return@setOnExitAnimationListener

                ObjectAnimator.ofPropertyValuesHolder(icon).run {
                    interpolator = AnticipateInterpolator()
                    repeatCount = 2 // 반복 횟수
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
fun BiometricAuthScreen() {
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
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
                    onFailure("Authentication failed")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    val context = LocalContext.current
    var authMessage by remember { mutableStateOf("지문 인증을 진행해주세요.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = authMessage)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (isBiometricAvailable(context)) {
                    showBiometricPrompt(
                        context,
                        onSuccess = { authMessage = "인증 성공!" },
                        onFailure = { errorMsg -> authMessage = "인증 실패: $errorMsg" }
                    )
                } else {
                    authMessage = "지문 인증을 사용할 수 없습니다."
                }
            }
        ) {
            Text("지문 인증하기")
        }
    }
}