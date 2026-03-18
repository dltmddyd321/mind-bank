package com.windrr.mindbank.presentation.ui.activity

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
    val biometricAvailable = remember {
        androidx.biometric.BiometricManager.from(context)
            .canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(Unit) {
        biometricEnabled = dataStoreViewModel.isBiometricEnabled()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        biometricEnabled = !biometricEnabled
                        dataStoreViewModel.setBiometricEnabled(biometricEnabled)
                    }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpaceSurface
        ),
        border = if (isEnabled) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                SpacePurple.copy(alpha = 0.5f)
            )
        } else {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                SpaceBorder.copy(alpha = 0.3f)
            )
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
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
                        if (isEnabled) 
                            SpacePurple.copy(alpha = 0.2f)
                        else 
                            SpaceBorder.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isEnabled) SpacePurple else SpaceCloud.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = SpaceCloud
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
                        .background(
                            SpacePurple,
                            RoundedCornerShape(12.dp)
                        ),
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
