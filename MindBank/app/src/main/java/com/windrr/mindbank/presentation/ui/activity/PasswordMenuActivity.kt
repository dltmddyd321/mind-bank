package com.windrr.mindbank.presentation.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.windrr.mindbank.R
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceCloud
import com.windrr.mindbank.presentation.ui.theme.SpaceCoral
import com.windrr.mindbank.presentation.ui.theme.SpacePurple
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
import com.windrr.mindbank.presentation.ui.theme.SpaceTheme
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
@AndroidEntryPoint
class PasswordMenuActivity : ComponentActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordMenuScreen(
                        context = this@PasswordMenuActivity,
                        dataStoreViewModel = dataStoreViewModel
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PasswordMenuScreen(context: Context, dataStoreViewModel: DataStoreViewModel) {
    var biometricEnabled by remember { mutableStateOf(false) }
    var appLockEnabled by remember { mutableStateOf(false) }
    var hasPinRegistered by remember { mutableStateOf(false) }
    var showDisableDialog by remember { mutableStateOf(false) }
    var showBiometricBlockedDialog by remember { mutableStateOf(false) }

    val biometricAvailable = remember {
        androidx.biometric.BiometricManager.from(context)
            .canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(Unit) {
        biometricEnabled = dataStoreViewModel.isBiometricEnabled()
        appLockEnabled = dataStoreViewModel.isAppLockEnabled()
        hasPinRegistered = dataStoreViewModel.getPasswordHash().isNotEmpty()
    }

    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            title = { Text(stringResource(R.string.app_lock_disable_confirm_title)) },
            text = { Text(stringResource(R.string.app_lock_disable_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataStoreViewModel.setAppLockEnabled(false)
                        dataStoreViewModel.setPassword("")
                        dataStoreViewModel.setPasswordHash("")
                        dataStoreViewModel.setPasswordSalt("")
                        dataStoreViewModel.setBiometricEnabled(false)
                        appLockEnabled = false
                        biometricEnabled = false
                        hasPinRegistered = false
                        showDisableDialog = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        color = SpaceCoral
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisableDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showBiometricBlockedDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricBlockedDialog = false },
            title = { Text(stringResource(R.string.biometric_disable_blocked_title)) },
            text = { Text(stringResource(R.string.biometric_disable_blocked_message)) },
            confirmButton = {
                TextButton(onClick = { showBiometricBlockedDialog = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecurityOptionCard(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.pin_setup),
                description = "PIN 번호로 앱 잠금 설정",
                onClick = {
                    context.startActivity(Intent(context, PasswordEditActivity::class.java))
                }
            )

            if (biometricAvailable) {
                SecurityOptionCard(
                    icon = Icons.Default.Face,
                    title = if (biometricEnabled) {
                        stringResource(R.string.biometric_disable)
                    } else {
                        stringResource(R.string.biometric_enable)
                    },
                    description = "생체 인증으로 빠른 접속",
                    isEnabled = biometricEnabled,
                    onClick = {
                        if (biometricEnabled && !hasPinRegistered) {
                            // PIN 없이 생체인증만 등록된 상태 → OFF 차단
                            showBiometricBlockedDialog = true
                        } else {
                            biometricEnabled = !biometricEnabled
                            dataStoreViewModel.setBiometricEnabled(biometricEnabled)
                            if (biometricEnabled) {
                                appLockEnabled = true
                                dataStoreViewModel.setAppLockEnabled(true)
                            }
                        }
                    }
                )
            }

            if (appLockEnabled) {
                SecurityOptionCard(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.app_lock_disable),
                    description = stringResource(R.string.app_lock_disable_description),
                    isDangerous = true,
                    onClick = { showDisableDialog = true }
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SecurityOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean = false,
    isDangerous: Boolean = false,
    onClick: () -> Unit
) {
    val accentColor = when {
        isDangerous -> SpaceCoral
        isEnabled -> SpacePurple
        else -> SpaceBorder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isEnabled || isDangerous) 6.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = accentColor.copy(alpha = 0.2f),
                spotColor = accentColor.copy(alpha = 0.2f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpaceSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isEnabled || isDangerous) 2.dp else 1.dp,
            color = accentColor.copy(alpha = if (isEnabled || isDangerous) 0.5f else 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        accentColor.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (isDangerous) SpaceCoral else SpaceCloud
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SpaceCloud.copy(alpha = 0.6f)
                    )
                )
            }

            if (isEnabled) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(SpacePurple, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.checked_img),
                        contentDescription = "Enabled",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
