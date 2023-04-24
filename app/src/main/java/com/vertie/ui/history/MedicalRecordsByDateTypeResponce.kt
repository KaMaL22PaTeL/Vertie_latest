package com.vertie.ui.history

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MedicalRecordsByDateTypeResponce(
    @Expose @SerializedName("medicalRecords") var medicalRecords : List<MedicalRecord>,
    @Expose @SerializedName("weeklyGraphViewModel") var weeklyGraphViewModel : List<WeeklyGraphDataModel>
) : Serializable


