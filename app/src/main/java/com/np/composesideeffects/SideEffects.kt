package com.np.composesideeffects

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme

class SideEffects: BaseFragment() {

    private val viewModel: SideEffectsViewModel by viewModels()

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    private val screen = "Effect-Handlers and Side-Effect States"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appLog(screen)

        collectFlowWithLifecycle(viewModel.uiState) { }
    }

    @Composable
    private fun ComposeView() {
        ComposeSideEffectsTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {

                    Text("Suspended Effect Handler")

                    Button(
                        onClick = {
                            navigateTo(R.id.launched_effect)
                        }
                    ) {
                        Text("Launched Effect")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.remember_coroutine_scope)
                        }
                    ) {
                        Text("Remember Coroutine Scope")
                    }

                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    Text("Non Suspended Effect Handler")

                    Button(
                        onClick = {
                            navigateTo(R.id.side_effect)
                        }
                    ) {
                        Text("Side Effect")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.disposable_effect)
                        }
                    ) {
                        Text("Disposable Effect")
                    }

                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    Text("Side-Effect States")

                    Button(
                        onClick = {
                            navigateTo(R.id.remember_updated_state)
                        }
                    ) {
                        Text("Remember Updated State")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.produce_state)
                        }
                    ) {
                        Text("Produce State")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.derived_state_of)
                        }
                    ) {
                        Text("Derived State Of")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.snapshot_flow)
                        }
                    ) {
                        Text("Snapshot Flow")
                    }
                }
            }
        }
    }
}

/*
    A side-effect is a change to the state of the app that happens outside the scope of a composable
function. Due to composables' lifecycle and properties such as unpredictable recompositions,
executing recompositions of composables in different orders, or recompositions that can be
discarded, composables should ideally be side-effect free.

    However, sometimes side-effects are necessary, for example, to trigger a one-off event such as
showing a snackbar or navigate to another screen given a certain state condition. These actions
should be called from a controlled environment that is aware of the lifecycle of the composable.

    To handle these Side Effects we have various Effect-Handlers and Side-Effect States.

        There are two types of Effect-Handlers
            1. Suspended effect handler
                i. LaunchedEffect
               ii. rememberCoroutineScope
            2. Non-suspended effect handler
                i. DisposableEffect
               ii. SideEffect

        There are four types of Side-Effect States
            1. rememberUpdateState
            2. produceState
            3. derivedStateOf
            4. snapShotFlow
*/