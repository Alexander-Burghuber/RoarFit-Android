package at.htl_leonding.roarfit.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val code: Int,
    val personId: Int,
    val message: String,
    val token: String,
    val permissions: List<LoginPermission>
)

data class LoginPermission(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("object") val obj: String,
    val action: String
)