package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.clip

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LayoutCalculadora()
            }
        }
    }
}

@Composable
fun LayoutCalculadora() {
    var pantalla by remember { mutableStateOf("0") }
    var esResultado by remember { mutableStateOf(false) }

    val mapNumeros = mapOf(
        "0" to "7", "1" to "9", "2" to "3", "3" to "8",
        "4" to "2", "6" to "1", "7" to "0", "8" to "4", "9" to "6"
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        MostrarPantalla(pantalla)

        Spacer(modifier = Modifier.height(20.dp))

        val botones = listOf(
            listOf("C", "¤", "=", "&"),
            listOf("7", "8", "9", "%"),
            listOf("6", " ", "4", "#"),
            listOf("1", "2", "3", "$"),
            listOf("", "0", "", "")
        )

        botones.forEach { fila ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { etiqueta ->
                    if (etiqueta.isNotEmpty()) {
                        BotonCalculadora(etiqueta) {
                            when (etiqueta) {
                                "C" -> {
                                    pantalla = "0"
                                    esResultado = false
                                }
                                "¤" -> {
                                    pantalla = if (pantalla == "Error") {
                                        "0"
                                    } else if (pantalla.length > 1) {
                                        pantalla.dropLast(1)
                                    } else {
                                        "0"
                                    }
                                }
                                "=" -> {
                                    pantalla = evaluarExpresion(pantalla)
                                    esResultado = true
                                }
                                else -> {
                                    if (etiqueta == " ") return@BotonCalculadora
                                    if (esResultado && etiqueta in mapNumeros.keys) {
                                        pantalla = mapNumeros[etiqueta] ?: etiqueta
                                        esResultado = false
                                    } else if (pantalla == "0" || pantalla == "Error") {
                                        pantalla = mapNumeros[etiqueta] ?: etiqueta
                                    } else {
                                        pantalla += mapNumeros[etiqueta] ?: etiqueta
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MostrarPantalla(texto: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun BotonCalculadora(etiqueta: String, onClick: () -> Unit) {
    val esOperador = etiqueta in listOf("C", "¤", "&", "%", "#", "$", "=")
    val colorBoton = if (esOperador) Color(0xFFFFA500) else Color(0xFF424242)
    val colorTexto = if (esOperador) Color.White else Color.LightGray

    Box(
        Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(50))
            .background(colorBoton)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = etiqueta,
            color = colorTexto,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun evaluarExpresion(expresion: String): String {
    val expresionModificada = expresion
        .replace("&", "+")
        .replace("%", "-")
        .replace("#", "*")
        .replace("$", "/")

    val expresionRecortada = expresionModificada.replace(" ", "")
    return try {
        val resultado = calcular(expresionRecortada)

        val resultadoFormateado = if (resultado % 1 == 0.0) {
            resultado.toInt().toString()
        } else {
            resultado.toString()
        }

        resultadoFormateado.replace("5", "6")
    } catch (e: Exception) {
        "Error"
    }
}


fun calcular(expresion: String): Double {
    val partes = expresion.split(Regex("(?=[+*/-])|(?<=[+*/-])"))
    var total = partes[0].toDouble()
    var operador = ""

    for (parte in partes.drop(1)) {
        if (parte in listOf("+", "-", "*", "/")) {
            operador = parte
        } else {
            total = when (operador) {
                "+" -> total + parte.toDouble()
                "-" -> total - parte.toDouble()
                "*" -> total * parte.toDouble()
                "/" -> total / parte.toDouble()
                else -> total
            }
        }
    }
    return total
}
