package pik.repository.oauth

import spock.lang.Specification

class LoginUsersTest extends Specification{

    def "notGetToken"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        def user = users.getToken(email)

        then:
        user == ""
    }

    def "getToken"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        users.addUser(email);
        def user = users.getToken(email)

        then:
        user != ""
    }

    def "notCheckUserToken"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        users.addUser(email);
        def user = users.checkUser(email, "")

        then:
        !user
    }

    def "checkUserToken"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        users.addUser(email);
        def user = users.checkUser(email, users.getToken(email))

        then:
        user
    }

    def "notCheckUser"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        def user = users.checkUser(email)

        then:
        !user
    }

    def "checkUser"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        users.addUser(email)
        def user = users.checkUser(email)

        then:
        user
    }

    def "deleteToken"(){
        given:
        def email = "ala@wp.pl"
        def users = new LoginUsers()

        when:
        users.addUser(email)
        users.deleteUser(email)
        def user = users.getToken(email)

        then:
        user == ""
    }
}
