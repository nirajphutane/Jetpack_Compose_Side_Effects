package com.np.composesideeffects

import androidx.lifecycle.viewModelScope
import com.np.composesideeffects.base_pkgs.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel: BaseViewModel() {

    private val _uiState = MutableStateFlow<UIStates<Any?, String>>(UIStates.Loading())
    val uiState = _uiState.asStateFlow()

    private val _navigate = MutableStateFlow<Int?>(null)
    val navigate = _navigate.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = UIStates.Success(null)
            _uiState.value = UIStates.Error("")
            // _navigate.value = R.id.practical_fragment_1
        }
    }
}