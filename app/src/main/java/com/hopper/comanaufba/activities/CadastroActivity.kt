package com.hopper.comanaufba.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hopper.comanaufba.R
import com.hopper.comanaufba.models.Vendedor
import com.hopper.comanaufba.modules.MaskEditUtil
import java.util.*
import kotlin.collections.ArrayList

class CadastroActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var localizacaoAtual: Location? = null
    private val PICK_IMAGE = 2020;
    private var imagemSelecionada: String? = null
    private lateinit var ref: DatabaseReference
    private lateinit var instance: FirebaseDatabase
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        // Adapter
        val drop = findViewById<Spinner>(R.id.campus)
        val items = arrayOf("Ondina", "Canela", "Federação", "Faculdade de Economia")
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, items)
        drop.adapter = adapter
        // Firebase Database
        instance = FirebaseDatabase.getInstance()
        ref = instance.reference
        storageRef = FirebaseStorage.getInstance().reference.child("images/")
        // Localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        findViewById<NachoTextView>(R.id.nacho_text_view).addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
        // Máscaras
        val etHorarioInicial = findViewById<EditText>(R.id.horarioInicial)
        etHorarioInicial.addTextChangedListener(MaskEditUtil.mask(etHorarioInicial, MaskEditUtil.FORMAT_HOUR))
        val etHorarioFinal = findViewById<EditText>(R.id.horarioFinal)
        etHorarioFinal.addTextChangedListener(MaskEditUtil.mask(etHorarioFinal, MaskEditUtil.FORMAT_HOUR))
        val etTelefone = findViewById<EditText>(R.id.contato)
        etTelefone.addTextChangedListener(MaskEditUtil.mask(etTelefone, MaskEditUtil.FORMAT_FONE))
    }
    fun pickLocation(view: View){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            localizacaoAtual = location
            Toast.makeText(applicationContext,"Localização obtida com sucesso", Toast.LENGTH_SHORT).show()
            Log.d("Loca", location.toString())
        }
            .addOnCanceledListener {
                Toast.makeText(applicationContext, "Algum erro foi encontrado", Toast.LENGTH_SHORT).show()
            }
    }
    fun pickImage(view: View){
        val intent = Intent()
        intent.type = "image/*";
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(applicationContext, "Imagem selecionada com sucesso", Toast.LENGTH_SHORT).show()
                if (data != null) {
                    storageRef.child(UUID.randomUUID().toString()).putFile(data.data).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            imagemSelecionada = task.result?.metadata?.path
                            Log.d("Imagem", imagemSelecionada.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Foi encontrado um erro na seleção da imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun saveForm(view: View){
        val nome: String = findViewById<EditText>(R.id.nome).text.toString()
        val localizacao: String = findViewById<EditText>(R.id.localizacao).text.toString()
        val campus: String = findViewById<Spinner>(R.id.campus).selectedItem as String
        var formasPagamento: ArrayList<String> = arrayListOf();
        if(findViewById<CheckBox>(R.id.cbDinheiro).isChecked) formasPagamento.add("Dinheiro")
        if(findViewById<CheckBox>(R.id.cbDebito).isChecked) formasPagamento.add("Débito")
        if(findViewById<CheckBox>(R.id.cbCredito).isChecked) formasPagamento.add("Crédito")
        if(findViewById<CheckBox>(R.id.cbPicpay).isChecked) formasPagamento.add("PicPay")
        val contato: String = findViewById<EditText>(R.id.contato).text.toString()
        val horarioInicial: String = findViewById<EditText>(R.id.horarioInicial).text.toString()
        val horarioFinal: String = findViewById<EditText>(R.id.horarioFinal).text.toString()
        val descricao: String = findViewById<EditText>(R.id.tags).text.toString()
        val tags = findViewById<NachoTextView>(R.id.nacho_text_view).chipValues
        val vendedor = Vendedor(nome = nome,contato = contato,descricao = descricao,localizacao = localizacao,geolocalizacao = localizacaoAtual,horarioInicial = horarioInicial,horarioFinal = horarioFinal,aberto = true,imagemRef = imagemSelecionada,campus = campus,formasPagamento = formasPagamento, tags = tags)
        ref.push().setValue(vendedor).addOnSuccessListener {
            Toast.makeText(applicationContext, "Cadastro criado com sucesso!", Toast.LENGTH_SHORT).show()
        }.addOnCanceledListener {
            Toast.makeText(applicationContext, "Falha no cadastro!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                finish()
            }
            R.id.nav_cadastro -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
