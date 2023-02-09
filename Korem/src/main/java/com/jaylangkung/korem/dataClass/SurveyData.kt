package com.jaylangkung.korem.dataClass

data class SurveyData(
    val idsurvey: String,
    val jawaban: List<SurveyJawaban>,
    val pertanyaan: String
)