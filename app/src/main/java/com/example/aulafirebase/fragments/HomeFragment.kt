package com.example.aulafirebase.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.aulafirebase.databinding.FragmentHomeBinding
import com.example.aulafirebase.helpers.Permissao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var uriImagemSelecionada: Uri? = null
    private var bitmapImage: Bitmap? = null

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

    // abrir camera e tirar uma foto
    private val abrirCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imagemCapturada = result.data?.extras?.getParcelable<Bitmap>("data")
        if (imagemCapturada != null) {
            bitmapImage = imagemCapturada
            binding.imageSelecionada.setImageBitmap( imagemCapturada )
            Toast.makeText(requireContext(), "Foto capturada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Erro ao capturar a imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissoes = listOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("permissao_app", "requestCode: ${requestCode}")
        
        permissions.forEachIndexed { index, valor ->
            Log.i("permissao_app", "permission: $index) ${valor}")
        }

        grantResults.forEachIndexed { index, valor ->
            Log.i("permissao_app", "concedida: $index) ${valor}")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        Permissao.requisirarPermissoes(
            requireActivity(),
            permissoes,
            100
        )

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

        binding.btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            abrirCamera.launch(intent)
        }

        return binding.root
    }

    private fun uploadCamera() {
        val idUser = autenticacao.currentUser?.uid

        val outputStream = ByteArrayOutputStream()

        bitmapImage?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outputStream
        )

        if (bitmapImage != null && idUser != null) {
            armazenamento
                .getReference("fotos")
                .child(idUser)
                .child("foto.jpg")
                .putBytes( outputStream.toByteArray() )
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
