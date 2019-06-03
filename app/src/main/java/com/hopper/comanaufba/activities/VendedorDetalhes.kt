package com.hopper.comanaufba.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hopper.comanaufba.R
import com.hopper.comanaufba.modules.GlideApp

class VendedorDetalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendedor_detalhes)
        val context = this
        val user = intent.extras.getString("id")
        val instance = FirebaseDatabase.getInstance()
        val ref = instance.reference.child(user)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var imagem = FirebaseStorage.getInstance().getReference(p0.child("").child("imagemRef").value.toString())
                Log.d("IMAGEM", imagem.toString())
                GlideApp.with(context)
                    .load(imagem)
                    .into(findViewById(R.id.imagemDetail))
                findViewById<TextView>(R.id.textNome).text = p0.child("").child("nome").value.toString()
                findViewById<TextView>(R.id.textAberto).text =
                    "Aberto agora! ${p0.child("").child("horarioInicial").value.toString()} - ${p0.child("").child("horarioFinal").value.toString()}"
                findViewById<TextView>(R.id.textLocal).text =
                    "${p0.child("").child("localizacao").value.toString()} - ${p0.child("").child("campus").value.toString()}"
                val tagsContent = p0.child("").child("tags").value as ArrayList<String>?
                if (tagsContent != null) {
                    findViewById<TextView>(R.id.tags).text = tagsContent.joinToString()
                }
                findViewById<TextView>(R.id.textTelefone).text = p0.child("").child("contato").value.toString()
                findViewById<TextView>(R.id.textDescricao).text = p0.child("").child("descricao").value.toString()
                var pagamentoTexto = "Aceita "
                var vetorPag = arrayOf(p0.child("").child("formasPagamento").value)
                (vetorPag).forEachIndexed { index, s ->
                    if(index < vetorPag.size - 1){
                        if(vetorPag.size == 2){
                            pagamentoTexto += "$s e "
                        } else {
                            pagamentoTexto += "$s, "
                        }
                    } else {
                        pagamentoTexto += s
                    }
                }
                findViewById<Button>(R.id.buttonCaminho).setOnClickListener {
                    val lat = p0.child("").child("geolocalizacao").child("latitude").value.toString()
                    val lon = p0.child("").child("geolocalizacao").child("longitude").value.toString()
                    val uri = "google.navigation:q=$lat,$lon"
                    Log.d("URI", uri)
                    val gmmIntentUri = Uri.parse(uri)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        })
    }
}
