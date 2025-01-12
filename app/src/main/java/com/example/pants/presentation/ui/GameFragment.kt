package com.example.pants.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pants.R
import com.example.pants.databinding.FragmentGameBinding
import com.example.pants.utils.extension.collectFlow
import com.example.pants.utils.extension.setColoredText
import com.example.pants.utils.extension.showErrorDialog
import com.example.pants.utils.extension.showToast
import com.example.pants.presentation.viewmodel.SharedGameViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class GameFragment : Fragment() {

    private val viewBinding by lazy { FragmentGameBinding.inflate(layoutInflater) }
    private val viewModel by activityViewModel<SharedGameViewModel>()
    private lateinit var adapter: ColorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            textTitle.setColoredText(getString(R.string.sort_the_pants))
            adapter = ColorListAdapter { colorModel ->
                navigateToPicker(colorModel.name)
            }

            colorsList.adapter = adapter

            collectFlow(viewModel.colorBoard, adapter::submitList)
            collectFlow(viewModel.errorMessage, ::showErrorDialog)

            btnCheck.setOnClickListener {
                checkOrderAndChangeView()
            }
        }
    }

    private fun navigateToPicker(colorName: String) {
        val fragment = ColorPickerFragment().apply {
            arguments = Bundle().apply {
                putString(COLOR_NAME_ARG, colorName)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun checkOrderAndChangeView() {
        with(viewBinding) {
            val colors = viewModel.checkColorOrder(adapter.currentList)
            when {
                colors == null -> {
                    showToast(getString(R.string.success))
                }
                colors.isNotEmpty() -> {
                    adapter.apply {
                        setOriginalList()
                        submitList(colors)
                        setHelpMode(true)
                    }
                    textTitle.setColoredText(getString(R.string.pants_solution))
                    hseGradient.visibility = View.GONE
                    btnCheck.text = getString(R.string.try_again)
                    btnCheck.setOnClickListener {
                        resetNextButton()
                    }
                }
            }
        }
    }

    private fun resetNextButton() {
        with(viewBinding) {
            hseGradient.visibility = View.VISIBLE
            textTitle.setColoredText(getString(R.string.sort_the_pants))
            adapter.apply {
                setHelpMode(false)
                resetToOriginalOrder()
            }
            btnCheck.text = getString(R.string.next)
            btnCheck.setOnClickListener {
                checkOrderAndChangeView()
            }
        }
    }
companion object {
        const val COLOR_NAME_ARG = "COLOR_NAME"
    }
}
