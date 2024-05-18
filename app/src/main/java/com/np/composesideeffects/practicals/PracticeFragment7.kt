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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

class PracticeFragment7: BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practical 7: RememberCoroutineScope"
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
                            Counter(recompose)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Counter(state: Int) {
        appLog("State: $state")

        var count by remember { mutableIntStateOf(0) }
        var isEnabled by remember { mutableStateOf(true) }

        val coroutineScope = rememberCoroutineScope()

        Text("State: $state")
        Button(
            onClick = {
                isEnabled = false
                coroutineScope.launch {
                    count = 0
                    try {
                        while (count < 5) {
                            delay(1000)
                            count++
                            appLog("Tick: $count")
                        }
                        Toast.makeText(context, Html.fromHtml("<font color='#00FF00'>Counter finished!!</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
                    } catch(e: Exception) {
                        appLog("Error: ${e.message}")
                        Toast.makeText(context, Html.fromHtml("<font color='#FF0000'>Error: ${e.message}</font>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT).show()
                    }
                    isEnabled = true
                }
            },
            enabled = isEnabled
        ) {
            Text("Start 5sec Counter")
        }
        Text("Count: $count")
    }
}

/*

    To launch a coroutine outside of a composable, but scoped so that it will be automatically
canceled once it leaves the composition, use rememberCoroutineScope. Also use rememberCoroutineScope
whenever you need to control the lifecycle of one or more coroutines manually.

    The rememberCoroutineScope is a composable function that returns a CoroutineScope bound to the
point of the Composition where it's called. The scope will be cancelled when the call leaves the
Composition. It can be use to obtain a composition-aware scope to launch coroutine outside
composable.

    The rememberCoroutineScope is used to create a coroutine scope that is tied to the Composable
function's lifecycle. This lets you manage coroutines efficiently and safely by ensuring they are
cancelled when the Composable function is removed from the composition. You can use the launch
function within the scope to easily and safely manage asynchronous operations.

    By using rememberCoroutineScope(), do finite jobs in this coroutine scope. Do not write infinite
job in this coroutine scope.

    Also, this coroutine scope will launch in the callback when it call, and if callback is called
multiple times then this coroutine will launch multiple times and will run parallel. So take care of
this behaviour.

    Use this scope for launching another independent coroutine, calling suspend function and for
performing UI operations such as to show Toast or Snackbar, open / close navigation drawer, for
route / navigation purpose etc.

    Use cases of rememberCoroutineScope:
        Network Requests / Database Operations
        Animation
        State Management
        Dialogs, Snackbar and Toasts
        Navigation
*/
