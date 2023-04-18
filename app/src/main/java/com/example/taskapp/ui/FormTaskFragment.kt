package com.example.taskapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskapp.R
import com.example.taskapp.data.model.StatusEnum
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentFormTaskBinding
import com.example.taskapp.util.initToolBar
import com.example.taskapp.util.showBottomSheet

class FormTaskFragment : BaseFragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var status: StatusEnum = StatusEnum.TODO
    private var newTask: Boolean = true

    private val args: FormTaskFragmentArgs by navArgs()

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar(binding.toolbar)

        getArgs()
        initListener()
    }

    private fun initListener() {
        binding.btnSave.setOnClickListener {
            observeViewModel()

            validateData()
        }

        binding.rgStatus.setOnCheckedChangeListener { _, id ->
            status = when (id) {
                R.id.rbTodo -> StatusEnum.TODO
                R.id.rbDoing -> StatusEnum.DOING
                else -> StatusEnum.DONE
            }
        }

    }

    private fun getArgs() {
        args.task.let {
            if (it != null) {
                this.task = it

                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false
        status = task.status
        binding.textToolbar.setText(R.string.text_toolbar_update_form_task_fragment)

        binding.editDescription.setText(task.description)

        setStatus()
    }

    private fun setStatus() {
        binding.rgStatus.check(
            when (task.status) {
                StatusEnum.TODO -> R.id.rbTodo
                StatusEnum.DOING -> R.id.rbDoing
                else -> R.id.rbDone
            }
        )
    }

    private fun validateData() {
        val description = binding.editDescription.text.toString().trim()

        if (description.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newTask) task = Task()
            task.description = description
            task.status = status

            if (newTask) {
                viewModel.insertTask(task)
            } else {
                viewModel.updateTask(task)
            }


        } else {
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }

    private fun observeViewModel() {
        viewModel.taskInsert.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                R.string.text_save_success_form_task_fragment,
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack()
        }

        viewModel.taskUpdate.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                R.string.text_update_success_form_task_fragment,
                Toast.LENGTH_SHORT
            ).show()

            binding.progressBar.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}