package com.vertie

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import de.kenkou.sdk.headless.core.backend.AnonymousAuthTokenProvider
import de.kenkou.sdk.headless.domain.model.OnboardingQuestionnaireException
import de.kenkou.sdk.headless.domain.model.anamnesis.obqa.OnboardingQuestionnaireAnswersModel
import de.kenkou.sdk.visual.KenkouSDKVisual
import de.kenkou.sdk.visual.activityresults.KenkouActivityResult
import de.kenkou.sdk.visual.activityresults.KenkouResultsMapper
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val testTokenProvider = AnonymousAuthTokenProvider.provide()
//        KenkouSDKVisual.initialize(
//            testTokenProvider,
//            applicationContext,
//            partnerName = "Vertie",
//            sdkKey = "Kenkou4Vertie!"
//        )

        KenkouSDKVisual.initialize(
            tokenProvider = testTokenProvider,
            appContext = applicationContext,
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

//        KenkouSDKVisual.presentMeasurementInstructions(this)

        findViewById<Button>(R.id.btStart).setOnClickListener {
            Log.d("a","a")
//            KenkouSDKVisual.presentMeasurementInstructions(this)
            startFullMeasurement()
        }

    }

    private fun startFullMeasurement() {
        try {
//            activity?.let {
            KenkouSDKVisual.presentMeasurement(this)
//            }
        } catch (e: OnboardingQuestionnaireException) {
            showQuestionnaireErrorPopup()
        }
    }

    private fun showQuestionnaireErrorPopup() {
        AlertDialog.Builder(this)
            .setTitle("Measurement Stopped with error")
            .setMessage("Onboarding questionnaire hasn't been completed.")
            .setPositiveButton("Open questionnaire") { dialog, which ->
//                OnboardingQuestionnaireAnswersModel(1994,1,127.00,69.00, System.currentTimeMillis(),12.12)
                KenkouSDKVisual.presentOnboardingQuestionnaire(this)
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            when (val activityResult = KenkouResultsMapper.onActivityResult(requestCode, resultCode, data)) {
                is KenkouActivityResult.Instructions -> {
                    Log.d("a","a")
//                    KenkouSDKVisual.presentLastBiofeedbackResults(this)
                }
                is KenkouActivityResult.Measurement -> {
                    //measurement finished
                    KenkouSDKVisual.presentPostMeasurementQuestionnaire(this@MainActivity, activityResult.measurement)
                }
                is KenkouActivityResult.PostMeasurementQuestionnaire -> {
                    KenkouSDKVisual.presentBiofeedbackIntroduction(this@MainActivity)
                }
                is KenkouActivityResult.Results -> {
                    Log.d("a","a")
//                    KenkouSDKVisual.presentLastBiofeedbackResults(this)
                }
                is KenkouActivityResult.OnboardingQuestionnaireAnswers -> {
                    OnboardingQuestionnaireAnswersModel(1994,1,127.00,69.00, System.currentTimeMillis(),12.12)
//                    KenkouSDKVisual.presentLastBiofeedbackResults(this)
                    Log.d("a","a")
                }
                is KenkouActivityResult.BiofeedbackIntroduction ->{
                    KenkouSDKVisual.presentBiofeedbackBaseline(
                        this@MainActivity,true,true,true,null)
                }
                is KenkouActivityResult.BiofeedbackExercise ->{
                    KenkouSDKVisual.presentMeasurementInstructions(this@MainActivity)
                }
                is KenkouActivityResult.BiofeedbackBaseline ->{
                    activityResult.baseline?.let {
                        KenkouSDKVisual.presentBiofeedbackExercise(this@MainActivity,
                            it,true,true,true)
                    }
                }
                else -> {
                    Log.d("a","a")
                }
            }
        }
    }

}