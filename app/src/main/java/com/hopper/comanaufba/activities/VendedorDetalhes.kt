package com.hopper.comanaufba.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hopper.comanaufba.R

class VendedorDetalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendedor_detalhes)
        val user = intent.extras.getString("id")
        
    }
}
