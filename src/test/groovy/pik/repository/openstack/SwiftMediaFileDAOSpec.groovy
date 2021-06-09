package pik.repository.openstack

import pik.repository.util.MediaFile
import pik.repository.util.MetadataChange
import spock.lang.Specification


class SwiftMediaFileDAOSpec extends Specification {
    def swiftDAO = new SwiftMediaFileDAO(true)
    def username = "testuser"
    def resDir = "src/test/resources/objects/"

    def "Should add different types of resources"(){
        given:
        def mediaFile = new MediaFile(username, name, name)
        def storedObj

        when:
        swiftDAO.uploadMediaFile(mediaFile, new File(resDir + name))
        storedObj = swiftDAO.getMediaFile(username, name)

        then:
        storedObj != null

        where:
        name <<["test-gif.gif",
                "test-img1.jpg",
                "test-img2.jpeg",
                "test-img3.png",
                "test-video.mp4"]
    }

    def uploadAllFiles(String [] filenames){
        for (int i=0; i<filenames.size(); i++){
            def medium = new MediaFile(username, filenames[i], filenames[i])
            def file = new File(resDir + filenames[i])
            swiftDAO.uploadMediaFile(medium, file)
        }
    }

    def "Should list all objects in the container" (){
        given: "some sample files"
        String [] names= ["test-gif.gif", "test-img1.jpg", "test-img2.jpeg"]
        uploadAllFiles(names)
        def files_listed

        when: "all files by this user are queried"
        files_listed = swiftDAO.getAllByUser(username)

        then: "list should contain files with matching file ids"
        files_listed.size() == 3
        assert files_listed.find {it -> it.getFileId() == names[0]}
        assert files_listed.find {it -> it.getFileId() == names[1]}
        assert files_listed.find {it -> it.getFileId() == names[2]}
        files_listed.each {
            print(it.toString())
        }
    }

    def "Should allow for display name to change"(){
        //works with non-mock swift dao because of implementation bug that does not refresh metadata
        given:
        def swiftDAO = new SwiftMediaFileDAO(false)
        def fileId = "test-img1.jpg"
        def displayName = "displayName"
        swiftDAO.uploadMediaFile(new MediaFile(username, fileId, fileId), new File(resDir + fileId))
        def mediaFile = swiftDAO.getMediaFile(username, fileId)

        when:
        mediaFile.setDisplayName(displayName)
        swiftDAO.updateMediaFile(username, fileId, new MetadataChange(displayName))
        def result = swiftDAO.getMediaFile(username, fileId)

        then:
        result.getDisplayName() == displayName
        print(result)
    }

    def displayNameChangeSetup(String [] filenames, String [] displayNames){
        for (int i=0; i<filenames.size(); i++){
            def medium = new MediaFile(username, displayNames[i], filenames[i])
            def file = new File(resDir + filenames[i])
            swiftDAO.uploadMediaFile(medium, file)
        }
    }

    def "Should allow to find file by part of its display name"(){
        given:
        String [] names= ["test-gif.gif", "test-img1.jpg", "test-img2.jpeg"]
        String [] displayNames = ["how is display1", "display2 is thee", "not"]
        displayNameChangeSetup(names, displayNames)

        expect:
        swiftDAO.getAllContaining( username, "display").size() == 2
    }

    def "Should allow to find file by exactly its display name"(){
        given:
        String [] names= ["test-gif.gif", "test-img1.jpg", "test-img2.jpeg"]
        String [] displayNames = ["how is display1", "display2 is thee", "not"]
        displayNameChangeSetup(names, displayNames)

        expect:
        displayNames.each {
            swiftDAO.getMediaFileByDisplayName(username, it) != null
        }
    }

    def "Should list all images in container"(){
        given:
        String [] names= ["test-gif.gif", "test-img1.jpg", "test-img2.jpeg","test-img3.png", "test-video.mp4"]
        uploadAllFiles(names)

        when:
        def list = swiftDAO.getAllByUserAndType(username, "image")

        then:
        list.size() == 4
        list.find {it -> it.getFileId() == names[0]}
        list.find {it -> it.getFileId() == names[1]}
        list.find {it -> it.getFileId() == names[2]}
        list.find {it -> it.getFileId() == names[3]}

    }

    def "Should list all videos in container"(){
        given:
        String [] names= ["test-gif.gif", "test-img1.jpg", "test-img2.jpeg","test-img3.png", "test-video.mp4"]
        uploadAllFiles(names)

        when:
        def list = swiftDAO.getAllByUserAndType(username, "video")

        then:
        list.size() == 1
        list.find {it -> it.getFileId() == names[4]}

    }

    def "Should delete files"(){
        given:
        String [] names= ["test-img1.jpg", "test-img2.jpeg"]
        uploadAllFiles(names)

        when:
        swiftDAO.deleteMediaFile(username, names[1])
        def list = swiftDAO.getAllByUserAndType(username, "any")

        then:
        list.size() == 1
        list[0].getFileId() == names[0]
    }

    def "Should check if file exists"(){
        given:
        String [] names= ["test-img1.jpg", "test-img2.jpeg"]
        uploadAllFiles(names)

        expect:
        names.each {
            swiftDAO.exists(username, it)
        }
        !swiftDAO.exists(username, "some random name")
    }

    def "Should find media files by their display name"(){

    }

    def cleanup(){
        def files_listed = swiftDAO.getAllByUser(username)
        files_listed.each {
            swiftDAO.deleteMediaFile(username, it.getFileId())
        }
    }
}
