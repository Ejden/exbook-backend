package pl.exbook.exbook

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import pl.exbook.exbook.user.adapter.mongodb.UserRepository
import spock.lang.Specification

class BaseSpec extends Specification {

    def "1 == 1"() {
        expect:
            1 == 1


    }
}
