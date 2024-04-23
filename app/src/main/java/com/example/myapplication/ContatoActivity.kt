package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ContatoActivity : Activity() {

    private val listaContatos = ArrayList<Contato>()

    override fun onCreate(bundle : Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.contato_activity)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)

        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        carregarPrefs()

        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val phone = edtPhone.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                val contato = Contato(name = name, email = email, phone = phone)
                listaContatos.add(contato)
                salvarPrefs()
                Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show()

                edtName.text.clear()
                edtEmail.text.clear()
                edtPhone.text.clear()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener {
            val nomeBusca = edtName.text.toString()

            // Procurar na lista de contatos pelo nome que contém o texto digitado
            val contatoEncontrado = listaContatos.find { contato ->
                contato.name.contains(nomeBusca, ignoreCase = true)
            }

            if (contatoEncontrado != null) {
                // Preencher os EditTexts com as informações do contato encontrado
                edtName.setText(contatoEncontrado.name)
                edtEmail.setText(contatoEncontrado.email)
                edtPhone.setText(contatoEncontrado.phone)
            } else {
                // Limpar os EditTexts se nenhum contato for encontrado
                edtName.setText("")
                edtEmail.setText("")
                edtPhone.setText("")
                Toast.makeText(this, "Contato não encontrado", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun salvarPrefs() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("contatos_prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Converter lista de contatos para JSON usando Gson
        val gson = Gson()
        val jsonContatos = gson.toJson(listaContatos)

        // Salvar JSON no SharedPreferences
        editor.putString("contatos", jsonContatos)
        editor.apply()
    }

    private fun carregarPrefs() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("contatos_prefs", Context.MODE_PRIVATE)
        val jsonContatos = sharedPreferences.getString("contatos", "")

        if (!jsonContatos.isNullOrBlank()) {
            // Converter JSON de volta para lista de contatos usando Gson
            val gson = Gson()
            val type: Type = object : TypeToken<ArrayList<Contato>>() {}.type
            listaContatos.clear() // Limpar lista atual antes de adicionar contatos salvos
            listaContatos.addAll(gson.fromJson(jsonContatos, type))
        }
    }
}