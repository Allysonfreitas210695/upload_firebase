package com.example.aulafirebase.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.aulafirebase.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var uriImagemSelecionada: Uri? = null
    private val autenticacao = FirebaseAuth.getInstance()
    private val armazenamento = FirebaseStorage.getInstance()

    // ActivityResult para abrir a galeria e selecionar uma imagem
    private val abrirGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uriImagemSelecionada = uri
            binding.imageSelecionada.setImageURI(uri)
            Toast.makeText(requireContext(), "Imagem selecionada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Configurar botão para abrir a galeria
        binding.btnGaleria.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        // Configurar botão para fazer o upload da imagem
        binding.btnUpload.setOnClickListener {
            uploadGaleria()
        }

        // Configurar botão para recuperar imagem do Firebase
        binding.btnRecuperar.setOnClickListener {
            recuperarImagemFirebase()
        }

        return binding.root
    }

    private fun uploadGaleria() {
        val idUser = autenticacao.currentUser?.uid
        if (uriImagemSelecionada != null && idUser != null) {
            armazenamento
                .getReference("fotos")
                .child(idUser)
                .child("foto.jpg")
                .putFile(uriImagemSelecionada!!)
                .addOnSuccessListener { task ->
                    Toast.makeText(requireContext(), "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show()

                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uriFirebase ->
                        Toast.makeText(requireContext(), uriFirebase.toString(), Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun recuperarImagemFirebase() {
        val idUser = autenticacao.currentUser?.uid
        if (idUser != null) {
            armazenamento
                .getReference("fotos")
                .child(idUser)
                .child("foto.jpg")
                .downloadUrl
                .addOnSuccessListener { urlFirebase ->
                    if (urlFirebase != null) {
                        Glide.with(this)
                            .load(urlFirebase)
                            .into(binding.imageRecuperada)
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao recuperar a imagem", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
