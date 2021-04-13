pipeline{
	agent any
	def pom = readMavenPom file: 'pom.xml'
    // replace last number in version with Jenkins build number
    def version = pom.version.replace("SNAPSHOT", ".${currentBuild.number}")

    tools {
        maven 'M3'
    }

	stages {
		stage('Build') {
			steps {
				sh "mvn clean compile -Dversion=${version}"
			}
		}
		stage('Test') {
			steps {
				sh "mvn test"
			}
		}
		stage('SonarQube analysis') {
			steps {
				withSonarQubeEnv('sonar-server') {
					sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
				}
			}
		}
		stage('Deploy to nexus') {
        	steps {
        		withMaven(maven: 'M3', mavenSettingsConfig: 'mvn-setting-xml') {
              		sh "mvn jar:jar deploy:deploy"
          		}
    		}
        }
		stage('Deploy') {
			steps {
				sh "mvn heroku:deploy"
			}
		}
	}
}
