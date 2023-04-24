package com.vertie.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vertie.DashboardActivity
import com.vertie.databinding.FragmentHomeBinding
import com.vertie.javacode.activities.MeasurementIntroActivity
import com.vertie.javacode.singleton.SingletonClass
import de.kenkou.sdk.headless.core.backend.AnonymousAuthTokenProvider
import de.kenkou.sdk.headless.domain.model.OnboardingQuestionnaireException
import de.kenkou.sdk.visual.KenkouSDKVisual

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var isAllFabsVisible: Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnBiofeedback.visibility = View.GONE
        binding.btnHeartrate.visibility = View.GONE
        binding.addAlarmActionText.visibility = View.GONE
        binding.addPersonActionText.visibility = View.GONE

        isAllFabsVisible = false
        binding.addFab.setOnClickListener(View.OnClickListener {
            (if (!isAllFabsVisible!!) {
                binding.btnHeartrate.show()
                binding.btnBiofeedback.show()
                binding.addAlarmActionText.visibility = View.VISIBLE
                binding.addPersonActionText.visibility = View.VISIBLE
                true
            } else {
                binding.btnHeartrate.hide()
                binding.btnBiofeedback.hide()
                binding.addAlarmActionText.visibility = View.GONE
                binding.addPersonActionText.visibility = View.GONE
                false
            }).also { isAllFabsVisible = it }
        })

        binding.btnBiofeedback.setOnClickListener {
//            Toast.makeText(requireContext(), "Biofeedback", Toast.LENGTH_SHORT).show()
            binding.btnHeartrate.hide()
            binding.btnBiofeedback.hide()
            binding.addAlarmActionText.visibility = View.GONE
            binding.addPersonActionText.visibility = View.GONE
            isAllFabsVisible = false
            SingletonClass.getInstance().mesurmentType="2"
            startFullMeasurement()

        }

        binding.btnHeartrate.setOnClickListener {
//            Toast.makeText(requireContext(), "Heart Rate", Toast.LENGTH_SHORT).show()
            binding.btnHeartrate.hide()
            binding.btnBiofeedback.hide()
            binding.addAlarmActionText.visibility = View.GONE
            binding.addPersonActionText.visibility = View.GONE
            isAllFabsVisible = false
            SingletonClass.getInstance().mesurmentType="1"
            startFullMeasurement()

        }

        val measurement_tutorial: AppCompatButton = binding.measurementTutorial
        measurement_tutorial.setOnClickListener {
            startActivity(Intent(activity, MeasurementIntroActivity::class.java))
        }

        val testTokenProvider = AnonymousAuthTokenProvider.provide()

        KenkouSDKVisual.initialize(
            tokenProvider = testTokenProvider,
            appContext = this.requireActivity(),
            partnerName = "Vertie",
            sdkKey = "Kenkou4Vertie!",
            locale = null,
//            unitSystem = "",
            initializationCallback = { result ->
                result.onSuccess {
                    // SDK ready to work
                    Log.d("a","a")
                }.onFailure { exception ->
                    // Handle exception
                    Log.d("a","a")
                }
            }
        )

        val measurementstartbutton: ConstraintLayout = binding.measurementstartbutton
        measurementstartbutton.setOnClickListener {
            startFullMeasurement()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun startFullMeasurement() {
        try {
//            activity?.let {
            KenkouSDKVisual.presentMeasurement(this.requireActivity())
//            }
        } catch (e: OnboardingQuestionnaireException) {
            showQuestionnaireErrorPopup()
        }
    }

    private fun showQuestionnaireErrorPopup() {
        AlertDialog.Builder(this.requireActivity())
            .setTitle("Measurement Stopped with error")
            .setMessage("Onboarding questionnaire hasn't been completed.")
            .setPositiveButton("Open questionnaire") { dialog, which ->
//                OnboardingQuestionnaireAnswersModel(1994,1,127.00,69.00, System.currentTimeMillis(),12.12)
                KenkouSDKVisual.presentOnboardingQuestionnaire(this.requireActivity())
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

}