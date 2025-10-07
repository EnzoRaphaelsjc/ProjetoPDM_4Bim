package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMaratonaFilmes(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val dbAjudante = remember { DatabaseAjudante(context) }
    var listaDeFilmes by remember { mutableStateOf(emptyList<Filme>()) }
    var mostrarDialogAdicionar by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userId) {
        listaDeFilmes = dbAjudante.getFilmes(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_halloween_pattern),
            contentDescription = "Fundo de abóboras de Halloween",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            floatingActionButton = {
                Column(horizontalAlignment = Alignment.End) {
                    FloatingActionButton(
                        onClick = { mostrarDialogAdicionar = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) { Text("+", style = MaterialTheme.typography.headlineMedium) }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = { navController.navigate("fim_de_ano_screen") },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QuestionMark,
                            contentDescription = "Ajuda/Informações",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    "Lista de filmes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listaDeFilmes) { filme ->
                        ItemFilme(
                            filme = filme,
                            aoAtualizar = { filmeAtualizado ->
                                dbAjudante.atualizarFilmeComoAssistido(filmeAtualizado.id, filmeAtualizado.assistido, filmeAtualizado.nota)
                                listaDeFilmes = dbAjudante.getFilmes(userId)
                            },
                            aoExcluir = { idDoFilme ->
                                dbAjudante.deletarFilme(idDoFilme)
                                listaDeFilmes = dbAjudante.getFilmes(userId)
                                Toast.makeText(context, "Filme excluído!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogAdicionar) {
        DialogAdicionarFilme(
            aoDispensar = { mostrarDialogAdicionar = false },
            aoAdicionar = { titulo, ano, tags, dataPlanejada, imageUri ->
                dbAjudante.adicionarFilme(titulo, ano, tags, dataPlanejada, imageUri, userId)
                listaDeFilmes = dbAjudante.getFilmes(userId)
                mostrarDialogAdicionar = false
                Toast.makeText(context, "Filme adicionado!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ItemFilme(
    filme: Filme,
    aoAtualizar: (Filme) -> Unit,
    aoExcluir: (Int) -> Unit
) {
    var mostrarDialogNota by remember { mutableStateOf(false) }
    var mostrarDialogExcluir by remember { mutableStateOf(false) }

    if (mostrarDialogNota) {
        DialogoNotaFilme(
            aoDispensar = { mostrarDialogNota = false },
            aoConfirmar = { nota ->
                aoAtualizar(filme.copy(assistido = true, nota = nota))
                mostrarDialogNota = false
            }
        )
    }

    if (mostrarDialogExcluir) {
        DialogoConfirmarExclusao(
            aoDispensar = { mostrarDialogExcluir = false },
            aoConfirmar = {
                aoExcluir(filme.id)
                mostrarDialogExcluir = false
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .alpha(if (filme.assistido) 0.7f else 1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (filme.assistido) 2.dp else 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = Uri.parse(filme.imageUri ?: ""),
                contentDescription = "Pôster do filme ${filme.titulo}",
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.pumpkin)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(filme.titulo, style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface))
                Text("Ano: ${filme.ano} | Gênero: ${filme.tags}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)))
                Text("Planejado para: ${filme.dataPlanejada}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)))
                if (filme.assistido) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "ASSISTIDO! - Nota: ${filme.nota}/10",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(
                    checked = filme.assistido,
                    onCheckedChange = { novoEstado ->
                        if (novoEstado) {
                            mostrarDialogNota = true
                        } else {
                            aoAtualizar(filme.copy(assistido = false, nota = 0))
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                IconButton(onClick = { mostrarDialogExcluir = true }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "Excluir filme",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAdicionarFilme(aoDispensar: () -> Unit, aoAdicionar: (String, Int, String, String, String?) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var dataPlanejada by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val seletorDeImagemLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
        }
    }
    val estadoDatePicker = rememberDatePickerState()
    var mostrarDatePicker by remember { mutableStateOf(false) }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    estadoDatePicker.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dataPlanejada = sdf.format(Date(millis))
                    }
                    mostrarDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                Button(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoDatePicker)
        }
    }

    AlertDialog(
        onDismissRequest = aoDispensar,
        title = { Text("Adicionar Novo Filme") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = ano, onValueChange = { ano = it }, label = { Text("Ano de Lançamento") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Gêneros (ex: slasher)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dataPlanejada,
                    onValueChange = {},
                    label = { Text("Data Planejada") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = "Selecionar Data",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { seletorDeImagemLauncher.launch("image/*") }) {
                    Text("Selecionar Imagem")
                }
                imageUri?.let {
                    AsyncImage(model = it, contentDescription = "Imagem selecionada", modifier = Modifier.size(100.dp).padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = { aoAdicionar(titulo, ano.toIntOrNull() ?: 0, tags, dataPlanejada, imageUri?.toString()) }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = aoDispensar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoNotaFilme(
    aoDispensar: () -> Unit,
    aoConfirmar: (Int) -> Unit
) {
    var textoNota by remember { mutableStateOf("") }
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = aoDispensar,
        title = { Text("Avalie o Filme") },
        text = {
            OutlinedTextField(
                value = textoNota,
                onValueChange = { textoNota = it },
                label = { Text("Nota (0 a 10)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = {
                val nota = textoNota.toIntOrNull()
                if (nota != null && nota in 0..10) {
                    aoConfirmar(nota)
                } else {
                    Toast.makeText(context, "Por favor, insira uma nota válida de 0 a 10.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = aoDispensar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoConfirmarExclusao(
    aoDispensar: () -> Unit,
    aoConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = aoDispensar,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza de que deseja excluir este filme da sua lista?") },
        confirmButton = {
            Button(
                onClick = aoConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            Button(onClick = aoDispensar) {
                Text("Cancelar")
            }
        }
    )
}