package com.example.uberride.ui.onboarding.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.uberride.R
import com.example.uberride.databinding.FragmentRegisterBinding
import com.example.uberride.ui.onboarding.OnboardingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OtpFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var auth: FirebaseAuth

    lateinit var OTP : String
    lateinit var RESEND_TOKEN: ForceResendingToken
    lateinit var PHONE_NUMBER: String

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBundleData()
        verifyOtp()
        serviceObserver()

        setOtpTextWatcher(binding.etOtp1, binding.etOtp2)
        setOtpTextWatcher(binding.etOtp2, binding.etOtp3)
        setOtpTextWatcher(binding.etOtp3, binding.etOtp4)
        setOtpTextWatcher(binding.etOtp4, binding.etOtp5)
        setOtpTextWatcher(binding.etOtp5, binding.etOtp6)

    }

    /** Service Call */
    private fun checkUserExists() {
        lifecycleScope.launch {
            onboardingViewModel.checkUserExists()
        }
    }

    private fun getBundleData() {

        OTP = arguments?.getString("OTP").toString()
        //RESEND_TOKEN = arguments?.getParcelable("Resend_token")!!
        PHONE_NUMBER = arguments?.getString("PHONE_NUMBER").toString()

        onboardingViewModel.phoneNumber = PHONE_NUMBER
    }

    //Responsible to automatically jup to the next edit text
    private fun setOtpTextWatcher(currentEditText: EditText, nextEditText: EditText) {

        currentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    nextEditText.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }


    //Here we verify the user entered OTP
    private fun verifyOtp() {

        val otp1 = binding.etOtp1
        val otp2 = binding.etOtp2
        val otp3 = binding.etOtp3
        val opt4 = binding.etOtp4
        val otp5 = binding.etOtp5
        val otp6 = binding.etOtp6


        val btnVerify = binding.btnVerify

        btnVerify.setOnClickListener {


            //Getting the user entered OTP
            val enteredOtp = (otp1.text?.trim().toString() + otp2.text?.trim().toString() + otp3.text?.trim().toString() +
                    opt4.text?.trim().toString() + otp5.text?.trim().toString() + otp6.text?.trim().toString())

            if (enteredOtp.isNotEmpty()) {
                if (enteredOtp.length == 6) {
                    binding.loader2.visibility = View.VISIBLE

                    Log.d("OTP", OTP)
                    Log.d("OTP", enteredOtp)

                    //Verify the OTP
                    val credentials: PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP, enteredOtp)

                    //Once credential are verified we pass the credentials to sign in the user
                    signInWithPhoneAuthCredential(credentials)

                }
                else {
                    Toast.makeText(requireActivity(), "Enter the full OTP", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireActivity(), "Enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun serviceObserver() {

        onboardingViewModel.responseCheckUserExists.observe(viewLifecycleOwner, Observer { result ->
            if (result.body()?.user != null) {
                //Toast.makeText(requireActivity(), "User Already Exists.", Toast.LENGTH_SHORT).show()

                //Store user details in view model
                onboardingViewModel.userId = result.body()!!.user.id
                onboardingViewModel.name = result.body()!!.user.name
                onboardingViewModel.phoneNumber = result.body()!!.user.phone

                binding.loader2.visibility = View.GONE

                storeDataToSharedPreference()
                navigateToLandingMain()
            }
            else {
                //Continue with registration flow
                //Toast.makeText(requireActivity(), "User Doesn't Exist.", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_registerFragment_to_userDetailsFragment)
            }

        })
    }


    //Add user details to shared preference
    private fun storeDataToSharedPreference() {
        onboardingViewModel.addDataToSharedPref()

    }

    private fun navigateToLandingMain() {
        findNavController().navigate(R.id.action_registerFragment_to_landingBaseActivity)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(requireActivity(), "Phone Verified", Toast.LENGTH_SHORT).show()

                    //Store verified phone number in view model
                    onboardingViewModel.phoneNumber = PHONE_NUMBER

                    checkUserExists()


                    //val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.loader2.visibility = View.GONE
                        Toast.makeText(requireActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", task.exception.toString())
                    }
                    // Update UI
                }
            }
    }



}