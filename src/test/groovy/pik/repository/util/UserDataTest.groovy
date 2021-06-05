package pik.repository.util

import pik.repository.util.UserData
import spock.lang.Specification

class UserDataTest extends Specification{

    def "userData"(){
        given:
            def stringEmail = "login@domena"
            def stringPassword = "passwordUser"
            def stringName = "nameUser"
            def stringSurname = "surnameUser"

        when:
            def userData = new UserData(stringEmail, stringPassword, stringName, stringSurname)

        then:
            userData
    }

    def "get methods"(){
        given:
        def stringEmail = "login@domena"
        def stringPassword = "passwordUser"
        def stringName = "nameUser"
        def stringSurname = "surnameUser"
        def userData = new UserData(stringEmail, stringPassword, stringName, stringSurname)

        when:
        def getStringEmail = userData.getEmail()
        def getStringPassword = userData.getPassword()
        def getStringName = userData.getName()
        def getStringSurname = userData.getSurname()

        then:
        getStringEmail == stringEmail
        getStringPassword == stringPassword
        getStringName == stringName
        getStringSurname == stringSurname
    }

    def "set methods"(){
        given:
        def stringEmail = "login@domena"
        def stringPassword = "passwordUser"
        def stringName = "nameUser"
        def stringSurname = "surnameUser"
        def userData = new UserData("prevEmail@domena", "prevPassword", "prevName", "prevSurname")

        when:
        userData.setEmail(stringEmail)
        userData.setPassword(stringPassword)
        userData.setName(stringName)
        userData.setSurname(stringSurname)

        then:
        userData.getEmail() == stringEmail
        userData.getPassword() == stringPassword
        userData.getName() == stringName
        userData.getSurname() == stringSurname
    }
}
