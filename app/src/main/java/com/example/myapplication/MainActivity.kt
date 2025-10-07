package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val activity = (LocalActivity.current as? Activity)

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                navController = navController,
                onExitClick = { activity?.finish() }
            )
        }
        composable("login_screen") { TelaDeLogin(navController = navController) }
        composable("register_screen") { TelaDeRegistro(navController = navController) }

        // ROTA ATUALIZADA PARA ACEITAR O ID DO USUÁRIO
        composable(
            route = "maratona_filmes_screen/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            if (userId != -1) {
                TelaMaratonaFilmes(navController = navController, userId = userId)
            }
        }

        composable("fim_de_ano_screen") { FimDeAnoScreen(navController = navController) }
    }
}

@Composable
fun MainScreen(navController: NavController, onExitClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_halloween_pattern),
            contentDescription = "Fundo de abóboras de Halloween",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Maratona de Terror",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.pumpkin),
                contentDescription = "Abóbora de Halloween",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.navigate("login_screen") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Login", color = MaterialTheme.colorScheme.onPrimary) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("register_screen") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Cadastro", color = MaterialTheme.colorScheme.onPrimary) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onExitClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Sair", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}