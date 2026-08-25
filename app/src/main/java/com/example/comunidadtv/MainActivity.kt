package com.example.comunidadtv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.comunidadtv.ui.theme.ComunidadTVTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComunidadTVTheme {
                ComunidadTVApp()
            }
        }
    }
}

@Composable
fun ComunidadTVApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        // Pantalla principal
        composable("inicio") {
            InicioScreen(
                onAvisosClick = {
                    navController.navigate("avisos")
                },
                onEventosClick = {
                    navController.navigate("eventos")
                }
            )
        }

        // Pantalla de avisos
        composable("avisos") {
            AvisosScreen(
                onRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de eventos
        composable("eventos") {
            EventosScreen(
                onRegresar = {
                    navController.popBackStack()
                }
            )
        }
    }
}


// ----------------------------------------------------
// PANTALLA PRINCIPAL
// ----------------------------------------------------

@Composable
fun InicioScreen(
    onAvisosClick: () -> Unit,
    onEventosClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ComunidadTV"
        )

        Text(
            text = "Información de tu comunidad",
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = onAvisosClick
        ) {
            Text("📢 Avisos")
        }

        Button(
            onClick = onEventosClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("📅 Eventos")
        }
    }
}


// ----------------------------------------------------
// PANTALLA DE AVISOS
// ----------------------------------------------------

@Composable
fun AvisosScreen(
    onRegresar: () -> Unit
) {

    var avisos by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // Leer avisos desde Firebase
    LaunchedEffect(Unit) {

        val referencia = FirebaseDatabase
            .getInstance()
            .getReference("avisos")

        referencia.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val lista = mutableListOf<Map<String, String>>()

                    for (avisoSnapshot in snapshot.children) {

                        val aviso = mapOf(

                            "titulo" to (
                                    avisoSnapshot
                                        .child("titulo")
                                        .getValue(String::class.java)
                                        ?: ""
                                    ),

                            "descripcion" to (
                                    avisoSnapshot
                                        .child("descripcion")
                                        .getValue(String::class.java)
                                        ?: ""
                                    ),

                            "fecha" to (
                                    avisoSnapshot
                                        .child("fecha")
                                        .getValue(String::class.java)
                                        ?: ""
                                    ),

                            "lugar" to (
                                    avisoSnapshot
                                        .child("lugar")
                                        .getValue(String::class.java)
                                        ?: ""
                                    ),

                            "categoria" to (
                                    avisoSnapshot
                                        .child("categoria")
                                        .getValue(String::class.java)
                                        ?: ""
                                    )
                        )

                        lista.add(aviso)
                    }

                    avisos = lista
                    cargando = false
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    error = databaseError.message
                    cargando = false
                }
            }
        )
    }


    // Interfaz
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "📢 Avisos"
        )

        Button(
            onClick = onRegresar,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Regresar")
        }


        when {

            // Cargando Firebase
            cargando -> {

                CircularProgressIndicator()
            }


            // Error de Firebase
            error != null -> {

                Text(
                    text = "Error: $error"
                )
            }


            // No existen avisos
            avisos.isEmpty() -> {

                Text(
                    text = "No hay avisos registrados."
                )
            }


            // Mostrar avisos
            else -> {

                LazyColumn {

                    items(avisos) { aviso ->

                        Card(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {

                                Text(
                                    text = aviso["titulo"] ?: ""
                                )

                                Text(
                                    text = aviso["descripcion"] ?: "",
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        bottom = 12.dp
                                    )
                                )

                                Text(
                                    text = "📅 ${aviso["fecha"]}"
                                )

                                Text(
                                    text = "📍 ${aviso["lugar"]}"
                                )

                                Text(
                                    text = "🏷️ ${aviso["categoria"]}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ----------------------------------------------------
// PANTALLA DE EVENTOS
// ----------------------------------------------------

@Composable
fun EventosScreen(
    onRegresar: () -> Unit
) {

    var eventos by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {

        val referencia = FirebaseDatabase
            .getInstance()
            .getReference("eventos")

        referencia.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val lista = mutableListOf<Map<String, String>>()

                    for (eventoSnapshot in snapshot.children) {

                        val evento = mapOf(
                            "titulo" to (
                                    eventoSnapshot.child("titulo")
                                        .getValue(String::class.java) ?: ""
                                    ),

                            "descripcion" to (
                                    eventoSnapshot.child("descripcion")
                                        .getValue(String::class.java) ?: ""
                                    ),

                            "fecha" to (
                                    eventoSnapshot.child("fecha")
                                        .getValue(String::class.java) ?: ""
                                    ),

                            "lugar" to (
                                    eventoSnapshot.child("lugar")
                                        .getValue(String::class.java) ?: ""
                                    ),

                            "categoria" to (
                                    eventoSnapshot.child("categoria")
                                        .getValue(String::class.java) ?: ""
                                    )
                        )

                        lista.add(evento)
                    }

                    eventos = lista
                    cargando = false
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    error = databaseError.message
                    cargando = false
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "📅 Eventos"
        )

        Button(
            onClick = onRegresar,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Regresar")
        }

        when {

            cargando -> {

                CircularProgressIndicator()
            }

            error != null -> {

                Text(
                    text = "Error: $error"
                )
            }

            eventos.isEmpty() -> {

                Text(
                    text = "No hay eventos registrados."
                )
            }

            else -> {

                LazyColumn {

                    items(eventos) { evento ->

                        Card(
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {

                                Text(
                                    text = evento["titulo"] ?: ""
                                )

                                Text(
                                    text = evento["descripcion"] ?: "",
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        bottom = 12.dp
                                    )
                                )

                                Text(
                                    text = "📅 ${evento["fecha"]}"
                                )

                                Text(
                                    text = "📍 ${evento["lugar"]}"
                                )

                                Text(
                                    text = "🏷️ ${evento["categoria"]}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}