package pik.repository.mysqlDAO


import pik.repository.mysqlDAOs.UserDAO
import spock.lang.Specification

class UserDAOSpec extends Specification {

    def "Should return proper name"() {
        given:
        def userDao = new UserDAO()

        when:
        def name = userDao.getUserName("ptoplis0@home.pl")

        then:
        name == "Peggie"

    }

    def "Creating user test"() {
        given:
        def userDao = new UserDAO()
        def initialCount = userDao.getCount()

        when:
        userDao.insertUser(null, "Turing", "alan.turing@gmail.com", "ImmaAlan")
        def lastName = userDao.getLastName("alan.turing@gmail.com")
        def modifiedCount = userDao.getCount()


        then:
        lastName == "Turing"
        modifiedCount == initialCount + 1

    }

    def "Updating email test"() {
        given:
        def userDao = new UserDAO()
        def initialName = userDao.getUserName("ada.lovelace@gmail.com")


        when:
        userDao.modifyUserEmail("ada.lovelace@gmail.com", "a.da.lovelace@gmail.com")
        def newEmailName = userDao.getUserName("a.da.lovelace@gmail.com")

        then:
        initialName == newEmailName

    }

    def "modify user back"() {
        given:
        def userDao = new UserDAO()
        def initialName = userDao.getUserName("a.da.lovelace@gmail.com")

        when:
        userDao.modifyUserEmail("a.da.lovelace@gmail.com", "ada.lovelace@gmail.com")
        def newEmailName = userDao.getUserName("ada.lovelace@gmail.com")

        then:
        initialName == newEmailName
    }

    def "Deleting test"() {
        given:
        def userDao = new UserDAO()
        def initialCount = userDao.getCount()

        when:
        userDao.deleteUser("alan.turing@gmail.com")
        def username = userDao.getUserName("alan.turing@gmail.com")
        def newCount = userDao.getCount()

        then:
        username == null
        newCount == initialCount - 1

    }

    def "Test password matching"() {
        given:
        def userDao = new UserDAO()
        def userPasswHash = userDao.getPasswordHash("bfend9@gmpg.org")
        def userSalt = userDao.getSalt("bfend9@gmpg.org")

        when:
        def userPasswStuff = userDao.hashPassword("77Ila32PlRdi", userSalt)

        then:
        System.out.println(userSalt)
        userPasswStuff.get("passwHash") == userPasswHash
        userPasswStuff.get("salt") == userSalt
    }

    def "Test password matching refactorized"() {
        given:
        def userDao = new UserDAO()

        when:
        def result = userDao.isPasswordMatch("bfend9@gmpg.org", "77Ila32PlRdi")

        then:
        result == true
    }

    def "Test all users info"() {
        given:
        def userDao = new UserDAO()

        when:
        def result = userDao.getAllUserInfo("bfend9@gmpg.org")

        then:
        result.getEmail() == "bfend9@gmpg.org"
        result.getName() == "Bartolomeo"
        result.getSurname() == "Fend"
    }

    def "Test user existance"() {
        given:
        def userDao = new UserDAO()

        when:
        def resultExists = userDao.isUserExist("gscoyles3@blogger.com")
        def resultDoesntExist = userDao.isUserExist("ronnyosullivan@logger.com")

        then:
        resultExists
        !resultDoesntExist
    }

    def "Test full modification user"() {

        given:
        def userDao = new UserDAO()
        def initialEmail = "jriddoch7@twitter.com"
        def newEmail = "jriddoch7@gmail.com"
        def newName = "Jonasz"
        def newSurname = "Riddach"

        when:
        userDao.modifyAllData(initialEmail, newName, newSurname, newEmail)
        def assignedName = userDao.getUserName(newEmail)
        def assignedSurname = userDao.getLastName(newEmail)


        then:
        assignedName == newName
        assignedSurname == newSurname

    }
}
