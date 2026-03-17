package com.windrr.mindbank.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.windrr.mindbank.R
import com.windrr.mindbank.presentation.ui.activity.ui.theme.MindBankTheme
import com.windrr.mindbank.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordMenuActivity : ComponentActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindBankTheme {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                context.startActivity(Intent(context, PasswordEditActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.pin_setup))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (biometricAvailable) {
            Button(
                onClick = {
                    biometricEnabled = !biometricEnabled
                    dataStoreViewModel.setBiometricEnabled(biometricEnabled)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (biometricEnabled) {
                        stringResource(R.string.biometric_disable)
                    } else {
                        stringResource(R.string.biometric_enable)
                    }
                )
            }
        }
    }
}
