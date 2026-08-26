package com.example.comunidadtv.tv

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

import com.example.comunidadtv.tv.ui.theme.ComunidadTVTheme

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// =========================================================
// MODELO EVENTO
// =========================================================

data class Evento(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val lugar: String = "",
    val categoria: String = ""
)


// =========================================================
// MAIN ACTIVITY
// =========================================================

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            ComunidadTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    ComunidadTV()
                }
            }
        }
    }
}


// =========================================================
// CONTROL PRINCIPAL DE LA TV
// =========================================================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ComunidadTV() {

    // Pantalla inicial
    var pantalla by remember {
        mutableStateOf("inicio")
    }


    /*
     * =====================================================
     * CONTROL DE LA TV CON FIREBASE
     *
     * Al iniciar la TV:
     *
     * control_tv
     *      pantalla = "inicio"
     *
     * Esto evita que la TV recuerde "eventos" de una
     * ejecución anterior.
     * =====================================================
     */

    LaunchedEffect(Unit) {

        val referencia = FirebaseDatabase
            .getInstance()
            .getReference("control_tv")
            .child("pantalla")


        // -------------------------------------------------
        // PRIMERO: FORZAMOS LA TV A INICIO
        // -------------------------------------------------

        referencia
            .setValue("inicio")
            .addOnCompleteListener {

                // -------------------------------------------------
                // DESPUÉS: ESCUCHAMOS LOS CAMBIOS DEL MÓVIL
                // -------------------------------------------------

                referencia.addValueEventListener(

                    object : ValueEventListener {

                        override fun onDataChange(
                            snapshot: DataSnapshot
                        ) {

                            val nuevaPantalla =
                                snapshot.value
                                    ?.toString()
                                    ?: "inicio"


                            // -----------------------------------------
                            // Solo aceptamos pantallas válidas
                            // -----------------------------------------

                            pantalla = when (nuevaPantalla) {

                                "eventos" -> "eventos"

                                else -> "inicio"
                            }
                        }


                        override fun onCancelled(
                            error: DatabaseError
                        ) {

                            pantalla = "inicio"
                        }
                    }
                )
            }
    }


    // =====================================================
    // NAVEGACIÓN
    // =====================================================

    when (pantalla) {

        // -------------------------------------------------
        // INICIO
        // -------------------------------------------------

        "inicio" -> {

            InicioTV(
                onEventos = {

                    FirebaseDatabase
                        .getInstance()
                        .getReference("control_tv")
                        .child("pantalla")
                        .setValue("eventos")
                }
            )
        }


        // -------------------------------------------------
        // EVENTOS
        // -------------------------------------------------

        "eventos" -> {

            EventosTV()
        }


        // -------------------------------------------------
        // CUALQUIER OTRO VALOR
        // -------------------------------------------------

        else -> {

            InicioTV(
                onEventos = {

                    FirebaseDatabase
                        .getInstance()
                        .getReference("control_tv")
                        .child("pantalla")
                        .setValue("eventos")
                }
            )
        }
    }
}


// =========================================================
// INICIO TV
// =========================================================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun InicioTV(
    onEventos: () -> Unit
) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {


        Text(
            text = "ComunidadTV"
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Información de tu comunidad"
        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        // =================================================
        // BOTÓN EVENTOS
        // =================================================

        Button(

            onClick = onEventos,

            modifier = Modifier.width(300.dp)
        ) {

            Text(
                text = "📅 Eventos"
            )
        }
    }
}


// =========================================================
// EVENTOS TV
// =========================================================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EventosTV() {

    var eventos by remember {

        mutableStateOf<List<Evento>>(
            emptyList()
        )
    }


    var cargando by remember {

        mutableStateOf(true)
    }


    // =====================================================
    // LEER EVENTOS DE FIREBASE
    // =====================================================

    LaunchedEffect(Unit) {

        val referencia = FirebaseDatabase
            .getInstance()
            .getReference("eventos")


        referencia.addValueEventListener(

            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    val lista =
                        mutableListOf<Evento>()


                    for (
                    hijo in snapshot.children
                    ) {

                        val evento = Evento(

                            id = hijo.key ?: "",


                            titulo =
                                hijo.child("titulo")
                                    .value
                                    ?.toString()
                                    ?: "",


                            descripcion =
                                hijo.child("descripcion")
                                    .value
                                    ?.toString()
                                    ?: "",


                            fecha =
                                hijo.child("fecha")
                                    .value
                                    ?.toString()
                                    ?: "",


                            lugar =
                                hijo.child("lugar")
                                    .value
                                    ?.toString()
                                    ?: "",


                            categoria =
                                hijo.child("categoria")
                                    .value
                                    ?.toString()
                                    ?: ""
                        )


                        lista.add(evento)
                    }


                    eventos = lista

                    cargando = false
                }


                override fun onCancelled(
                    error: DatabaseError
                ) {

                    eventos = emptyList()

                    cargando = false
                }
            }
        )
    }


    // =====================================================
    // DISEÑO
    // =====================================================

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // -------------------------------------------------
        // TÍTULO
        // -------------------------------------------------

        Text(
            text = "📅 EVENTOS DE LA COMUNIDAD"
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // -------------------------------------------------
        // CARGANDO
        // -------------------------------------------------

        if (cargando) {

            Text(
                text = "Cargando eventos..."
            )
        }


        // -------------------------------------------------
        // SIN EVENTOS
        // -------------------------------------------------

        else if (eventos.isEmpty()) {

            Text(
                text = "No hay eventos registrados."
            )
        }


        // -------------------------------------------------
        // EVENTOS
        // -------------------------------------------------

        else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(20.dp)
            ) {

                items(eventos) { evento ->

                    EventoCard(evento)
                }
            }
        }
    }
}


// =========================================================
// TARJETA DEL EVENTO
// =========================================================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EventoCard(
    evento: Evento
) {

    Card(
        onClick = {}
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {


            // -------------------------------------------------
            // TÍTULO
            // -------------------------------------------------

            Text(
                text = evento.titulo
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // -------------------------------------------------
            // DESCRIPCIÓN
            // -------------------------------------------------

            Text(
                text = evento.descripcion
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // -------------------------------------------------
            // FECHA
            // -------------------------------------------------

            Text(
                text = "📅 ${evento.fecha}"
            )


            // -------------------------------------------------
            // LUGAR
            // -------------------------------------------------

            Text(
                text = "📍 ${evento.lugar}"
            )


            // -------------------------------------------------
            // CATEGORÍA
            // -------------------------------------------------

            Text(
                text = "🏷️ ${evento.categoria}"
            )
        }
    }
}