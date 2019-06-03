package com.hopper.comanaufba.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.hopper.comanaufba.R
import com.hopper.comanaufba.models.Vendedor
import com.hopper.comanaufba.modules.GlideApp

class VendedorRVAdapter(private val vendedores: ArrayList<Vendedor>, val context: Context) : RecyclerView.Adapter<VendedorRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_model, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.d("TAM",vendedores.size.toString())
        return vendedores.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val single = vendedores[position]
        holder.cv.tag = single.id
        holder.nome.text = single.nome
        holder.tags.text = single.tags.joinToString()
        holder.localizacao.text = single.localizacao
        holder.aberto.text = if(single.aberto) "Aberto agora!" else "Fechado :("
        var imagem = single.imagemRef?.let { FirebaseStorage.getInstance().getReference(it) }
        GlideApp.with(context)
            .load(imagem)
            .into(holder.imagem)
//        var categoriaTexto = ""
//        (single.categorias).forEachIndexed { index, s ->
//            if(index < single.categorias.size - 1){
//                categoriaTexto += "$s, "
//            } else {
//                categoriaTexto += s
//            }
//        }
//        holder.categorias.text = categoriaTexto
        var pagamentoTexto = "Aceita "
        (single.formasPagamento).forEachIndexed { index, s ->
            if(index < single.formasPagamento.size - 1){
                if(single.formasPagamento.size == 2){
                    pagamentoTexto += "$s e "
                } else {
                    pagamentoTexto += "$s, "
                }
            } else {
                pagamentoTexto += s
            }
        }
        holder.formasPagamento.text = pagamentoTexto
        Log.d("Teste", "Chegou aqui")
    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.findViewById(R.id.cv)
        var nome: TextView = itemView.findViewById(R.id.nome)
        var localizacao: TextView = itemView.findViewById(R.id.localizacao)
        var aberto: TextView = itemView.findViewById(R.id.aberto)
        var formasPagamento: TextView = itemView.findViewById(R.id.formasPagamento)
        var tags: TextView = itemView.findViewById(R.id.tags)
        var imagem: ImageView = itemView.findViewById(R.id.imagem)
    }

}