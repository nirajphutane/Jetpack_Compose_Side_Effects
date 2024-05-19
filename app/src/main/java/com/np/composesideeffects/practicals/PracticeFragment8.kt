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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.HtmlCompat
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme
import kotlinx.coroutines.delay

class PracticeFragment8 : BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview
    @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practical 8: RememberUpdatedState"
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
                var recompose by remember { mutableIntStateOf(1001) }
                var composeAgain by remember { mutableStateOf(false) }
                var showCompose by remember { mutableStateOf(true) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

                    Button(onClick = { recompose += 123 }) {
                        Text("Recompose")
                    }

                    Button(onClick = { composeAgain = !composeAgain }) {
                        Text("Compose Again")
                    }

                    Button(onClick = { showCompose = !showCompose }) {
                        Text((if(showCompose) "Remove" else "Add") + " Compose")
                    }

                    if(showCompose) {
                        key(composeAgain) {
                            var counter by remember { mutableIntStateOf(0) }
                            LaunchedEffect(key1 = Unit) {
                                while (true) {
                                    delay(2000)
                                    counter++
                                }
                            }
                            CounterView(recompose, counter)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CounterView(state: Int, counter: Int) {
        appLog("State: $state")

        val counterState = rememberUpdatedState(counter)
        LaunchedEffect(key1 = Unit) {
            while (true) {
                delay(1000)
                Toast.makeText(context, Html.fromHtml("<font color='#FF0000'>Counter: $counter</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
                delay(1000)
                Toast.makeText(context, Html.fromHtml("<font color='#00FF00'>Counter (with rememberUpdatedState): ${counterState.value}</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
            }
        }

        Text("State: $state")
        Text("Count: $counter")
    }
}

/*
    When you want to reference a value in effect that shouldn’t restart if the value changes then
use rememberUpdatedState. This process is helpful if we have long running option that is expensive
to restart.

    There are situations where you need to access the latest state of a Composable, even when it
changes within a Composable function. This is where rememberUpdatedState comes into play. It ensures
that you can access the most recent state of a composable within its scope, regardless of when that
state is updated. This is particularly useful when working with mutable state that might change
multiple times during composition.
*/