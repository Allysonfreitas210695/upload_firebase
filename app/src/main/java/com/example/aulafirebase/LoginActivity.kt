package com.example.aulafirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aulafirebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var autenticacao = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()

       verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuario = autenticacao.currentUser

        if(usuario != null){
            Toast.makeText(this, "usuário logado id: "+usuario?.uid, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(this, "Não tem usuário logado!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            logarUsuario()
        }

        binding.linkRegister.setOnClickListener {
            startActivity( Intent(this, CadastroActivity::class.java) )
        }
    }

    private fun logarUsuario() {
        if (!binding.editTextLogin.text.isNullOrEmpty() && !binding.editTextPassword.text.isNullOrEmpty()) {
            autenticacao.signInWithEmailAndPassword(
                binding.editTextLogin.text.toString(),
                binding.editTextPassword.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(this, "Sucesso ao logar!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao logar! " + exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}