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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme
import kotlinx.coroutines.delay

class ProduceState : BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview
    @Composable
    override fun Preview() = ComposeView()

    private val screen = "ProduceState"
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
                val recompose = remember { mutableIntStateOf(1001) }
                val composeAgain = remember { mutableStateOf(false) }
                val showCompose = remember { mutableStateOf(true) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

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
                            Counter(recompose.intValue)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Counter(state: Int) {
        appLog("State: $state")

        val countState = produceState(initialValue = 0) {
            var i = 0
            while (true) {
                delay(1000)
                appLog("Tick: ${i++}")
                value = i
            }
        }

        Text("State: $state")
        Text("Count: ${countState.value}")
    }
}

/*
    The produceState converts non-compose state into compose state. It launches a coroutine scoped
to the composition that can push values into a returned state. The producer is started when
produceState enters the Composition and is stopped when it leaves the Composition. The returned
State combines; setting the same value will not cause a recomposition.

    The producer is launched when produceState enters the Composition, and will be cancelled when it
leaves the Composition. The returned State conflates; setting the same value won't trigger a
recomposition.

    Even though produceState creates a coroutine, it can also be used to observe non-suspending
sources of data. To remove the subscription to that source, use the awaitDispose function.

    Example: to bring external subscription-driven state such as Flow, LiveData, or RxJava into the
Composition.
*/