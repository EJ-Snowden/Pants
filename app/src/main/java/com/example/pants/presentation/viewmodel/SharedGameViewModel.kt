package com.example.pants.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pants.domain.model.ColorModel
import com.example.pants.domain.usecases.CheckBoardOrderUseCase
import com.example.pants.domain.usecases.GetColorBoardUseCase
import com.example.pants.domain.usecases.SaveColorUseCase
import com.example.pants.domain.usecases.UpdateColorUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedGameViewModel(
    private val getColorBoardUseCase: GetColorBoardUseCase,
    private val checkBoardOrderUseCase: CheckBoardOrderUseCase,
    private val updateColorUseCase: UpdateColorUseCase,
    private val saveColorUseCase: SaveColorUseCase
) : ViewModel() {

    private val _colorBoard = MutableStateFlow(EMPTY_BOARD)
    val colorBoard: StateFlow<List<ColorModel>> = _colorBoard.asStateFlow()

    private val _currentColorName = MutableStateFlow<String?>(null)
    val currentColorName: StateFlow<String?> = _currentColorName.asStateFlow()

    private val _selectedColor = MutableStateFlow(Color.Black)
    val selectedColor: StateFlow<Color> = _selectedColor.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        initColorBoard()
    }

    fun setColorModelByName(name: String) {
        _colorBoard.value.find { it.name == name }?.let { colorModel ->
            _currentColorName.value = colorModel.name
            updateColorSettings(colorModel.guessHue ?: 0f)
        }
    }

    fun saveColor(newHue: Float) {
        viewModelScope.launch {
            val updatedBoard = saveColorUseCase(_colorBoard.value, _currentColorName.value.orEmpty(), newHue)
            _colorBoard.value = updatedBoard
        }
    }


    fun updateColorSettings(hue: Float) {
        _selectedColor.value = Color.hsv(hue, 1f, 1f)
        _colorBoard.value = _colorBoard.value.map { color ->
            if (color.name == _currentColorName.value) {
                updateColorUseCase(color, hue)
            } else {
                color
            }
        }
    }


    fun checkColorOrder(board: List<ColorModel>): List<ColorModel>? {
        when {
            board.isEmpty() -> {
                initColorBoard()
                return board
            }

            checkBoardOrderUseCase(board) -> {
                initColorBoard()
                return null
            }

            else -> {
                return board.sortedBy { it.realHue }
            }
        }
    }

    private fun initColorBoard() {
        viewModelScope.launch {
            getColorBoardUseCase(BOARD_SIZE).fold(
                onSuccess = { _colorBoard.value = it.toPersistentList() },
                onFailure = { _errorMessage.emit(it.message.orEmpty()) }
            )
        }
    }

    private companion object {
        const val BOARD_SIZE = 5
        val EMPTY_BOARD = emptyList<ColorModel>()
    }
}