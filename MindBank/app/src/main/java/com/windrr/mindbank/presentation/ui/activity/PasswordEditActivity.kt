package com.windrr.mindbank.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@AndroidEntryPoint
class PasswordEditActivity : ComponentActivity() {

    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoading by remember { mutableStateOf(true) }
                    var legacyPassword by remember { mutableStateOf("") }
                    var savedHash by remember { mutableStateOf("") }
                    var savedSalt by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        legacyPassword = dataStoreViewModel.getPassWord()
                        savedHash = dataStoreViewModel.getPasswordHash()
                        savedSalt = dataStoreViewModel.getPasswordSalt()
                        isLoading = false
                    }

                    if (!isLoading) {
                        val hasSavedPin = savedHash.isNotEmpty() || legacyPassword.isNotEmpty()
                        PinEditFlow(
                            hasSavedPin = hasSavedPin,
                            verifyCurrentPin = { pin -> verifyPin(pin, savedHash, savedSalt, legacyPassword) },
                            onSavePin = { newPin ->
                                val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
                                val hash = pbkdf2Hash(newPin, saltBytes)
                                dataStoreViewModel.setPasswordSalt(Base64.getEncoder().encodeToString(saltBytes))
                                dataStoreViewModel.setPasswordHash(hash)
                                dataStoreViewModel.setAppLockEnabled(true)
                                dataStoreViewModel.setPassword("")
                                startActivity(
                                    Intent(this@PasswordEditActivity, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    }
                                )
                                finish()
                            },
                            onCancel = { finish() }
                        )
                    }
                }
            }
        }
    }

    private fun verifyPin(pin: String, hash: String, salt: String, legacy: String): Boolean {
        if (hash.isEmpty() && legacy.isNotEmpty()) return pin == legacy
        if (salt.isEmpty() || hash.isEmpty()) return false
        val saltBytes = Base64.getDecoder().decode(salt)
        return pbkdf2Hash(pin, saltBytes) == hash
    }

    private fun pbkdf2Hash(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, 120_000, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return Base64.getEncoder().encodeToString(skf.generateSecret(spec).encoded)
    }
}

// Step 0: 기존 PIN 확인 (PIN이 있을 때)
// Step 1: 새 PIN 입력
// Step 2: 새 PIN 확인
@Composable
private fun PinEditFlow(
    hasSavedPin: Boolean,
    verifyCurrentPin: (String) -> Boolean,
    onSavePin: (String) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(if (hasSavedPin) 0 else 1) }
    var newPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val title = when (step) {
        0 -> stringResource(R.string.pin_current_enter)
        1 -> stringResource(R.string.pin_new_enter)
        else -> stringResource(R.string.pin_new_confirm)
    }
    val subtitle = when (step) {
        0 -> stringResource(R.string.pin_current_subtitle)
        1 -> stringResource(R.string.pin_new_subtitle)
        else -> stringResource(R.string.pin_confirm_subtitle)
    }

    BackHandler(enabled = !isProcessing) {
        when {
            step > 1 -> { step--; errorMessage = "" }
            step == 1 && hasSavedPin -> { step = 0; errorMessage = "" }
            else -> onCancel()
        }
    }

    PinDialPad(
        title = title,
        subtitle = subtitle,
        errorMessage = errorMessage,
        isProcessing = isProcessing,
        onBack = {
            if (!isProcessing) {
                when {
                    step > 1 -> { step--; errorMessage = "" }
                    step == 1 && hasSavedPin -> { step = 0; errorMessage = "" }
                    else -> onCancel()
                }
            }
        },
        onComplete = { pin ->
            if (isProcessing) return@PinDialPad
            errorMessage = ""
            when (step) {
                0 -> {
                    // PBKDF2 검증 → 백그라운드 실행
                    isProcessing = true
                    scope.launch {
                        val ok = withContext(Dispatchers.Default) { verifyCurrentPin(pin) }
                        isProcessing = false
                        if (ok) {
                            step = 1
                        } else {
                            errorMessage = "비밀번호가 일치하지 않습니다."
                        }
                    }
                }
                1 -> {
                    newPin = pin
                    step = 2
                }
                2 -> {
                    if (pin == newPin) {
                        onSavePin(pin)
                    } else {
                        errorMessage = "비밀번호가 일치하지 않습니다."
                    }
                }
            }
        }
    )
}

@Composable
private fun PinDialPad(
    title: String,
    subtitle: String,
    errorMessage: String,
    isProcessing: Boolean = false,
    onBack: () -> Unit,
    onComplete: (String) -> Unit
) {
    var pin by remember(title) { mutableStateOf("") }

    LaunchedEffect(title) { pin = "" }

    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "DEL")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceNavy)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 뒤로가기
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SpaceCloud,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // 타이틀 + 점 인디케이터 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SpaceCloud
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SpaceCloud.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 점 인디케이터
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

            // 에러 메시지 (고정 높이 확보해 레이아웃 흔들림 방지)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(color = SpaceCoral),
                minLines = 1
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 키패드
        Column(
            modifier = Modifier.padding(bottom = 40.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        when (key) {
                            "" -> Box(modifier = Modifier.size(72.dp))
                            "DEL" -> DialButton(
                                onClick = {
                                    if (!isProcessing && pin.isNotEmpty()) pin = pin.dropLast(1)
                                },
                                isLoading = false
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Delete",
                                    tint = SpaceCloud,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            else -> DialButton(
                                onClick = {
                                    if (!isProcessing && pin.length < 6) {
                                        pin += key
                                        if (pin.length == 6) onComplete(pin)
                                    }
                                },
                                isLoading = isProcessing && pin.length == 6
                            ) {
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
    }
}

@Composable
private fun DialButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(SpaceSurface, CircleShape)
            .border(1.dp, SpaceBorder.copy(alpha = 0.4f), CircleShape)
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = SpacePurple,
                strokeWidth = 2.dp
            )
        } else {
            content()
        }
    }
}
