package com.example.aulafirebase.activitys

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aulafirebase.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            cadastrarUser()
        }
    }

    private fun cadastrarUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            // Mostrar a ProgressBar e desabilitar o botão de registro
            binding.progressBar2.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        // Ocultar a ProgressBar e habilitar o botão de registro após a operação
                        binding.progressBar2.visibility = View.GONE
                        binding.btnRegister.isEnabled = true

                        if (task.isSuccessful) {
                            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Falha ao cadastrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        // Também ocultar a ProgressBar e habilitar o botão em caso de falha
                        binding.progressBar2.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Toast.makeText(this, "Erro: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                binding.progressBar2.visibility = View.GONE // Ocultar a ProgressBar se as senhas não corresponderem
                binding.btnRegister.isEnabled = true // Reabilitar o botão
                Toast.makeText(this, "As senhas não correspondem", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }
}
