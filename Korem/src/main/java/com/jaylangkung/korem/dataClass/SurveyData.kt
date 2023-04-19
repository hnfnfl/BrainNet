package com.jaylangkung.korem.dataClass

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