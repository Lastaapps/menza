/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.root

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.features.starting.ui.screen.PrivacyDialogContent
import cz.lastaapps.menza.features.starting.ui.vm.DownloadViewModel
import cz.lastaapps.menza.features.starting.ui.vm.PrivacyViewModel
import cz.lastaapps.menza.navigation.Dest.R
import cz.lastaapps.menza.ui.dests.info.InfoLayout
import cz.lastaapps.menza.ui.dests.info.InfoViewModel
import cz.lastaapps.menza.ui.dests.settings.SettingsLayout
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.locals.*
import cz.lastaapps.menza.ui.theme.AppTheme

@Composable
fun AppRoot(
    activity: Activity,
    viewModel: RootViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
) {

    val privacyViewModel: PrivacyViewModel = koinActivityViewModel()
    val initViewModel: DownloadViewModel = koinActivityViewModel()
    val menzaViewModel: MenzaViewModel = koinActivityViewModel()
    val settingsViewModel: SettingsViewModel = koinActivityViewModel()

    AppTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            ApplyLocalProviders(
                activity = activity,
                viewModelStoreOwner = viewModelStoreOwner,
            ) {

                //show app if ready
                    AppContent(menzaViewModel, settingsViewModel)
            }
        }
    }
}

@Composable
fun ApplyLocalProviders(
    activity: Activity,
    viewModelStoreOwner: ViewModelStoreOwner,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalActivityViewModelOwner provides viewModelStoreOwner) {
        WithLocalWindowSizes(activity) {
            WithFoldingFeature(activity) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
private fun AppContent(viewModel: MenzaViewModel, settingsViewModel: SettingsViewModel) {

    val menzaId by viewModel.selectedMenza.collectAsState()
    val onMenzaSelected: (MenzaId?) -> Unit = { viewModel.selectMenza(it) }

    val snackbarHostState = remember { SnackbarHostState() }
    val drawableLazyListState = rememberLazyListState()
    val navHostState = rememberAnimatedNavController()

    //drawer should auto open, if there is no menza selected and user hasn't touched the drawer
    val drawerState =
        rememberDrawerState(if (menzaId == null) DrawerValue.Open else DrawerValue.Closed)

    WithSnackbarProvider(snackbarHostState = snackbarHostState) {
//        WithDrawerListStateProvider(drawableLazyListState) {

            ChooseLayout(
                navController = navHostState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = viewModel,
                settingsViewModel = settingsViewModel,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState
            ) {
                AnimatedNavHost(
                    navController = navHostState,
                    startDestination = R.start,
                    modifier = Modifier.fillMaxSize()
                ) {

                    composable(
                        R.today,
                    ) {
                    }
                    composable(R.week) {
                    }
                    composable(R.info) {
                        InfoLayout(
                            navController = navHostState,
                            snackbarHostState = snackbarHostState,
                            menzaId = menzaId,
                            infoViewModel = koinActivityViewModel<InfoViewModel>(),
                        )
                    }
                    composable(R.settings) {
                        SettingsLayout(
                            navController = navHostState,
                            menzaViewModel = viewModel,
                            settingsViewModel = settingsViewModel,
                        )
                    }
                    composable(R.license) {
                    }
                    composable(R.osturak) {
                    }
                    dialog(R.privacyPolicy) {
                        PrivacyDialogContent(showAccept = false, onAccept = {})
                    }
                }
            }
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseLayout(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
//    when (LocalWindowWidth.current) {
//        WindowWidthSizeClass.Compact -> {
//            AppLayoutCompact(
//                navController = navController,
//                menzaId = menzaId,
//                onMenzaSelected = onMenzaSelected,
//                menzaViewModel = menzaViewModel,
//                settingsViewModel = settingsViewModel,
//                snackbarHostState = snackbarHostState,
//                drawerState = drawerState,
//                content = content,
//            )
//        }
//        WindowWidthSizeClass.Medium -> {
//            AppLayoutMedium(
//                navController = navController,
//                menzaId = menzaId,
//                onMenzaSelected = onMenzaSelected,
//                menzaViewModel = menzaViewModel,
//                settingsViewModel = settingsViewModel,
//                snackbarHostState = snackbarHostState,
//                drawerState = drawerState,
//                content = content,
//            )
//        }
//        WindowWidthSizeClass.Expanded -> {
//            AppLayoutExpanded(
//                navController = navController,
//                menzaId = menzaId,
//                onMenzaSelected = onMenzaSelected,
//                menzaViewModel = menzaViewModel,
//                settingsViewModel = settingsViewModel,
//                snackbarHostState = snackbarHostState,
//                drawerState = drawerState,
//                content = content,
//            )
//        }
//    }
}


