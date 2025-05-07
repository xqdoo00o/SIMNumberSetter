package com.kieronquinn.app.simnumbersetter

import android.app.Application
import com.kieronquinn.app.simnumbersetter.repositories.*
import com.kieronquinn.app.simnumbersetter.ui.screens.main.MainViewModel
import com.kieronquinn.app.simnumbersetter.ui.screens.main.MainViewModelImpl
import org.koin.android.ext.koin.androidContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application: Application() {

    private val repositories = module {
        single<PermissionRepository> { PermissionRepositoryImpl(get(), get()) }
        single<RootRepository> { RootRepositoryImpl() }
        single<ServiceRepository> { ServiceRepositoryImpl(get()) }
    }

    private val viewModels = module {
        single<MainViewModel> { MainViewModelImpl(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(repositories, viewModels)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            private var isFirstResume = true
            override fun onResume(owner: LifecycleOwner) {
                if (isFirstResume) {
                    isFirstResume = false
                } else {
                    val viewModel: MainViewModel by inject()
                    viewModel.onReload()
                }
            }
        })
    }

}