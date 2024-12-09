package us.aldwin.ambient.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * JSON handling for Ambient APIs
 */
@Suppress("DuplicatedCode")
public object AmbientJson {
    /**
     * A Jackson [ObjectMapper] configured to handle Ambient JSON.
     */
    @JvmStatic
    public val mapper: ObjectMapper by lazy {
        ObjectMapper().apply {
            setDefaultPrettyPrinter(
                DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                },
            )
            enable(SerializationFeature.INDENT_OUTPUT)

            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            registerModule(JavaTimeModule())
            registerKotlinModule()

            enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
        }
    }
}
