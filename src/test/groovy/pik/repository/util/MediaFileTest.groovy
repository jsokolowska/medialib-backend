package pik.repository.util

import org.bouncycastle.crypto.engines.EthereumIESEngine
import spock.lang.Specification

class MediaFileTest extends Specification{

    def "mediaFile"(){
        given:
        def userId = "email@domena"
        def type = "zdjecie"
        def fileId = "idZdjecia"
        def url = "urlZdjecia"
        def displayName = "nazwaZdjecia"
        def size = 14

        when:
        def konstruktor1 = new MediaFile(userId, fileId, type, displayName, url, size)
        def konstruktor2 = new MediaFile(userId, displayName, fileId)

        then:
        konstruktor1
        konstruktor2
    }

    def "get methods"(){
        given:
        def userId = "email@domena"
        def type = "zdjecie"
        def fileId = "idZdjecia"
        def url = "urlZdjecia"
        def displayName = "nazwaZdjecia"
        def size = 14
        def konstruktor1 = new MediaFile(userId, fileId, type, displayName, url, size)
        def konstruktor2 = new MediaFile(userId, displayName, fileId)

        when:
        def userIdGet1 = konstruktor1.getUserId()
        def typeGet1 = konstruktor1.getType()
        def fileIdGet1 = konstruktor1.getFileId()
        def displayNameGet1 = konstruktor1.getDisplayName()
        def sizeGet1 = konstruktor1.getSize()
        def urlGet1 = konstruktor1.getUrl()

        def userIdGet2 = konstruktor2.getUserId()
        def typeGet2 = konstruktor2.getType()
        def fileIdGet2 = konstruktor2.getFileId()
        def displayNameGet2 = konstruktor2.getDisplayName()
        def sizeGet2 = konstruktor2.getSize()
        def urlGet2 = konstruktor2.getUrl()

        then:
        userIdGet1 == userId
        typeGet1 == type
        fileIdGet1 == fileId
        displayNameGet1 ==displayName
        sizeGet1 == size
        urlGet1 == url

        userIdGet2 == userId
        typeGet2 == ""
        fileIdGet2 == fileId
        displayNameGet2 == displayName
        sizeGet2 == 0
        urlGet2 == ""
    }

    def "set methods"(){
        given:
        def userId = "email@domena"
        def type = "zdjecie"
        def fileId = "idZdjecia"
        def url = "urlZdjecia"
        def displayName = "nazwaZdjecia"
        def size = 14
        def konstruktor1 = new MediaFile("prevUserId", fileId, "filmy", "prevName", "prevURL", size)
        def konstruktor2 = new MediaFile("prevUserId", "prevName", fileId)

        when:
        konstruktor1.setUserId(userId)
        konstruktor1.setType(type)
        konstruktor1.setDisplayName(displayName)
        konstruktor1.setUrl(url)

        konstruktor2.setUserId(userId)
        konstruktor2.setType(type)
        konstruktor2.setDisplayName(displayName)
        konstruktor2.setUrl(url)

        then:
        konstruktor1.getUserId() == userId
        konstruktor1.getType() == type
        konstruktor1.getDisplayName() == displayName
        konstruktor1.getUrl() == url

        konstruktor2.getUserId() == userId
        konstruktor2.getType() == type
        konstruktor2.getDisplayName() == displayName
        konstruktor2.getUrl() == url
    }

    def "to String"(){
        given:
        def userId = "email@domena"
        def type = "zdjecie"
        def fileId = "idZdjecia"
        def url = "urlZdjecia"
        def displayName = "nazwaZdjecia"
        def size = 14
        def konstruktor = new MediaFile(userId, fileId, type, displayName, url, size)

        when:
        def napis = konstruktor.toString()

        then:
        napis == "MediaFile{userId='email@domena', type='zdjecie', fileId='idZdjecia', url='urlZdjecia', displayName='nazwaZdjecia', size=14}"
    }

    def "to JSON"(){
        given:
        def userId = "email@domena"
        def type = "zdjecie"
        def fileId = "idZdjecia"
        def url = "urlZdjecia"
        def displayName = "nazwaZdjecia"
        def size = 14
        def konstruktor = new MediaFile(userId, fileId, type, displayName, url, size)

        when:
        def napis = konstruktor.toJson()

        then:
        napis == "{ \"displayName\":\"nazwaZdjecia\",\"fileId\":\"idZdjecia\"}"
    }
}
