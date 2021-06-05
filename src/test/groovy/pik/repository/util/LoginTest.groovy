package pik.repository.util

import pik.repository.util.Login
import spock.lang.Specification

class LoginTest extends Specification{
    def "login"(){
        given:
        def stringEmail = "login@domena"
        def stringPassword = "passwordUser"

        when:
        def user = new Login(stringEmail, stringPassword)

        then:
        user
    }

    def "get methods"(){
        given:
        def stringEmail = "login@domena"
        def stringPassword = "passwordUser"
        def user = new Login(stringEmail, stringPassword)

        when:
        def getStringEmail = user.getEmail()
        def getStringPassword = user.getPassword()

        then:
        getStringEmail == stringEmail
        getStringPassword == stringPassword
    }

    def "set methods"(){
        given:
        def stringEmail = "login@domena"
        def stringPassword = "passwordUser"
        def user = new Login("prevEmail@domena", "prevPassword")

        when:
        user.setEmail(stringEmail)
        user.setPassword(stringPassword)

        then:
        user.getEmail() == stringEmail
        user.getPassword() == stringPassword
    }
}
