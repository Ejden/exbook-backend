package pl.exbook.exbook.util.callhandler

import mu.KLogger
import pl.exbook.exbook.util.log
import pl.exbook.exbook.util.mapper.FromDomainMapper
import pl.exbook.exbook.util.mapper.TwoWayMapper
import javax.servlet.http.HttpServletRequest

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

inline fun <X, Y, Z, W, R> handleRequest(
    mapper: TwoWayMapper<X, Y, Z, W>,
    requestBody: X,
    call: (command: Y) -> Z,
    response: (result: W) -> R,
    request: HttpServletRequest,
    logUsing: KLogger
): R {
    request.log(using = logUsing)
    return handleRequest(mapper, requestBody, call, response)
}

inline fun <X, Y, R> handleRequest(
    mapper: FromDomainMapper<X, Y>,
    call: () -> X,
    response: (result: Y) -> R
): R {
    val callResult = call()
    return response(mapper.fromDomain(callResult))
}

inline fun <X, Y, R> handleRequest(
    mapper: FromDomainMapper<X, Y>,
    call: () -> X,
    response: (result: Y) -> R,
    request: HttpServletRequest,
    logUsing: KLogger
): R {
    request.log(using = logUsing)
    return handleRequest(mapper, call, response)
}
