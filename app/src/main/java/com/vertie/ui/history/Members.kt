package com.vertie.ui.history

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Members(
    @Expose @SerializedName("members") var members : MedicalRecordsByDateTypeResponce
) : Serializable


