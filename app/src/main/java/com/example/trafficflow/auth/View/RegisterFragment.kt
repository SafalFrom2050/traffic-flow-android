package com.example.trafficflow.auth.View

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import com.example.trafficflow.R
import com.example.trafficflow.Utils
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.auth.ViewModel.RegisterViewModel
import com.example.trafficflow.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment(R.layout.fragment_register) {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.chipLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_login)
        }

        //Order Matters
        setRegisterButtonBehaviour()
        setUpValidations()
        //

        setErrorMessages()
    }

    private fun setRegisterButtonBehaviour() {
        viewModel.userRepository.isLoadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            binding.btnRegister.isEnabled = !isLoading
            if (isLoading) {
                binding.btnRegister.text = "Registering you..."
            } else {
                binding.btnRegister.text = "Register"
            }
        }

        binding.btnRegister.setOnClickListener {
            viewModel.register(requireContext())
            Utils.hideSoftKeyBoard(requireContext(), it )
        }
    }

    private fun setUpValidations() {

        binding.fieldFirstName.doOnTextChanged { text, _, _, _ ->
            viewModel.fnameLiveData.value = text.toString()
        }

        binding.fieldLastName.doOnTextChanged { text, _, _, _ ->
            viewModel.lnameLiveData.value = text.toString()
        }

        binding.fieldPhone.doOnTextChanged { text, _, _, _ ->
            viewModel.phoneLiveData.value = text.toString()
        }

        binding.fieldEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.emailLiveData.value = text.toString()
        }

        binding.fieldPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.passwordLiveData.value = text.toString()
        }

        viewModel.isValidLiveData.observe(viewLifecycleOwner) {
            binding.btnRegister.isEnabled = it
        }
    }

    private fun setErrorMessages() {
        viewModel.userRepository.authResponseLiveData.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse.accessToken == null) {
                Snackbar.make(requireView(),
                    authResponse.message!!.capitalize(), Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.error)).show()
            }

            if (authResponse.errors != null){
                if (authResponse.errors.fName != null) {
                    binding.fieldFirstName.error = authResponse.errors.fName[0]
                }
                if (authResponse.errors.lName != null) {
                    binding.fieldLastName.error = authResponse.errors.lName[0]
                }
                if (authResponse.errors.phone != null) {
                    binding.fieldPhone.error = authResponse.errors.phone[0]
                }
                if (authResponse.errors.email != null) {
                    binding.fieldEmail.error = authResponse.errors.email[0]
                }
                if (authResponse.errors.password != null) {
                    binding.fieldPassword.error = authResponse.errors.password[0]
                }
            }
        }
    }
}