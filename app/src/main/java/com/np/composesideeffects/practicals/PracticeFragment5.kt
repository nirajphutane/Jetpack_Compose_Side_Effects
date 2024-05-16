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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

class PracticeFragment5 : BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview
    @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practical 5: DerivedStateOf"
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

        var count by remember { mutableIntStateOf(1) }
        val isCountInMultipleOf5 by remember {
            derivedStateOf {
                val message = "Count In DerivedState: $count"
                appLog(message)
                showToast(message)
                count % 5 == 0
            }
        }

        val message = "Is count in multiple of 5? ${if (isCountInMultipleOf5) "Yes" else "No"}"
        appLog(message)
        showToast(message, 1000, color = "00FF00")

        Text("State: $state")
        Button(
            onClick = { count++ }
        ) {
            Text("$count++")
        }
        Text(message)
    }

    private fun showToast(text: String, duration: Long = 0, color: String = "FF0000") = lifecycleScope.launch {
        delay(duration)
        Toast.makeText(context, Html.fromHtml("<font color='#$color'>$text</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
    }
}

/*
    The derivedStateOf is a composable that can be used to derive new state based on the values of
other state variables. It is useful when you need to compute a value that depends on other values,
and you want to avoid recomputing the value unnecessarily.

    In Compose, recomposition occurs each time an observed state object or composable input changes.
A state object or input may be changing more often than the UI actually needs to update, leading to
unnecessary recomposition.

    You should use the derivedStateOf function when your inputs to a composable are changing more
often than you need to recompose. This often occurs when something is frequently changing, such as a
scroll position, but the composable only needs to react to it once it crosses a certain threshold.
The derivedStateOf creates a new Compose state object you can observe that only updates as much as
you need. In this way, it acts similarly to the Kotlin Flows distinctUntilChanged()/

    The derivedStateOf is like distinctUntilChanged. So the  derivedStateOf {} should be used when
your state or key is changing more than you want to update your UI.

    Always remember that there needs to be a difference in the amount of change between the input
arguments and output result for derivedStateOf to make sense.

    Some examples of when it could be used (not exhaustive):
        1. Observing if scrolling passes a threshold (scrollPosition > 0)
        2. Items in a list is greater than a threshold (items > 0)
        3. Form validation as above (username.isValid())

    Caution: derivedStateOf is expensive, and you should only use it to avoid unnecessary
recomposition when a result hasn't changed.
*/