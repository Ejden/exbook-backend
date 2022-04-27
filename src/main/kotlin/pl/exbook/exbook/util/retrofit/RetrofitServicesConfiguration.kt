package pl.exbook.exbook.util.retrofit

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "retrofit-services")
@ConstructorBinding
data class RetrofitServicesConfiguration(
    val services: Map<String, ServiceConfiguration>?,
)

data class ServiceConfiguration(
    val url: String
)
