package com.vertie.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.vertie.Constants
import com.vertie.LoginActivity
import com.vertie.databinding.FragmentProfileBinding
import com.vertie.javacode.apiManager.APIManager
import com.vertie.javacode.utility.Debugger
import com.vertie.javacode.utility.Globals
import com.vertie.utils.bindingUtils.DataFormate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    var TAG:String = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
        profileViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
        }

        binding.btnLogout.setOnClickListener {
            activity?.let{

                val builder = AlertDialog.Builder(activity)
                builder.setTitle("")
                builder.setMessage("Are you sure you want to log out?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    val settings = context?.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
                    if (settings != null) {
                        settings?.edit()?.clear()?.commit()
                    }
                    val intent = Intent (it, LoginActivity::class.java)
                    it.startActivity(intent)
                    activity?.finish()
                }
                builder.setNegativeButton("No") { dialog, which ->
                }
                builder.show()
            }
        }
        val preferences = this.activity?.getSharedPreferences(Constants.user, 0)
        val email : String? = preferences?.getString(Constants.user_email,null)
        val token : String? = preferences?.getString(Constants.token,null)
        preferences?.getString(Constants.user_id,null)?.let { getUserProfileData(it) }
        return root
    }

    fun getUserProfileData(userId: String){
        val call = APIManager
            .getUserManagerService(
                this.activity,
                GsonConverterFactory.create(GsonBuilder().setLenient().create())
            )
            .getUserById2(userId)
        Globals.showProgressDialog(this.activity)
        call.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                val body = response.body() as LinkedTreeMap<String, String>?
                Debugger.debugD(TAG, response.toString())
                Globals.hideProgressDialog()
                binding.tvName.text = body?.get("firstName") + " "+body?.get("lastName")
                binding.tvEm.text = body?.get("email")
                binding.tvAddr.text = body?.get("streetAddress1") + " " +body?.get("streetAddress2")
                binding.tvMob.text = body?.get("primaryPhone")
                binding.tvCountryy.text = body?.get("country")
                binding.tvGenderr.text = body?.get("gender")
                binding.tvUName.text = body?.get("firstName")?.get(0).toString() +" "+body?.get("lastName")?.get(0)
                binding.tvDOB.text = DataFormate.DataFormateInDDMMYYYY(body?.get("dob").toString())
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