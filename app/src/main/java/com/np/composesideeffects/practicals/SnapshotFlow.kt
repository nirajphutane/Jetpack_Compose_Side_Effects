package com.np.composesideeffects.practicals

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SnapshotFlow : BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview
    @Composable
    override fun Preview() = ComposeView()

    private val screen = "Snapshot Flow"
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
                var launchedEffectKey by remember { mutableStateOf(true) }
                var recompose by remember { mutableIntStateOf(1001) }
                var composeAgain by remember { mutableStateOf(false) }
                var showCompose by remember { mutableStateOf(true) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

                    Button(onClick = { launchedEffectKey = !launchedEffectKey }) {
                        Text("Update LaunchedEffect Key")
                    }

                    Button(onClick = { recompose += 123 }) {
                        Text("Recompose")
                    }

                    Button(onClick = { composeAgain = !composeAgain }) {
                        Text("Compose Again")
                    }

                    Button(onClick = { showCompose = !showCompose }) {
                        Text((if (showCompose) "Remove" else "Add") + " Compose")
                    }

                    if (showCompose) {
                        key(composeAgain) {
                            Counter(recompose, launchedEffectKey)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Counter(state: Int, key: Boolean) {
        appLog("State: $state")

        var count by remember { mutableIntStateOf(1) }

        LaunchedEffect(key) {
            snapshotFlow { count }
                .map {
                    "Flow: $it"
                }
                .collect {
                    appLog(it)
                    showToast(it)
                }
        }

        Text("State: $state")
        Button(
            onClick = {
                count++

            }
        ) {
            Text("$count++")
        }
    }

    private fun showToast(text: String, duration: Long = 0, color: String = "FF0000") = lifecycleScope.launch {
        delay(duration)
        Toast.makeText(context, Html.fromHtml("<font color='#$color'>$text</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
    }
}

/*
    Use snapshotFlow to convert State<T> objects into a cold Flow. The snapshotFlow runs its block
when collected and emits the result of the State objects read in it. When one of the State objects
read inside the snapshotFlow block mutates, the Flow will emit the new value to its collector if the
new value is not equal to the previous emitted value (this behavior is similar to that of
Flow.distinctUntilChanged).

    The snapshotFlow is a function that allows you to create a flow that emits the current value of
a state object, and then emits any subsequent changes to that object. This can be useful for
creating reactive UIs that respond to changes in state, without having to manually manage callbacks
or listeners.

    LaunchedEffect is used to collect from the countFlow flow, ensuring that the collection only
occurs when the component is active and stops when it's removed.
*/