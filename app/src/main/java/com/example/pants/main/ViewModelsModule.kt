package com.example.pants.main

import com.example.pants.domain.usecases.CheckBoardOrderUseCase
import com.example.pants.domain.usecases.GetColorBoardUseCase
import com.example.pants.domain.usecases.SaveColorUseCase
import com.example.pants.domain.usecases.UpdateColorUseCase
import com.example.pants.presentation.viewmodel.SharedGameViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {

    single { UpdateColorUseCase() }
    single { SaveColorUseCase() }
    single { CheckBoardOrderUseCase() }
    single { GetColorBoardUseCase(get()) }

    viewModelOf(::SharedGameViewModel)
}
