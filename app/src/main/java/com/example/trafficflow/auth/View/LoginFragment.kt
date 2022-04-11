package com.example.trafficflow.auth.View

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import com.example.trafficflow.MainActivity
import com.example.trafficflow.R
import com.example.trafficflow.Utils
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.auth.ViewModel.LoginViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"

    companion object {
        fun newInstance() = LoginFragment()
    }

    lateinit var viewModel: LoginViewModel

    lateinit var btnLogin: Button

    lateinit var emailField: TextInputEditText
    lateinit var passwordField: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        initViews()


        // Order matters
        setLoginButtonBehaviour()
        setUpValidations()
        setErrorMessages()
        //

        setUpNavigation()
    }

    private fun setUpNavigation() {
        val chipRegister = view?.findViewById<Chip>(R.id.chipRegister)
        chipRegister?.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_to_register)
        }
    }

    private fun initViews() {
        btnLogin = view?.findViewById<Button>(R.id.btnLogin)!!
        emailField = view?.findViewById(R.id.fieldEmail)!!
        passwordField = view?.findViewById(R.id.fieldPassword)!!
    }

    private fun setUpValidations() {

        emailField.doOnTextChanged { text, _, _, _ ->
            viewModel.emailLiveData.value = text?.toString()
        }

        viewModel.userRepository.authResponseLiveData.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse.accessToken != null) {
                val i = Intent(context, MainActivity::class.java)
                startActivity(i)
                requireActivity().finish()
            }
        }

        passwordField.doOnTextChanged { text, _, _, _ ->
            viewModel.passwordLiveData.value = text?.toString()
        }

        viewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
            btnLogin.isEnabled = isValid
        }
    }

    private fun setLoginButtonBehaviour() {
        viewModel.userRepository.isLoadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            btnLogin.isEnabled = !isLoading
            if (isLoading) {
                btnLogin.text = "Logging in..."
            } else {
                btnLogin.text = "LOGIN"
            }
        }

        btnLogin.setOnClickListener {
            viewModel.logIn(requireContext())
            Utils.hideSoftKeyBoard(requireContext(), it )
        }
    }

    private fun setErrorMessages() {
        viewModel.userRepository.authResponseLiveData.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse.accessToken == null) {
                Snackbar.make(requireView(),
                    authResponse.message!!.capitalize(), Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.error)).show()
            }

            if (authResponse.errors != null){
                if (authResponse.errors.email != null) {
                    emailField.error = authResponse.errors.email[0]
                }
                if (authResponse.errors.password != null) {
                    passwordField.error = authResponse.errors.password[0]
                }
            }
        }
    }

}