package pl.exbook.exbook

import spock.lang.Specification

class BaseIntegrationSpec extends Specification {

    def "1 should equal 1"() {
        expect:
            1 == 1
    }
}
