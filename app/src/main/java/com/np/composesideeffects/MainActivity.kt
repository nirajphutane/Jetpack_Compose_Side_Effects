package com.np.composesideeffects

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.findNavController
import com.np.composesideeffects.base_pkgs.BaseActivity
import com.np.composesideeffects.base_pkgs.BaseViewModel
import com.np.composesideeffects.core.appLog
import com.np.composesideeffects.ui.theme.ComposeSideEffectsTheme

class MainActivity : BaseActivity() {

    private val screen = "Main Activity"

    private val viewModel: MainActivityViewModel by viewModels()

    @Composable
    override fun View() = ComposeView()

    @Preview @Composable
    override fun Preview() = ComposeView()

    override fun onCreateCall(savedInstanceState: Bundle?) {
        appLog(screen)
    }

    @Composable
    private fun ComposeView() {
        val navigateTo = remember { mutableStateOf<Int?>(null) }

        collectFlowWithLifecycle(viewModel.navigate) { navigateTo.value = it }

        collectFlowWithLifecycle(viewModel.uiState) {
            when (it) {
                is BaseViewModel.UIStates.Loading -> { }

                is BaseViewModel.UIStates.Success -> { }

                is BaseViewModel.UIStates.Error -> { }
            }
        }

        ComposeSideEffectsTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                FragmentContainerView(navigateTo.value)
            }
        }
    }

    @Composable
    fun FragmentContainerView(@IdRes navigateTo: Int? = null) {
        AndroidView(
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.fragment_container_view, null, false)
            },
            update = { view ->
                navigateTo?.let {id -> view.findNavController().navigate(id) }
            }
        )
    }
}
