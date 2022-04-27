package pl.exbook.exbook.util.retrofit

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitService(val serviceName: String)
