package com.ait0ne.expensetracker.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.databinding.FragmentAuthBinding
import com.ait0ne.expensetracker.ui.viewmodels.AuthViewModel
import com.ait0ne.expensetracker.ui.viewmodels.FormFields
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthFragment:Fragment(R.layout.fragment_auth) {
    lateinit var viewmodel: AuthViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentAuthBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_auth,
                container,
                false
            )
        viewmodel = (activity as MainActivity).authViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()



        viewmodel.loginError.observe(viewLifecycleOwner) { error ->

            val container = view.findViewById<TextInputLayout>(R.id.etLoginContainer)



            if (error == null) {
                container.isErrorEnabled = false
            } else {
                container.error = getString(error)
            }
        }



        viewmodel.passwordError.observe(viewLifecycleOwner) { error ->

            val container = view.findViewById<TextInputLayout>(R.id.etPasswordContainer)



            if (error == null) {
                container.isErrorEnabled = false
            } else {
                container.error = getString(error)
            }
        }



        viewmodel.loading.observe(viewLifecycleOwner) {
            toggleLoader(it)
        }
    }




    fun setupView() {
        viewmodel.onError { error ->

            Snackbar.make(requireView(), getString(error), Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.error))
                .setTextColor(requireContext().getColor(R.color.white))

                .show()
        }

        viewmodel.onSuccess { success ->

            val prefs = requireContext().getSharedPreferences(
                "com.ait0ne.expensetracker", Context.MODE_PRIVATE
            )


            prefs.edit().putString("jwt", success).apply()


            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToMainFragment())


        }

        val login = view?.findViewById<TextInputEditText>(R.id.etLogin)

        login?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewmodel.clearErrors("login")
            }
        });


        val password = view?.findViewById<TextInputEditText>(R.id.etPassword)

        password?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewmodel.clearErrors("login")
            }
        });



        viewmodel.onButtonClick {
            hideKeyboardFrom(requireContext(), requireView())
        }

    }


    private fun toggleLoader(value: Boolean) {
        val loader = view?.findViewById<ProgressBar>(R.id.loginLoader)

        loader?.let {
            it.apply {
                visibility = if (value) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            }
        }
    }


    fun hideKeyboardFrom(context: Context, view: View) {

        val focus = view.findFocus()
        focus.clearFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}