package pl.exbook.exbook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExbookApplication

fun main(args: Array<String>) {
    runApplication<ExbookApplication>(*args)
}
