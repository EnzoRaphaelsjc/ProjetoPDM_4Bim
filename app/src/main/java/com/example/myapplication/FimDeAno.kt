package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun FimDeAnoScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_halloween_pattern),
            contentDescription = "Fundo de abóboras de Halloween",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(

                painter = painterResource(id = R.drawable.img),
                contentDescription = "Imagem comemorativa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(400.dp) // Ajuste o tamanho conforme necessário
            )
            // ---------------------------------------------------

            Spacer(modifier = Modifier.height(32.dp))

            // --- CÓDIGO PARA O TEXTO PERSONALIZADO ---
            Text(
                text = "Obrigado pelos 3 anos e por tudo!",
                color = Color.White, // Letra branca, como solicitado
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium // Usa a fonte de Halloween para o título
            )
            // -----------------------------------------

            Spacer(modifier = Modifier.height(48.dp))

            // Botão para voltar para a tela anterior
            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Voltar", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

// Preview para visualização no Android Studio
@Preview(showBackground = true)
@Composable
fun FimDeAnoScreenPreview() {
    MyApplicationTheme(darkTheme = true) {
        FimDeAnoScreen(navController = rememberNavController())
    }
}