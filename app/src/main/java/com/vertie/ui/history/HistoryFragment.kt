package com.vertie.ui.history

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.vertie.Constants
import com.vertie.R
import com.vertie.activity.QuestionActivity
import com.vertie.data.user.LoginResponse
import com.vertie.databinding.FragmentHistoryBinding
import com.vertie.javacode.apiManager.APIManager
import com.vertie.javacode.utility.Debugger
import com.vertie.javacode.utility.Globals
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class HistoryFragment : Fragment() {

    var TAG:String = "HistoryFragment"
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var medicalRecordData : Array<MedicalRecord>
    lateinit var weeklyGraphDataModel : Array<WeeklyGraphDataModel>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyViewModel.text.observe(viewLifecycleOwner) {

        }

        val preferences = this.activity?.getSharedPreferences(Constants.user, 0)
        val email : String? = preferences?.getString(Constants.user_email,null)
        val token : String? = preferences?.getString(Constants.token,null)

        val button_add_manual : ConstraintLayout = binding.buttonAddManual
        button_add_manual.setOnClickListener {
            activity?.let{
                val intent = Intent (it, QuestionActivity::class.java)
                it.startActivity(intent)
            }
        }

        binding.dayButton.setOnClickListener {
            binding.dayButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.menu_item_background_selected) }
            binding.weekButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.monthButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.dayText.setTextColor(Color.parseColor("#ffffff"))
            binding.weekText.setTextColor(Color.parseColor("#000000"))
            binding.monthText.setTextColor(Color.parseColor("#000000"))
            getHistoryDataByType(0)
        }
        binding.weekButton.setOnClickListener {
            binding.dayButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.weekButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.menu_item_background_selected) }
            binding.monthButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.dayText.setTextColor(Color.parseColor("#000000"))
            binding.weekText.setTextColor(Color.parseColor("#ffffff"))
            binding.monthText.setTextColor(Color.parseColor("#000000"))
            getHistoryDataByType(1)
        }
        binding.monthButton.setOnClickListener {
            binding.dayButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.weekButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.bottom_navigation_background) }
            binding.monthButton.background = activity?.let { it1 -> ContextCompat.getDrawable(it1.applicationContext, R.drawable.menu_item_background_selected) }
            binding.dayText.setTextColor(Color.parseColor("#000000"))
            binding.weekText.setTextColor(Color.parseColor("#000000"))
            binding.monthText.setTextColor(Color.parseColor("#ffffff"))
            getHistoryDataByType(2)
        }
        return root
    }

    fun getHistoryDataByType(type: Int){
        val mapFields = java.util.HashMap<String, Any?>()
                mapFields["ProfileId"] = "740e9faf-a49f-4255-b711-04cc555b1b71"
        mapFields["email"] = "loricmarshall16@gmail.com"
        mapFields["date"] =  LocalDateTime.now().toString()
        mapFields["dateType"] = type
        val call = APIManager
            .getUserManagerService(
                this.activity,
                GsonConverterFactory.create(GsonBuilder().setLenient().create())
            )
            .getMedicalRecordsByDateType(mapFields)
        Globals.showProgressDialog(this.activity)
        call.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                val body = response.body() as LinkedTreeMap<String, String>?
                Debugger.debugD(TAG, response.toString())
                Globals.hideProgressDialog()
                val jsonObject1: JsonObject = Gson().toJsonTree(body).getAsJsonObject()
                medicalRecordData = Gson().fromJson(jsonObject1.get("medicalRecords"), Array<MedicalRecord>::class.java)
                weeklyGraphDataModel = Gson().fromJson(jsonObject1.get("weeklyGraphViewModel"), Array<WeeklyGraphDataModel>::class.java)
                Debugger.debugE(TAG, "API Failed: ")
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                Globals.hideProgressDialog()
                Debugger.debugE(TAG, "API Failed: " + t.localizedMessage)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}