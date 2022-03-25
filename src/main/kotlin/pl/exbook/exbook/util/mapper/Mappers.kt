package pl.exbook.exbook.util.mapper

interface OneWayMapper<X, Y> {
    fun map(from: X): Y
}

interface TwoWayMapper<X, Y, Z, W> {
    fun toDomain(from: X): Y
    fun fromDomain(from: Z): W
}
