package com.regt17fd.manpower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.ui.navigation.ManpowerNavGraph
import com.regt17fd.manpower.ui.theme.ManpowerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManpowerTheme {
                // Any tap/drag anywhere resets the 15-minute idle timer (spec's
                // auto-logout requirement); the background loop below is what
                // actually enforces it once the user stops interacting.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial)
                                    sessionManager.touch()
                                }
                            }
                        },
                ) {
                    ManpowerNavGraph(sessionManager = sessionManager)
                }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(30_000)
                        sessionManager.checkIdleTimeout()
                    }
                }
            }
        }
    }
}
