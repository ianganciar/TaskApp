package com.example.taskapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.ui.adapter.ViewPagerAdapter
import com.example.taskapp.util.showBottomSheet
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initListeners()
        initTabs()
    }

    private fun initListeners(){
        binding.btnLogout.setOnClickListener {
            showBottomSheet(
                titleButton = R.string.text_button_dialog_confirm ,
                titleDialog = R.string.text_title_dialog_confirm_logout,
                message = getString(R.string.text_message_dialog_confirm_logout),
                onClick = {
                    auth.signOut()
                    findNavController().navigate(R.id.action_homeFragment_to_authentication)
                }
            )
        }
    }

    private fun initTabs(){
        val pageAdpter = ViewPagerAdapter(requireActivity())
        binding.viewpager.adapter = pageAdpter

        pageAdpter.addFragment(TodoFragment(), R.string.status_task_todo)
        pageAdpter.addFragment(DoingFragment(), R.string.status_task_doing)
        pageAdpter.addFragment(DoneFragment(), R.string.status_task_done)

        binding.viewpager.offscreenPageLimit = pageAdpter.itemCount

        TabLayoutMediator(binding.tabs, binding.viewpager){tab,position ->
            tab.text = getString(pageAdpter.getTitle(position))
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}