package com.vertie.ui.proxyuser

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.vertie.Constants
import com.vertie.R
import com.vertie.data.user.Userr
import com.vertie.databinding.FragmentProxyuserBinding
import com.vertie.javacode.adapters.ProxyUserAdapter
import com.vertie.javacode.adapters.ProxyUserAdapter.OnClickListner
import com.vertie.javacode.apiManager.APIManager
import com.vertie.javacode.models.ProxyUserModel
import com.vertie.javacode.singleton.SingletonClass
import com.vertie.javacode.utility.Debugger
import com.vertie.javacode.utility.Globals
import com.vertie.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class ProxyuserFragment : Fragment(), OnClickListner {

    var TAG: String = "ProxyuserFragment"
    private var _binding: FragmentProxyuserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val proxyuserViewModel =
            ViewModelProvider(this).get(ProxyuserViewModel::class.java)

        _binding = FragmentProxyuserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        proxyuserViewModel.text.observe(viewLifecycleOwner) {

        }

        val userList: String? =
            this.activity?.getSharedPreferences(Constants.userList, 0)?.getString(
                Constants.proxyUserList, null
            )

        val arrU: MutableList<Userr> = ArrayList()
        val gson = Gson()

        val type = object : TypeToken<List<Userr?>?>() {}.type

        if (userList.equals("[]")) {
            binding.tvNoProxyUser.setVisibility(View.VISIBLE)
        } else {
            if (userList == "null") {
                binding.tvNoProxyUser.setVisibility(View.VISIBLE)
            } else {
                binding.tvNoProxyUser.setVisibility(View.GONE)
                val contactList = gson.fromJson<ArrayList<Userr>>(userList, type)

                for (contact in contactList) {
                    arrU.add(contact)
                }
                val proxyUserModelArrayList = ArrayList<ProxyUserModel>()
                for (item in arrU) {
                    proxyUserModelArrayList.add(
                        ProxyUserModel(
                            item.id,
                            item.firstName + item.lastName,
                            R.drawable.user_icon,
                            item.id,
                            item.streetAddress1
                        )
                    )
                }

                val adapter = this.activity?.let { ProxyUserAdapter(it, proxyUserModelArrayList) }
                binding.idGVcourses.setAdapter(adapter)
                adapter?.setOnClickListner(this)

            }
        }

        return root
    }


    fun getProxiUserd(type: Int) {
        val mapFields = HashMap<String, String>()
//        mapFields["EmailorVIN"] = email.value!!
//        mapFields["Password"] = password.value!!
        val call = APIManager
            .getUserManagerService(
                this.activity,
                GsonConverterFactory.create(GsonBuilder().setLenient().create())
            )
            .login(mapFields)
        Globals.showProgressDialog(this.activity)
        call.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                val body = response.body() as LinkedTreeMap<String, String>?
                Debugger.debugD(TAG, response.toString())
                Globals.hideProgressDialog()
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

    override fun onClickEvent(view: View?, position: Int, item: ProxyUserModel?) {
         //activity?.startActivity(Intent(activity, ChildSelectionActivity::class.java))
        val editor: SharedPreferences.Editor =
            activity?.getSharedPreferences("user", Context.MODE_PRIVATE)!!.edit()
        if (position == 0) {
            editor.putBoolean("isFamilyMembar", false)
            editor.putString("user_id", item!!.userId)
            SingletonClass.getInstance().userId = item!!.userId
        } else {
            editor.putString("user_email", "")
            editor.putString("user_id", item!!.id)
            SingletonClass.getInstance().userId = item!!.id
            SingletonClass.getInstance().email = ""
            editor.putBoolean("isFamilyMembar", true)
        }
        editor.commit()
        val homeFragment = HomeFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.nav_host_fragment_activity_dashboard, homeFragment)
        transaction?.commit()

    }
}