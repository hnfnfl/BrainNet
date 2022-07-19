package com.jaylangkung.indirisma.retrofit

import com.jaylangkung.indirisma.retrofit.response.WilayahResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WilayahService {
    @GET("provinsi")
    fun provinsi(
    ): Call<WilayahResponse>

    @GET("kota")
    fun kotaKab(
        @Query("id_provinsi") id_provinsi: String
    ): Call<WilayahResponse>

    @GET("kecamatan")
    fun kecamatan(
        @Query("id_kota") id_kota: String
    ): Call<WilayahResponse>

    @GET("kelurahan")
    fun kelurahan(
        @Query("id_kecamatan") id_kecamatan: String
    ): Call<WilayahResponse>
}