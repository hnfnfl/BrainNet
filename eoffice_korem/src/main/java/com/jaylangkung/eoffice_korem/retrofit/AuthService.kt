package com.jaylangkung.eoffice_korem.retrofit

import com.jaylangkung.eoffice_korem.dataClass.DataSpinnerResponse
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    //login user
    @FormUrlEncoded
    @POST("auth/loginUser")
    fun login(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("device_id") deviceId: String,
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("auth/addToken")
    fun addToken(
        @Field("iduser_aktivasi") iduserAktivasi: String,
        @Field("device_token") deviceToken: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("auth/refreshToken")
    fun refreshAuthToken(
        @Field("iduser_aktivasi") iduserAktivasi: String,
    ): Call<UserResponse>

    @GET("auth/getSpinnerSurat")
    fun getSuratSpinnerData(): Call<DataSpinnerResponse>
}