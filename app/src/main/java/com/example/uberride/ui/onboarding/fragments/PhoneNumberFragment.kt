package com.example.uberride.ui.onboarding.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.uberride.R
import com.example.uberride.databinding.FragmentLoginBinding
import com.example.uberride.ui.onboarding.OnboardingViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class PhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    lateinit var pNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance() //Initialised firebase auth

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()

    }



    private fun onClick() {

        val phoneNumber = binding.etPhone
        val sendOtp = binding.btnSendOtp

        //Sends OTP to the entered phone number
        sendOtp.setOnClickListener {


            if (TextUtils.isEmpty(phoneNumber.text.toString())){
                Toast.makeText(requireContext(), "Enter a phone number", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.loader.visibility = View.VISIBLE
                pNumber = phoneNumber.text?.trim().toString()

                if (pNumber.length == 10) {

                    pNumber = "+91$pNumber"

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(pNumber)                         // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)       // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(callbacks)                        // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                }
                else {
                    Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    /** Call backs to handle result of the verify phone number request */

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            /* This callback will be invoked in two situations:
             1 - Instant verification. In some cases the phone number can be instantly
                 verified without needing to send or enter a verification code.
             2 - Auto-retrieval. On some devices Google Play services can automatically
                 detect the incoming verification SMS and perform verification without
                 user action.
             */

            //signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(requireActivity(), "Quota exceeded", Toast.LENGTH_SHORT).show()
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later

            binding.loader.visibility = View.GONE
            Log.d("SENT", verificationId)

            /** Once the code has been successfully sent we will store the otp in the bundle and navigate to the otp screen and pass the bundle*/
            val bundle = Bundle()
            bundle.putString("OTP", verificationId)
            //bundle.putParcelable("RESEND_TOKEN", token)
            bundle.putString("PHONE_NUMBER", binding.etPhone.text.toString())

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment, bundle)

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    //Store the verified phone number in the view model
                    onboardingViewModel.phoneNumber = binding.etPhone.text.toString()

                    findNavController().navigate(R.id.action_registerFragment_to_userDetailsFragment)

                    //val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }












}