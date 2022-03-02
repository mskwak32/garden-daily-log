package com.mskwak.presentation.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFragment
import com.mskwak.presentation.custom_component.ListItemDecoration
import com.mskwak.presentation.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutRes: Int = R.layout.fragment_home
    private val viewModel by viewModels<HomeViewModel>()
    private val adapter: PlantListAdapter by lazy { PlantListAdapter(viewModel) }

    override fun initDataBinding() {
        binding?.viewModel = viewModel
        binding?.adapter = adapter
        binding?.addPlantButton?.setOnClickListener {
            navigateAddPlant()
        }
        binding?.plantListView?.addItemDecoration(ListItemDecoration(20))
    }

    override fun initialize() {
        initToolbar()
        initObserver()
    }

    private fun initToolbar() {
        binding?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_addPlant -> {
                    navigateAddPlant()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObserver() {
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
            viewModel.isEmptyList.value = plants.isNullOrEmpty()
        }
        viewModel.openPlantEvent.observe(viewLifecycleOwner) {
            openPlantDetail(it)
        }
    }

    private fun navigateAddPlant() {
        val action = HomeFragmentDirections.actionHomeFragmentDestToEditPlantFragmentDest(null)
        findNavController().navigate(action)
    }

    private fun openPlantDetail(plantId: Int) {

    }
}