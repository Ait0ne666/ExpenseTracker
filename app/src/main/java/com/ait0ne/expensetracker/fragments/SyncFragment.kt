package com.ait0ne.expensetracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.databinding.FragmentSyncBinding
import com.ait0ne.expensetracker.ui.viewmodels.SyncViewModel
import com.ait0ne.expensetracker.utils.Resource
import com.google.android.material.snackbar.Snackbar

class SyncFragment: Fragment(R.layout.fragment_sync) {

    lateinit var viewmodel: SyncViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSyncBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_sync,
                container,
                false
            )
        viewmodel = (activity as MainActivity).syncViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewmodel.initialSync()

        viewmodel.syncResult.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Error -> {
                    Snackbar.make(requireView(), "Ошибка синхронизации", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(requireContext().getColor(R.color.error))
                        .setTextColor(requireContext().getColor(R.color.white))
                        .show()
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    findNavController().navigate(SyncFragmentDirections.actionSyncFragment2ToMainFragment())
                }
            }
        }
    }


}