package pl.exbook.exbook.util.mapper

interface ToDomainMapper<X, Y> {
    fun toDomain(from: X): Y
}

interface FromDomainMapper<X, Y> {
    fun fromDomain(from: X): Y
}

interface TwoWayMapper<X, Y, Z, W> : ToDomainMapper<X, Y>, FromDomainMapper<Z, W> {
    override fun toDomain(from: X): Y
    override fun fromDomain(from: Z): W
}
