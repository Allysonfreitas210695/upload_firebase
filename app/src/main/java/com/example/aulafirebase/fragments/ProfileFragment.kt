package com.example.aulafirebase.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.aulafirebase.activitys.LoginActivity
import com.example.aulafirebase.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var textId: TextView
    private lateinit var textEmail: TextView
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        logoutButton = view.findViewById(R.id.btnLogout)
        textId = view.findViewById(R.id.textId)
        textEmail = view.findViewById(R.id.textEmail)

        // Recuperar informações do usuário
        val usuario = auth.currentUser
        if (usuario != null) {
            // Exibir ID e email do usuário
            textId.text = "ID: " + usuario.uid
            textEmail.text = "E-mail: " + usuario.email
        } else {
            textId.text = "ID não disponível"
            textEmail.text = "Email não disponível"
        }

        logoutButton.setOnClickListener {
            auth.signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
