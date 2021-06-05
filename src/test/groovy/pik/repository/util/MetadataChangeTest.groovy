package pik.repository.util

import spock.lang.Specification

class MetadataChangeTest extends Specification{

    def "metadata change"(){
        given:
        def name = "fileName"
        def description = "opis pliku"

        when:
        def konstruktor1 = new MetadataChange(name, description)
        def konstruktor2 = new MetadataChange(name)

        then:
        konstruktor1
        konstruktor2
    }

    def "get methods"(){
        given:
        def name = "fileName"
        def description = "opis pliku"
        def konstruktor1 = new MetadataChange(name, description)
        def konstruktor2 = new MetadataChange(name)

        when:
        def nameGet1 = konstruktor1.getDisplayName()
        def descGet1 = konstruktor1.getDescription()

        def nameGet2 = konstruktor2.getDisplayName()
        def descGet2 = konstruktor2.getDescription()

        then:
        nameGet1 == name
        descGet1 == description

        nameGet2 == name
        descGet2 == ""
    }

    def "set methods"(){
        given:
        def name = "fileName"
        def description = "opis pliku"
        def konstruktor1 = new MetadataChange("prevName", "prevDesc")
        def konstruktor2 = new MetadataChange("prevName")

        when:
        konstruktor1.setDisplayName(name)
        konstruktor1.setDescription(description)

        konstruktor2.setDisplayName(name)
        konstruktor2.setDescription(description)

        then:
        konstruktor1.getDisplayName() == name
        konstruktor1.getDescription() == description

        konstruktor2.getDisplayName() == name
        konstruktor2.getDescription() == description
    }
}
