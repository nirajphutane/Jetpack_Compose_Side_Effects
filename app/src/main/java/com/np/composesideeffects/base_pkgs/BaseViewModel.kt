package com.np.composesideeffects.base_pkgs

import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {

    sealed class UIStates<out T, out E> {

        class Success<T>(val value: T): UIStates<T, Nothing>()

        class Loading<T> : UIStates<T, Nothing>()

        class Error<E>(val error: E) : UIStates<Nothing, E>()
    }
}