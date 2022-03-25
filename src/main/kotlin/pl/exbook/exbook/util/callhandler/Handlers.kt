package pl.exbook.exbook.util.callhandler

import pl.exbook.exbook.util.mapper.TwoWayMapper

inline fun <X, Y, Z, W, R> handleRequest(
    mapper: TwoWayMapper<X, Y, Z, W>,
    requestBody: X,
    call: (command: Y) -> Z,
    response: (result: W) -> R
): R {
    val command = mapper.toDomain(requestBody)
    val callResult = call(command)
    return response(mapper.fromDomain(callResult))
}
