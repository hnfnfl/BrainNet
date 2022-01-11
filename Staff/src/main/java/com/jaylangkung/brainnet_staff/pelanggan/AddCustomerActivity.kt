package com.jaylangkung.brainnet_staff.pelanggan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityAddCustomerBinding

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var addCustomerBinding: ActivityAddCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomerBinding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(addCustomerBinding.root)
    }
}