package com.arduia.expense.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragChooseCurrencyDialogBinding
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.onboarding.ChooseCurrencyViewModel
import com.arduia.expense.ui.onboarding.CurrencyListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseCurrencyDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragChooseCurrencyDialogBinding

    private val viewModel by viewModels<ChooseCurrencyViewModel>()

    private lateinit var adapter: CurrencyListAdapter
    private var loadingHideJob: Job ? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragChooseCurrencyDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "ChooseCurrencyDialog")
    }

    private fun setupView() {
        adapter = CurrencyListAdapter(layoutInflater)
        adapter.setOnItemClickListener(viewModel::selectCurrency)
        binding.searchBox.setOnSearchTextChangeListener(viewModel::searchCurrency)
        with(binding.rvCurrencies) {
            this.adapter = this@ChooseCurrencyDialog.adapter
            itemAnimator = null
            addItemDecoration(
                MarginItemDecoration(
                    spaceSide = resources.getDimension(R.dimen.grid_3).toInt(),
                    spaceHeight = px(4)
                )
            )
        }
        binding.imvDropClose.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener {
            dismiss()
        }
    }

    private fun setupViewModel() {
        viewModel.currencies.observe(viewLifecycleOwner, adapter::submitList)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoadingWithDelay()
            }
        }
    }

    private fun showLoading() {
        loadingHideJob?.cancel()
        binding.pbLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingWithDelay() {
        loadingHideJob = lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            binding.pbLoading.visibility = View.INVISIBLE
        }
    }
}