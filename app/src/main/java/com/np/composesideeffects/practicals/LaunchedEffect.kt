package com.np.composesideeffects.practicals

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme
import kotlinx.coroutines.delay

class LaunchedEffect: BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    private val screen = "LaunchedEffect"
    private val localScreen = staticCompositionLocalOf  { screen }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appLog(screen)
    }

    @Composable
    private fun ComposeView() {
        ComposeSideEffectsTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val launchedEffectKey = remember { mutableStateOf(true) }
                val recompose = remember { mutableIntStateOf(1001) }
                val composeAgain = remember { mutableStateOf(false) }
                val showCompose = remember { mutableStateOf(true) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

                    Button(onClick = { launchedEffectKey.value = !launchedEffectKey.value }) {
                        Text("Update LaunchedEffect Key")
                    }

                    Button(onClick = { recompose.intValue += 123 }) {
                        Text("Recompose")
                    }

                    Button(onClick = { composeAgain.value = !composeAgain.value }) {
                        Text("Compose Again")
                    }

                    Button(onClick = { showCompose.value = !showCompose.value }) {
                        Text((if(showCompose.value) "Remove" else "Add") + " Compose")
                    }

                    if(showCompose.value) {
                        key(composeAgain.value) {
                            Counter(launchedEffectKey.value, recompose.intValue)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Counter(key: Boolean, state: Int) {
        appLog("State: $state")

        val count = remember { mutableIntStateOf(0) }
        LaunchedEffect(key1 = key) {
            appLog("LaunchedEffectKey: $key")
            var i = 0
            while (true) {
                delay(1000)
                appLog("Tick: ${i++}")
                count.intValue = i
            }
        }

        Text("LaunchedEffectKey: $key")
        Text("State: $state")
        Text("Count: ${count.intValue}")
    }
}

/*
    LaunchedEffect is a Composable function that executes a side effect in a separate coroutine
scope.

    The function executes in a separate coroutine scope, allowing the UI to remain responsive while
the operation is being performed.

    The key parameter in LaunchedEffect is used to identify the LaunchedEffect instance and prevent
it from being recomposed unnecessarily.

    When a Composable is recomposed, Jetpack Compose determines if it needs to be redrawn. If a
Composable’s state or props have changed, or if a Composable has called invalidate, Jetpack Compose
will redraw the Composable. Redrawing a Composable can be an expensive operation, especially if the
Composable contains long-running operations or side effects that don't need to be re-executed every
time the Composable is recomposed.

    By providing a key parameter to LaunchedEffect, we can specify a value that uniquely identifies
the LaunchedEffect instance. If the value of the key parameter changes, Jetpack Compose will
consider the LaunchedEffect instance as a new instance, and will execute the side effect again. If
the value of the key parameter remains the same, Jetpack Compose will skip the execution of the side
effect and reuse the previous result, preventing unnecessary recompositions.

    You also can use several keys for LaunchedEffect :
        Example:
        LaunchedEffect(0, "Kay-1", true, 3.0, 4F, null, Unit, Any?) {}

    Use Cases of LaunchedEffect:
        - Fetching Data from a Network
        - Performing Image Processing
        - Updating a Database
*/
