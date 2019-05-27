package com.hopper.comanaufba.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageMetadata
import com.hopper.comanaufba.R
import com.hopper.comanaufba.adapters.VendedorRVAdapter
import com.hopper.comanaufba.models.Vendedor

class MainActivity : AppCompatActivity() {
    private val RC_SIGNIN = 1;
    private var user = FirebaseAuth.getInstance().currentUser
    private var vendedores: ArrayList<Vendedor> = arrayListOf()
    private lateinit var instance: FirebaseDatabase

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGNIN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                user = FirebaseAuth.getInstance().currentUser
            } else {

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = FirebaseDatabase.getInstance()
        instance.setPersistenceEnabled(true)
        setContentView(R.layout.activity_main)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        var auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){

        } else {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGNIN
            )
        }
        var ref = instance.reference
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    var auxFormas: ArrayList<String> = arrayListOf()
                    (it.child("formasPagamento").children).forEach {
                        auxFormas.add(it.value.toString())
                    }
                    Log.d("FORMAS", auxFormas.toString());
                    val add = vendedores.add(
                        Vendedor(it.value.toString(),
                            it.child("nome").value.toString(),
                            it.child("contato").value.toString(),
                            it.child("descricao").value.toString(),
                            it.child("localizacao").value.toString(),
                            Location(it.child("geolocalizacao").value.toString()),
                            it.child("horarioInicial").value.toString(),
                            it.child("horarioFinal").value.toString(),
                            it.child("aberto").value.toString() == "true",
                            it.child("imagemRef").value.toString(),
                            it.child("campus").value.toString(),
                            auxFormas
                        )
                    )
                    val rv = findViewById<RecyclerView>(R.id.rv)
                    rv.layoutManager = LinearLayoutManager(applicationContext)
                    val rvAdapter = VendedorRVAdapter(vendedores, applicationContext)
                    rv.adapter = rvAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "Ocorreu um erro de conexão com o banco", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun onClickCard(v: View){
        intent = Intent(this, VendedorDetalhes::class.java).apply {
            putExtra("id", v.tag.toString())
        }
        startActivity(intent)
    }
}
