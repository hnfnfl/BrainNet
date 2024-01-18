package com.jaylangkung.eoffice_korem.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(mContext: Context) {

    companion object {
        const val USER_PREF = "USER_PREF"
    }

    private val mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun clear() {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.clear().apply()
    }
    fun setValue(key: String, value: String) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString(key, value).apply()
    }

    fun setValueBool(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(key, value).apply()
    }

    fun setValueInteger(key: String, value: Int) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putInt(key, value).apply()
    }

//    fun getAll() {
//        val allEntries: Map<String, *> = mSharedPreferences.all
//        for (entry: Map.Entry<String, *> in allEntries) {
//            Log.e("load sharedpref", "${entry.key} : ${entry.value}")
//        }
//    }

    fun getValue(key: String): String? {
        return mSharedPreferences.getString(key, "")
    }

    fun getValueBool(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun getValueInteger(key: String): Int {
        return mSharedPreferences.getInt(key, 0)
    }
}