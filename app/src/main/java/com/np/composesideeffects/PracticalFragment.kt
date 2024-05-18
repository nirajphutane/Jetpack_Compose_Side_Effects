package com.np.composesideeffects

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.viewModels
import com.np.composesideeffects.base_pkgs.BaseFragment
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme

class PracticalFragment: BaseFragment() {

    private val viewModel: PracticeFragmentViewModel by viewModels()

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    private val screen = "Practice Fragment"

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
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_1)
                        }
                    ) {
                        Text("Side Effect")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_2)
                        }
                    ) {
                        Text("Launched Effect")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_3)
                        }
                    ) {
                        Text("Disposable Effect")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_4)
                        }
                    ) {
                        Text("Produce State")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_5)
                        }
                    ) {
                        Text("Derived State Of")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_6)
                        }
                    ) {
                        Text("Snapshot Flow")
                    }

                    Button(
                        onClick = {
                            navigateTo(R.id.practical_fragment_7)
                        }
                    ) {
                        Text("Remember Coroutine Scope")
                    }
                }
            }
        }
    }
}