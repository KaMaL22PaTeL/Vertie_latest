package com.vertie.ui.history
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RecoveryAbility (

    @Expose @SerializedName("status") var status : String,
    @Expose @SerializedName("description") var description : String

        ) : Serializable