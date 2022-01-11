package com.jaylangkung.brainnet_staff.retrofit

import com.jaylangkung.brainnet_staff.retrofit.response.AddCustomerSpinnerResponse
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.LoginResponse
import com.jaylangkung.brainnet_staff.retrofit.response.WilayahResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    //login
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("auth/addToken")
    fun addToken(
        @Field("idadmin") idadmin: String,
        @Field("device_token") device_token: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("auth/refreshToken")
    fun refreshAuthToken(
        @Field("email") email: String,
        @Field("idadmin") idadmin: String,
        @Field("device_id") device_id: String
    ): Call<LoginResponse>

    //logout
    @FormUrlEncoded
    @POST("auth/logout")
    fun logout(
        @Field("idadmin") idadmin: String,
    ): Call<DefaultResponse>

    @GET("auth/getPaketInternet")
    fun getPaketInternet(
    ): Call<AddCustomerSpinnerResponse>

    @GET("auth/getMarketing")
    fun getMarketing(
    ): Call<AddCustomerSpinnerResponse>

    @GET("auth/getRekanan")
    fun getRekanan(
    ): Call<AddCustomerSpinnerResponse>
}