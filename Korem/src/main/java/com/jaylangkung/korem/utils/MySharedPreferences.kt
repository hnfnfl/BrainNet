package com.jaylangkung.korem.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(mContext: Context) {

    companion object {
        const val USER_PREF = "USER_PREF"
    }

    private val mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun setValue(key: String, value: String) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setValueBool(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setValueInteger(key: String, value: Int) {
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
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