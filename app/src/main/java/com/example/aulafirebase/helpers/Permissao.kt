package com.example.aulafirebase.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

class Permissao {
    companion object {
        fun requisirarPermissoes(activity: Activity, permissoes: List<String>, requestCode: Int){

            val permissoesNegadas = mutableListOf<String>()
            permissoes.forEach { permissao ->
               val temPermissao = ContextCompat.checkSelfPermission(
                    activity,
                    permissao
                ) == PackageManager.PERMISSION_DENIED

                if( !temPermissao ){
                    permissoesNegadas.add( permissao )
                }
            }

            if( permissoesNegadas.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    permissoesNegadas.toTypedArray(),
                    requestCode
                )
            }
        }

    }

}