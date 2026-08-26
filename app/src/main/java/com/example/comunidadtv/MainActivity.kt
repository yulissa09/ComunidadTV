package com.example.comunidadtv

import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.platform.LocalContext

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.comunidadtv.ui.theme.ComunidadTVTheme

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// =========================================================
// COLORES DEL DISEÑO
// =========================================================

val RosaPrincipal = Color(0xFFFFA8B8)
val RosaOscuro = Color(0xFF8F3D50)
val RosaClaro = Color(0xFFFFF0F3)
val Fondo = Color(0xFFFFF8FA)
val Tarjeta = Color(0xFFFFFFFF)
val TextoPrincipal = Color(0xFF3E3033)
val TextoSecundario = Color(0xFF725F64)


// =========================================================
// MAIN ACTIVITY
// =========================================================

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


// =========================================================
// NAVEGACIÓN PRINCIPAL
// =========================================================

@Composable
fun ComunidadTVApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        // =================================================
        // INICIO
        // =================================================

        composable("inicio") {

            InicioScreen(

                onEventosClick = {

                    // Mandamos la orden a la TV
                    FirebaseDatabase
                        .getInstance()
                        .getReference("control_tv")
                        .child("pantalla")
                        .setValue("eventos")

                    // Abrimos Eventos en el móvil
                    navController.navigate("eventos")
                }
            )
        }


        // =================================================
        // EVENTOS
        // =================================================

        composable("eventos") {

            EventosScreen(

                onRegresar = {

                    navController.popBackStack()
                },

                onRegistrarEvento = {

                    navController.navigate("registrarEvento")
                }
            )
        }


        // =================================================
        // REGISTRAR EVENTO
        // =================================================

        composable("registrarEvento") {

            RegistrarEventoScreen(

                onRegresar = {

                    navController.popBackStack()
                }
            )
        }
    }
}


// =========================================================
// PANTALLA PRINCIPAL
// =========================================================

@Composable
fun InicioScreen(
    onEventosClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 28.dp,
                    vertical = 40.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {


            // =================================================
            // ENCABEZADO
            // =================================================

            Text(
                text = "📺",
                fontSize = 55.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "ComunidadTV",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = RosaOscuro
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Información de tu comunidad",
                fontSize = 17.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(45.dp)
            )


            // =================================================
            // TARJETA PRINCIPAL
            // =================================================

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),

                shape = RoundedCornerShape(28.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Tarjeta
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "📅",
                        fontSize = 42.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Eventos comunitarios",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Consulta los próximos eventos de tu comunidad.",
                        fontSize = 14.sp,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )


                    // =================================================
                    // BOTÓN EVENTOS
                    // =================================================

                    Button(

                        onClick = onEventosClick,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = RosaPrincipal,
                            contentColor = TextoPrincipal
                        )
                    ) {

                        Text(
                            text = "📅  Ver eventos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// =========================================================
// EVENTOS
// =========================================================

@Composable
fun EventosScreen(
    onRegresar: () -> Unit,
    onRegistrarEvento: () -> Unit
) {

    var eventos by remember {

        mutableStateOf<List<Map<String, String>>>(
            emptyList()
        )
    }


    var cargando by remember {

        mutableStateOf(true)
    }


    var error by remember {

        mutableStateOf<String?>(null)
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
                        mutableListOf<Map<String, String>>()


                    for (
                    eventoSnapshot in snapshot.children
                    ) {

                        val evento = mapOf(

                            "titulo" to (
                                    eventoSnapshot
                                        .child("titulo")
                                        .getValue(
                                            String::class.java
                                        ) ?: ""
                                    ),

                            "descripcion" to (
                                    eventoSnapshot
                                        .child("descripcion")
                                        .getValue(
                                            String::class.java
                                        ) ?: ""
                                    ),

                            "fecha" to (
                                    eventoSnapshot
                                        .child("fecha")
                                        .getValue(
                                            String::class.java
                                        ) ?: ""
                                    ),

                            "lugar" to (
                                    eventoSnapshot
                                        .child("lugar")
                                        .getValue(
                                            String::class.java
                                        ) ?: ""
                                    ),

                            "categoria" to (
                                    eventoSnapshot
                                        .child("categoria")
                                        .getValue(
                                            String::class.java
                                        ) ?: ""
                                    )
                        )

                        lista.add(evento)
                    }


                    eventos = lista

                    cargando = false

                    error = null
                }


                override fun onCancelled(
                    databaseError: DatabaseError
                ) {

                    error = databaseError.message

                    cargando = false
                }
            }
        )
    }


    // =====================================================
    // DISEÑO DE EVENTOS
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 35.dp
                )
        ) {


            // =================================================
            // ENCABEZADO
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "📅",
                    fontSize = 34.sp
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text = "Eventos",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = RosaOscuro
                    )

                    Text(
                        text = "Actividades de tu comunidad",
                        fontSize = 14.sp,
                        color = TextoSecundario
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // =================================================
            // BOTONES
            // =================================================

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {


                Button(

                    onClick = onRegresar,

                    modifier = Modifier
                        .height(50.dp),

                    shape = RoundedCornerShape(16.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8DDE0),
                        contentColor = TextoPrincipal
                    )
                ) {

                    Text(
                        text = "← Regresar",
                        fontWeight = FontWeight.Bold
                    )
                }


                Button(

                    onClick = onRegistrarEvento,

                    modifier = Modifier
                        .height(50.dp),

                    shape = RoundedCornerShape(16.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosaPrincipal,
                        contentColor = TextoPrincipal
                    )
                ) {

                    Text(
                        text = "＋ Registrar evento",
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // CONTENIDO
            // =================================================

            when {

                cargando -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(
                                color = RosaOscuro
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(15.dp)
                            )

                            Text(
                                text = "Cargando eventos...",
                                color =
                                    TextoSecundario
                            )
                        }
                    }
                }


                error != null -> {

                    Text(
                        text = "Error: $error",
                        color = Color.Red
                    )
                }


                eventos.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "📭",
                                fontSize = 50.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Text(
                                text =
                                    "No hay eventos registrados.",
                                fontSize = 17.sp,
                                color =
                                    TextoSecundario
                            )
                        }
                    }
                }


                else -> {

                    LazyColumn(

                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(18.dp)
                    ) {

                        items(eventos) { evento ->


                            // =============================================
                            // TARJETA DEL EVENTO
                            // =============================================

                            Card(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(24.dp),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Tarjeta
                                    ),

                                elevation =
                                    CardDefaults.cardElevation(
                                        defaultElevation =
                                            5.dp
                                    )
                            ) {

                                Column(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp)
                                ) {


                                    // -------------------------------------
                                    // TÍTULO
                                    // -------------------------------------

                                    Text(
                                        text =
                                            evento["titulo"]
                                                ?: "",

                                        fontSize =
                                            22.sp,

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            RosaOscuro
                                    )


                                    Spacer(
                                        modifier =
                                            Modifier.height(8.dp)
                                    )


                                    // -------------------------------------
                                    // DESCRIPCIÓN
                                    // -------------------------------------

                                    Text(
                                        text =
                                            evento["descripcion"]
                                                ?: "",

                                        fontSize =
                                            16.sp,

                                        color =
                                            TextoPrincipal
                                    )


                                    Spacer(
                                        modifier =
                                            Modifier.height(16.dp)
                                    )


                                    // -------------------------------------
                                    // INFORMACIÓN
                                    // -------------------------------------

                                    Text(
                                        text =
                                            "📅  ${evento["fecha"]}",

                                        fontSize =
                                            15.sp,

                                        color =
                                            TextoSecundario
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(6.dp)
                                    )

                                    Text(
                                        text =
                                            "📍  ${evento["lugar"]}",

                                        fontSize =
                                            15.sp,

                                        color =
                                            TextoSecundario
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(6.dp)
                                    )

                                    Text(
                                        text =
                                            "🏷️  ${evento["categoria"]}",

                                        fontSize =
                                            15.sp,

                                        color =
                                            TextoSecundario
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// =========================================================
// REGISTRAR EVENTO
// =========================================================

@Composable
fun RegistrarEventoScreen(
    onRegresar: () -> Unit
) {

    val context = LocalContext.current


    var titulo by remember {
        mutableStateOf("")
    }


    var descripcion by remember {
        mutableStateOf("")
    }


    var fecha by remember {
        mutableStateOf("")
    }


    var lugar by remember {
        mutableStateOf("")
    }


    var categoria by remember {
        mutableStateOf("")
    }


    var guardando by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // DISEÑO
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 30.dp
                )
        ) {


            // =================================================
            // ENCABEZADO
            // =================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "📅",
                    fontSize = 34.sp
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text = "Nuevo evento",
                        fontSize = 28.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = RosaOscuro
                    )

                    Text(
                        text =
                            "Registra una actividad comunitaria",
                        fontSize = 14.sp,
                        color =
                            TextoSecundario
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // =================================================
            // BOTÓN REGRESAR
            // =================================================

            Button(

                onClick = onRegresar,

                modifier =
                    Modifier.height(48.dp),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFFE8DDE0),
                        contentColor =
                            TextoPrincipal
                    )
            ) {

                Text(
                    text = "← Regresar",
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // =================================================
            // FORMULARIO
            // =================================================

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {


                item {

                    OutlinedTextField(

                        value = titulo,

                        onValueChange = {
                            titulo = it
                        },

                        label = {
                            Text("Título del evento")
                        },

                        placeholder = {
                            Text(
                                "Ejemplo: Feria de la comunidad"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        singleLine = true
                    )
                }


                item {

                    OutlinedTextField(

                        value = descripcion,

                        onValueChange = {
                            descripcion = it
                        },

                        label = {
                            Text("Descripción")
                        },

                        placeholder = {
                            Text(
                                "Describe brevemente el evento"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        minLines = 3
                    )
                }


                item {

                    OutlinedTextField(

                        value = fecha,

                        onValueChange = {
                            fecha = it
                        },

                        label = {
                            Text("Fecha")
                        },

                        placeholder = {
                            Text("Ejemplo: 30/08/2026")
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        singleLine = true
                    )
                }


                item {

                    OutlinedTextField(

                        value = lugar,

                        onValueChange = {
                            lugar = it
                        },

                        label = {
                            Text("Lugar")
                        },

                        placeholder = {
                            Text(
                                "Ejemplo: Parque central"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        singleLine = true
                    )
                }


                item {

                    OutlinedTextField(

                        value = categoria,

                        onValueChange = {
                            categoria = it
                        },

                        label = {
                            Text("Categoría")
                        },

                        placeholder = {
                            Text(
                                "Ejemplo: Evento comunitario"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        singleLine = true
                    )
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    // =================================================
                    // GUARDAR
                    // =================================================

                    Button(

                        onClick = {

                            if (
                                titulo.isBlank() ||
                                descripcion.isBlank() ||
                                fecha.isBlank() ||
                                lugar.isBlank() ||
                                categoria.isBlank()
                            ) {

                                Toast.makeText(
                                    context,
                                    "Completa todos los campos",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }


                            guardando = true


                            val referencia =
                                FirebaseDatabase
                                    .getInstance()
                                    .getReference("eventos")
                                    .push()


                            val evento = mapOf(

                                "titulo" to titulo,

                                "descripcion" to descripcion,

                                "fecha" to fecha,

                                "lugar" to lugar,

                                "categoria" to categoria
                            )


                            referencia
                                .setValue(evento)

                                .addOnSuccessListener {

                                    guardando = false

                                    Toast.makeText(
                                        context,
                                        "Evento registrado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onRegresar()
                                }

                                .addOnFailureListener {

                                    guardando = false

                                    Toast.makeText(
                                        context,
                                        "Error al registrar el evento",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },

                        enabled = !guardando,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    RosaPrincipal,
                                contentColor =
                                    TextoPrincipal
                            )
                    ) {

                        if (guardando) {

                            CircularProgressIndicator(
                                color =
                                    TextoPrincipal
                            )

                        } else {

                            Text(
                                text =
                                    "💾  Guardar evento",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )
                }
            }
        }
    }
}