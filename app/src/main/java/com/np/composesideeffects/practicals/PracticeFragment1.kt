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
import androidx.compose.runtime.SideEffect
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

class PracticeFragment1: BaseFragment() {

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practical 1: SideEffect"
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
                val recompose = remember { mutableStateOf(false) }
                val state = remember { mutableIntStateOf(1001) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(localScreen.current)

                    Button(onClick = { state.intValue += 123 }) {
                        Text("Update State")
                    }

                    Button(onClick = { recompose.value = !recompose.value }) {
                        Text("Refresh")
                    }

                    key(recompose.value) {
                        val str = getValue(state.intValue)
                        appLog("3. Statement: $str")
                        Text(str)
                        Counter(state.intValue)
                        InitializeValue(state.intValue)
                    }
                }
            }
        }
    }

    @Composable
    fun Counter(state: Int) {
        appLog("State: $state")

        val count = remember { mutableIntStateOf(0) }

        SideEffect { appLog("1. Statement-1") }
        appLog("2. Statement-2")

        Column {

            SideEffect { appLog("3. Statement-3") }
            appLog("4. Statement-4")

            Button(
                onClick = { count.intValue++ }
            ) {

                SideEffect { appLog("5. Statement-5") }
                appLog("6. Statement-6")

                Text("${count.intValue}++")
            }
        }
    }

    @Composable
    fun getValue(state: Int): String {
        appLog("State: $state")

        val str = "Text"
        SideEffect { appLog("1. Statement: $str") }
        appLog("2. Statement: $str")
        return str
    }

    @Composable
    fun InitializeValue(state: Int) {
        appLog("State: $state")

        val isInitialized = remember { mutableStateOf(false) }
        appLog("1. Is Initialized: ${isInitialized.value}")

        SideEffect {
            if (!isInitialized.value) {
                // Execute one-time initialization tasks here
                // initializeBluetooth()
                // loadDataFromFile()
                // initializeLibrary()
                isInitialized.value = true
            }
            appLog("2. Is Initialized: ${isInitialized.value}")
        }

        Text("Is Initialized: ${isInitialized.value}")
    }

}

/*
    SideEffect is a Composable function that allows us to execute a side effect when its parent
Composable is recomposed. A side effect is an operation that does not affect the UI directly,
such as logging, analytics, or updating the external state.
This function is useful for executing operations that do not depend on the Composable’s state
or props.

    When a Composable is recomposed, all the code inside the Composable function is executed
again, including any side effects. However, the UI will only be updated with the changes that
have been made to the state or props of the Composable.

    SideEffect block will be execute only after successful composition or every recomposition of the
compose function and the statement inside that block will executed at last in the compose function
even after the return statement.

    For example, Dialog uses SideEffect to communicated dialog properties, layout-direction and the
dismiss callback from the Dialog parameters to the Android dialog created as a result of composition
. This presents the illusion that Dialog is natively part of Compose even though it uses the view
system to create the dialog.

    Use Cases of SideEffect:
        - Logging and Analytics
        - Performing One-Time Initialization such as setting up a connection to a Bluetooth device,
          loading data from a file, or initializing a library.
*/
