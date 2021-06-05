package pik.repository.util

import pik.repository.util.User
import spock.lang.Specification

class UserTest extends Specification{

    def "userData"(){
        given:
        def stringEmail = "login@domena"
        def stringName = "nameUser"
        def stringSurname = "surnameUser"

        when:
        def user = new User(stringEmail, stringName, stringSurname)

        then:
        user
    }

    def "get methods"(){
        given:
        def stringEmail = "login@domena"
        def stringName = "nameUser"
        def stringSurname = "surnameUser"
        def user = new User(stringEmail, stringName, stringSurname)

        when:
        def getStringEmail = user.getEmail()
        def getStringName = user.getName()
        def getStringSurname = user.getSurname()

        then:
        getStringEmail == stringEmail
        getStringName == stringName
        getStringSurname == stringSurname
    }

    def "set methods"(){
        given:
        def stringEmail = "login@domena"
        def stringName = "nameUser"
        def stringSurname = "surnameUser"
        def user = new User("prevEmail@domena", "prevName", "prevSurname")

        when:
        user.setEmail(stringEmail)
        user.setName(stringName)
        user.setSurname(stringSurname)

        then:
        user.getEmail() == stringEmail
        user.getName() == stringName
        user.getSurname() == stringSurname
    }
}
