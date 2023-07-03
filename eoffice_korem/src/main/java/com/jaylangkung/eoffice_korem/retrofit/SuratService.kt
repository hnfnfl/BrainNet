package com.jaylangkung.eoffice_korem.retrofit

import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.NotifikasiResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratKeluarResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratMasukResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface SuratService {
    @Multipart
    @POST("surat/insertSuratMasuk")
    fun insertSuratMasuk(
        @Part("iduser") iduser: RequestBody,
        @Part("sumber_surat") sumber: RequestBody,
        @Part("sumber_surat_next") sumberSurat: RequestBody,
        @Part("pengirim") pengirim: RequestBody,
        @Part("perihal") perihal: RequestBody,
        @Part("tanggal_surat") tglSurat: RequestBody,
        @Part foto: MultipartBody.Part? = null,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("surat/editSuratMasuk")
    fun editSuratMasuk(
        @Field("idsurat") idsurat: String,
        @Field("sumber_surat") sumberSurat: String,
        @Field("sumber_surat_next") sumberSuratNext: String,
        @Field("pengirim") pengirim: String,
        @Field("perihal") perihal: String,
        @Field("status") status: String,
        @Field("date") tglSurat: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @GET("surat/getSuratMasuk")
    fun getSuratMasuk(
        @Query("sumber_surat") sumber: String,
        @Query("sumber_surat_next") sumberNext: String,
        @Query("bentuk") bentuk: String,
        @Query("disposisi") disposisi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<SuratMasukResponse>

    @GET("surat/getSuratKeluar")
    fun getSuratKeluar(
        @Query("bentuk") bentuk: String,
        @Query("disposisi") disposisi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<SuratKeluarResponse>

    @Multipart
    @POST("surat/insertSuratKeluar")
    fun insertSuratKeluar(
        @Part("iduser") iduser: RequestBody,
        @Part("perihal") perihal: RequestBody,
        @Part("kepada") kepada: RequestBody,
        @Part("persetujuan") persetujuan: RequestBody,
        @Part("tanggal_surat") tglSurat: RequestBody,
        @Part foto: MultipartBody.Part? = null,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("surat/editSuratKeluar")
    fun editSuratKeluar(
        @Field("idsurat") idsurat: String,
        @Field("perihal") perihal: String,
        @Field("kepada") kepada: String,
        @Field("persetujuan") persetujuan: String,
        @Field("date") tglSurat: String,
        @Field("status") status: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("surat/insertSuratDisposisi")
    fun insertSuratDisposisi(
        @Field("iduser") iduser: String,
        @Field("idsurat") idsurat: String,
        @Field("tipe_surat") tipe_surat: String,
        @Field("jenis") jenis: String,
        @Field("catatan") catatan: String,
        @Field("catatan_tambahan") catatan_tambahan: String,
        @Field("penerima") penerima: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("surat/editSuratDisposisi")
    fun editSuratDisposisi(
        @Field("nomer_agenda") nomer_agenda: String,
        @Field("balasan") balasan: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @GET("surat/getNotifikasi")
    fun getNotifikasi(
        @Query("iduser") iduser: String,
        @Header("Authorization") tokenAuth: String
    ): Call<NotifikasiResponse>
}