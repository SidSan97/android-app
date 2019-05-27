package com.hopper.comanaufba.models

import android.location.Location

class Vendedor(
    val id: String = "",
    val nome: String,
    val contato: String,
    val descricao:String,
    val localizacao: String,
    val geolocalizacao: Location?,
    val horarioInicial: String,
    val horarioFinal: String,
    val aberto: Boolean,
    val imagemRef: String?,
    val campus: String,
    val formasPagamento: ArrayList<String>
               //val categorias: ArrayList<String>,
               //val itens: HashMap<String, Boolean>
){

}