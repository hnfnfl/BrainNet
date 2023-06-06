package com.jaylangkung.korem.dataClass

data class SurveyResponse(
    val data: ArrayList<SurveyData>,
    val status: String
)

data class SurveyData(
    val idsurvey: String,
    val jawaban: ArrayList<SurveyJawaban>,
    val pertanyaan: String
)

data class SurveyJawaban(
    val id: Int,
    val parameter: String,
    val value: String
)