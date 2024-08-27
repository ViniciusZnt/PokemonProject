package util

//Esse pacote é responsável pelo processso serialização e desserialização personalizado para as classes
//ApiResource e NamedApiResource da PokeAPI


//KSerializer is responsible for the representation of a serial form of a type T in terms of encoders and decoders and for constructing and deconstructing T from/to a sequence of encoding
// primitives. For classes marked with @Serializable, can be obtained from generated companion extension .serializer() or from serializer() function.
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
//Applying Serializable to the Kotlin class instructs the serialization plugin to automatically generate implementation of KSerializer for the current class, that can be used to serialize
// and deserialize the class.
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

@Serializable(with = ApiResourceSerializer::class )
data class ApiResource(val url: String)

@Serializable(with = NamedApiResourceSerializer::class)
data class NamedApiResource(val name: String, val url: String)



object ApiResourceSerializer : KSerializer<ApiResource> {
    // Define a estrutura do JSON para o `ApiResource`
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiResource", PrimitiveKind.STRING)

    // Converte `ApiResource` para uma string (serialização)
    override fun serialize(encoder: Encoder, value: ApiResource) {
        encoder.encodeString(value.url)
    }

    // Converte uma string JSON para `ApiResource` (desserialização)
    override fun deserialize(decoder: Decoder): ApiResource {
        val url = decoder.decodeString()
        return ApiResource(url)
    }
}

object NamedApiResourceSerializer : KSerializer<NamedApiResource> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NamedApiResource"){
        element("name", String.serializer().descriptor)
        element("url", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: NamedApiResource) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeStringElement(descriptor, 1, value.url)
        }
    }

    override fun deserialize(decoder: Decoder): NamedApiResource {
        return decoder.decodeStructure(descriptor) {
            var name: String? = null
            var url: String? = null
            when (decodeElementIndex(descriptor)) {
                0 -> name = decodeStringElement(descriptor, 0)
                1 -> url = decodeStringElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> throw SerializationException("Unexpected end of input")
                else -> throw SerializationException("Unknown index")
            }
            NamedApiResource(
                name ?: throw  SerializationException("name"),
                url ?: throw  SerializationException("url")
            )
        }
    }
}
