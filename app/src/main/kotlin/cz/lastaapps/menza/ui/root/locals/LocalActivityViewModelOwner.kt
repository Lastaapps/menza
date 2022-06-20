/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.menza.ui.root.locals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.*
import org.kodein.di.compose.localDI
import org.kodein.di.direct
import org.kodein.di.instance

// Stolen from LocalViewModelOwner
object LocalActivityViewModelOwner {
    private val LocalActivityViewModelOwner =
        compositionLocalOf<ViewModelStoreOwner?> { null }

    val current: ViewModelStoreOwner
        @Composable
        get() = LocalActivityViewModelOwner.current
            ?: ViewTreeViewModelStoreOwner.get(LocalView.current)!!

    infix fun provides(viewModelStoreOwner: ViewModelStoreOwner):
            ProvidedValue<ViewModelStoreOwner?> {
        return LocalActivityViewModelOwner.provides(viewModelStoreOwner)
    }
}

@Composable
inline fun <reified VM : ViewModel> rememberActivityViewModel(tag: Any? = null): ViewModelLazy<VM> =
    with(localDI()) {
        val viewModelStoreOwner = LocalActivityViewModelOwner.current

        remember {
            ViewModelLazy(
                viewModelClass = VM::class,
                storeProducer = { viewModelStoreOwner.viewModelStore },
                factoryProducer = {
                    object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return direct.instance<VM>(tag) as T
                        }
                    }
                }
            )
        }
    }
