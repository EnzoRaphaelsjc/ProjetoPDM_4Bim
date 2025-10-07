package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

data class Filme(
    val id: Int,
    val titulo: String,
    val ano: Int,
    val tags: String,
    val dataPlanejada: String,
    val assistido: Boolean,
    val nota: Int,
    val imageUri: String?
)

class DatabaseAjudante(context: Context) :
    SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_BANCO) {

    companion object {
        private const val VERSAO_BANCO = 4
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
        private const val IMAGE_URI = "image_uri"
        private const val ID_USUARIO_FK = "id_usuario"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val queryCriarTabelaUsuarios = ("CREATE TABLE " + TABELA_USUARIOS + "("
                + ID_USUARIO + " INTEGER PRIMARY KEY," + EMAIL + " TEXT UNIQUE,"
                + SENHA + " TEXT" + ")")
        db?.execSQL(queryCriarTabelaUsuarios)

        val queryCriarTabelaFilmes = ("CREATE TABLE " + TABELA_FILMES + "("
                + ID_FILME + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITULO + " TEXT NOT NULL,"
                + ANO + " INTEGER,"
                + TAGS + " TEXT,"
                + DATA_PLANEJADA + " TEXT,"
                + ASSISTIDO + " INTEGER DEFAULT 0,"
                + NOTA + " INTEGER CHECK($NOTA BETWEEN 0 AND 10),"
                + IMAGE_URI + " TEXT,"
                + ID_USUARIO_FK + " INTEGER,"
                + "FOREIGN KEY($ID_USUARIO_FK) REFERENCES $TABELA_USUARIOS($ID_USUARIO)" + ")")
        db?.execSQL(queryCriarTabelaFilmes)
    }

    override fun onUpgrade(db: SQLiteDatabase?, versaoAntiga: Int, versaoNova: Int) {
        if (versaoAntiga < 2) {
            // (código para criar a tabela de filmes se viesse de uma versão muito antiga)
        }
        if (versaoAntiga < 3) {
            db?.execSQL("ALTER TABLE $TABELA_FILMES ADD COLUMN $IMAGE_URI TEXT")
        }
        if (versaoAntiga < 4) {
            db?.execSQL("ALTER TABLE $TABELA_FILMES ADD COLUMN $ID_USUARIO_FK INTEGER")
        }
    }

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

    fun checkUser(email: String, password: String): Int? {
        val db = this.readableDatabase
        val hashedPassword = hashPassword(password)
        val cursor = db.rawQuery(
            "SELECT $ID_USUARIO FROM $TABELA_USUARIOS WHERE $EMAIL = ? AND $SENHA = ?",
            arrayOf(email, hashedPassword)
        )
        var userId: Int? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(ID_USUARIO))
        }
        cursor.close()
        db.close()
        return userId
    }

    fun adicionarFilme(titulo: String, ano: Int, tags: String, dataPlanejada: String, imageUri: String?, userId: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TITULO, titulo)
            put(ANO, ano)
            put(TAGS, tags)
            put(DATA_PLANEJADA, dataPlanejada)
            put(IMAGE_URI, imageUri)
            put(ID_USUARIO_FK, userId)
        }
        val success = db.insert(TABELA_FILMES, null, values)
        db.close()
        return success != -1L
    }

    fun getFilmes(userId: Int, filtroTag: String? = null): List<Filme> {
        val listaDeFilmes = mutableListOf<Filme>()
        val query = "SELECT * FROM $TABELA_FILMES WHERE $ID_USUARIO_FK = ?"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(query, arrayOf(userId.toString()))

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

    fun deletarFilme(id: Int) {
        val db = this.writableDatabase
        db.delete(TABELA_FILMES, "$ID_FILME = ?", arrayOf(id.toString()))
        db.close()
    }
}