package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

// Classe de dados para representar um Filme
data class Filme(
    val id: Int,
    val titulo: String,
    val ano: Int,
    val tags: String,
    val dataPlanejada: String,
    val assistido: Boolean,
    val nota: Int,
    val imageUri: String? // Campo para o URI da imagem
)

class DatabaseAjudante(context: Context) :
    SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_BANCO) {

    companion object {
        private const val VERSAO_BANCO = 3
        private const val NOME_BANCO = "GerenciadorUsuarios.db"

        // Constantes da Tabela de Usuários
        private const val TABELA_USUARIOS = "usuarios"
        private const val ID_USUARIO = "id"
        private const val EMAIL = "email"
        private const val SENHA = "senha"

        // Constantes da Tabela de Filmes
        private const val TABELA_FILMES = "filmes"
        private const val ID_FILME = "id"
        private const val TITULO = "titulo"
        private const val ANO = "ano"
        private const val TAGS = "tags"
        private const val DATA_PLANEJADA = "data_planejada"
        private const val ASSISTIDO = "assistido"
        private const val NOTA = "nota"
        private const val IMAGE_URI = "image_uri" // Nova coluna
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Cria a tabela de usuários
        val queryCriarTabelaUsuarios = ("CREATE TABLE " + TABELA_USUARIOS + "("
                + ID_USUARIO + " INTEGER PRIMARY KEY," + EMAIL + " TEXT UNIQUE,"
                + SENHA + " TEXT" + ")")
        db?.execSQL(queryCriarTabelaUsuarios)

        // Cria a tabela de filmes
        val queryCriarTabelaFilmes = ("CREATE TABLE " + TABELA_FILMES + "("
                + ID_FILME + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITULO + " TEXT NOT NULL,"
                + ANO + " INTEGER,"
                + TAGS + " TEXT,"
                + DATA_PLANEJADA + " TEXT,"
                + ASSISTIDO + " INTEGER DEFAULT 0,"
                + NOTA + " INTEGER CHECK($NOTA BETWEEN 0 AND 10),"
                + IMAGE_URI + " TEXT" + ")")
        db?.execSQL(queryCriarTabelaFilmes)
    }

    override fun onUpgrade(db: SQLiteDatabase?, versaoAntiga: Int, versaoNova: Int) {
        // Lógica para atualizar o banco de dados sem perder dados
        if (versaoAntiga < 2) {
            val queryCriarTabelaFilmes = ("CREATE TABLE " + TABELA_FILMES + "("
                    + ID_FILME + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TITULO + " TEXT NOT NULL,"
                    + ANO + " INTEGER,"
                    + TAGS + " TEXT,"
                    + DATA_PLANEJADA + " TEXT,"
                    + ASSISTIDO + " INTEGER DEFAULT 0,"
                    + NOTA + " INTEGER CHECK($NOTA BETWEEN 0 AND 10)" + ")")
            db?.execSQL(queryCriarTabelaFilmes)
        }
        if (versaoAntiga < 3) {
            // Adiciona a nova coluna de imagem sem deletar a tabela
            db?.execSQL("ALTER TABLE $TABELA_FILMES ADD COLUMN $IMAGE_URI TEXT")
        }
    }

    // --- Funções de Usuário ---
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun addUser(email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(EMAIL, email)
        contentValues.put(SENHA, hashPassword(password))
        val success = db.insert(TABELA_USUARIOS, null, contentValues)
        db.close()
        return success != -1L
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val hashedPassword = hashPassword(password)
        val cursor = db.rawQuery(
            "SELECT * FROM $TABELA_USUARIOS WHERE $EMAIL = ? AND $SENHA = ?",
            arrayOf(email, hashedPassword)
        )
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    // --- Funções de Filmes ---
    fun adicionarFilme(titulo: String, ano: Int, tags: String, dataPlanejada: String, imageUri: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TITULO, titulo)
            put(ANO, ano)
            put(TAGS, tags)
            put(DATA_PLANEJADA, dataPlanejada)
            put(IMAGE_URI, imageUri)
        }
        val success = db.insert(TABELA_FILMES, null, values)
        db.close()
        return success != -1L
    }

    fun getFilmes(filtroTag: String? = null): List<Filme> {
        val listaDeFilmes = mutableListOf<Filme>()
        val query = if (filtroTag.isNullOrEmpty()) {
            "SELECT * FROM $TABELA_FILMES"
        } else {
            "SELECT * FROM $TABELA_FILMES WHERE $TAGS LIKE ?"
        }
        val db = this.readableDatabase
        val cursor: Cursor? = if (filtroTag.isNullOrEmpty()) {
            db.rawQuery(query, null)
        } else {
            db.rawQuery(query, arrayOf("%$filtroTag%"))
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val filme = Filme(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_FILME)),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow(TITULO)),
                    ano = cursor.getInt(cursor.getColumnIndexOrThrow(ANO)),
                    tags = cursor.getString(cursor.getColumnIndexOrThrow(TAGS)),
                    dataPlanejada = cursor.getString(cursor.getColumnIndexOrThrow(DATA_PLANEJADA)),
                    assistido = cursor.getInt(cursor.getColumnIndexOrThrow(ASSISTIDO)) == 1,
                    nota = cursor.getInt(cursor.getColumnIndexOrThrow(NOTA)),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_URI))
                )
                listaDeFilmes.add(filme)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return listaDeFilmes
    }

    fun atualizarFilmeComoAssistido(id: Int, assistido: Boolean, nota: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ASSISTIDO, if (assistido) 1 else 0)
            put(NOTA, nota)
        }
        db.update(TABELA_FILMES, values, "$ID_FILME = ?", arrayOf(id.toString()))
        db.close()
    }
    // Adicione esta função dentro da classe DatabaseAjudante

    fun deletarFilme(id: Int) {
        val db = this.writableDatabase
        // O método delete remove a linha da tabela onde o ID corresponde
        db.delete(TABELA_FILMES, "$ID_FILME = ?", arrayOf(id.toString()))
        db.close()
    }
}