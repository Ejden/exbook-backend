package pl.exbook.exbook.util

import mu.KLogger
import java.io.BufferedReader
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.log(using: KLogger) = """
                REQUEST: method = [$method],
                path = [$requestURI],
                parameters = ${parseParameters(parameterMap)},
                body = ${reader.use(BufferedReader::readText)}
            """
    .let { using.info { it } }

private fun parseValue(arg: Any): String {
    if (arg is Array<*>) {
        return arg.joinToString(prefix = "[", postfix = "]", separator = ", ")
    }

    return arg.toString()
}

private fun parseParameters(parameters: Map<String, Array<String>>): String =
    parameters.entries.joinToString(separator = ", ") {"${it.key}=${parseValue(it.value)}" }
