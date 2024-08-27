//Escopo do Projeto: Aplicativo Kotlin Console para Consulta de Pokémon
//
//Objetivo:
//Desenvolver um aplicativo de console em Kotlin que permita ao usuário buscar informações sobre um Pokémon específico
// digitando seu nome.
//O aplicativo consumirá uma API de Pokémon e exibirá os dados do Pokémon, incluindo uma representação em ASCII
// art de sua imagem.

//Ideia para transformar a imagem em ASCC
//enum class Color(val rgb: Int) {                      // 1
//    RED(0xFF0000),                                    // 2
//    GREEN(0x00FF00),
//    BLUE(0x0000FF),
//    YELLOW(0xFFFF00);
//
//    fun containsRed() = (this.rgb and 0xFF0000 != 0)  // 3
//}

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import Models.*
import kotlinx.serialization.modules.SerializersModule
import util.ApiResourceSerializer
import util.NamedApiResourceSerializer


class CLIPokemon {



    //Passa para variavel cliente O handler HTTP padronizado
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                serializersModule = SerializersModule {
                    contextual(ApiResource::class, ApiResourceSerializer)
                    contextual(NamedApiResource::class, NamedApiResourceSerializer)
                }
            })
        }
    }

    fun game() {
        //runBlocking uma função especial usada para iniciar uma corrotina e bloquear
        // o thread atual até que a corrotina complete sua execução.
        runBlocking {
            while (true) {
                println("Você deseja saber sobre as informações de que Pokémon? (Digite o nome do Pokémon)")
                val option = readLine() ?: continue
                if (option.isNotEmpty()) {
                    val result = runCatching {
                        apiConsume(option)  //Aqui se faz relação com a corotine criado com essa função suspend
                    }.onSuccess { pokemon ->
                        println("Nome: ${pokemon.name}")
                        println("ID: ${pokemon.id}")
                        println("Altura: ${pokemon.height}")
                        println("Peso: ${pokemon.weight}")
                        // Adicione aqui outras informações que você queira exibir
                    }.onFailure { error ->
                        println("Erro: ${error.message}")
                    }
                } else {
                    println("Nome do Pokémon não pode estar vazio.")
                }
            }
        }
    }
    //Função suspend: Ela é parecida com async e é usada para definir
    //ações que podem ser pausadas e retomadas
    suspend fun apiConsume(pokeName: String): Pokemon {
        val url = "https://pokeapi.co/api/v2/pokemon/${pokeName.lowercase()}/"
        val response: HttpResponse = client.get(url)
        val jsonString = response.bodyAsText()  // Corrigido
        return Json.decodeFromString<Pokemon>(jsonString)
    }
}

fun main() {
    val cliPokemon = CLIPokemon()
    cliPokemon.game()
}
