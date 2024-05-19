package com.np.composesideeffects

import com.np.composesideeffects.base_pkgs.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SideEffectsViewModel: BaseViewModel() {

    private val _uiState = MutableStateFlow<UIStates<Nothing?, Nothing?>?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = UIStates.Success(null)
    }
}