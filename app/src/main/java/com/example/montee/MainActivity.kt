package com.example.montee

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Screen() } }
    }

    private fun startClimb() =
        ContextCompat.startForegroundService(this, Intent(this, ClimbService::class.java))

    private fun stopClimb() = stopService(Intent(this, ClimbService::class.java))

    @Composable
    private fun Screen() {
        val stats by ClimbState.stats.collectAsStateWithLifecycle()
        val running by ClimbState.running.collectAsStateWithLifecycle()

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { res ->
            if (res[Manifest.permission.ACCESS_FINE_LOCATION] == true) startClimb()
        }

        Column(
            Modifier.statusBarsPadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Vitesse ascensionnelle", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(stats.status)
            Text(
                stats.altitude?.let { "Altitude : %.0f m".format(it) } ?: "Altitude : —",
                fontSize = 18.sp
            )
            HorizontalDivider()
            stats.rows.forEach { r ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(r.label, fontSize = 20.sp)
                    Text(
                        r.rate?.let { "%+.0f m/h".format(it) } ?: "—",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                    Text(r.delta?.let { "%+.0f m".format(it) } ?: "", fontSize = 16.sp)
                }
            }
            Button(
                onClick = {
                    if (running) stopClimb() else {
                        val perms = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        launcher.launch(perms.toTypedArray())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (running) "Arrêter / réinitialiser" else "Démarrer") }
        }
    }
}
