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
import androidx.compose.runtime.DisposableEffect
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PracticeFragment3 : BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview
    @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practical 3: DisposableEffect"
    private val localScreen = staticCompositionLocalOf { screen }

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
                val disposableEffectKey = remember { mutableStateOf(true) }
                val recompose = remember { mutableIntStateOf(1001) }
                val composeAgain = remember { mutableStateOf(false) }
                val showCompose = remember { mutableStateOf(true) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

                    Button(onClick = { disposableEffectKey.value = !disposableEffectKey.value }) {
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
                            Counter(disposableEffectKey.value, recompose.intValue)
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
        DisposableEffect(key) {
            val scope = CoroutineScope(Dispatchers.Default)
            val job = scope.launch {
                appLog("DisposableEffectKey: $key")
                var i = 0
                while (true) {
                    delay(1000)
                    appLog("Tick: ${i++}")
                    count.intValue = i
                }
            }

            onDispose {
                appLog("onDispose()")
                job.cancel()
            }
        }

        Text("DisposableEffectKey: $key")
        Text("State: $state")
        Text("Count: ${count.intValue}")
    }
}

/*
    DisposableEffect is a Composable function that executes a side effect when its parent Composable
is first rendered, and disposes of the effect when the Composable is removed from the UI hierarchy.

    By providing a key parameter to DisposableEffect, we can specify a value that uniquely identifies
the DisposableEffect instance. If the value of the key parameter changes, Jetpack Compose will
consider the DisposableEffect instance as a new instance, and will execute the side effect again. If
the value of the key parameter remains the same, Jetpack Compose will skip the execution of the side
effect and reuse the previous result, preventing unnecessary recompositions.

    Use DisposableEffect to launch a coroutine. Also, use DisposableEffect to ensure that the
coroutine is cancelled and resources used by the coroutine are cleaned up when the Composable is no
longer in use.

    In the cleanup function of the DisposableEffect, we cancel the coroutine using the cancel()
method of the Job instance stored in job.

    The onDispose function is called when the Composable is removed from the UI hierarchy, and it
provides a way to clean up any resources used by the Composable. In this case, we use onDispose to
cancel the coroutine and ensure that any resources used by the coroutine are cleaned up.

    By using DisposableEffect and CoroutineScope together in this way, we ensure that the coroutine
launched by CoroutineScope is cancelled and resources are cleaned up when the Composable is no
longer in use. This prevents leaks and other performance issues, and improves the performance and
stability of our app.

    This function is useful for managing resources that need to be cleaned up when a Composable is
no longer in use, such as event listeners or animations.

    Use Cases of DisposableEffect
        - Adding and removing event listeners
        - Starting and stopping animations
        - Bind and unbinding sensors resources such as Camera, LocationManager, etc
        - Managing database connections
*/