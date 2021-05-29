pipeline{
	agent any

    tools {
        maven 'M3'
    }

	stages {
		stage('Build jar') {
			steps {
			   script {
			            def pom = readMavenPom file: 'pom.xml'
                        // replace last number in version with Jenkins build number
                        version = pom.version.replace("0-SNAPSHOT", "${currentBuild.number}")

                        sh "mvn versions:set -DnewVersion=${version}"
			            withMaven(maven: 'M3', mavenSettingsConfig: 'mvn-setting-xml') {
                          		sh "mvn clean compile"
                      	}
			}    }
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
		stage('Docker build'){
            steps{
                sh "mvn package"
                sh "docker build -t medialib/backend:${currentBuild.number} ."
                sh "docker tag medialib/backend:${currentBuild.number} medialib/backend:latest"
                sh "docker images"
            }
		}
		stage('Docker deploy'){
		    steps{
		        //sh "docker container stop medialibbackend"
		        //sh "docker container rm medialibbackend"
                sh "docker run --name=medialibbackend --network=host -d medialib/backend"
		    }
		}
		stage('Deploy to nexus'){
        			steps{
        			    withMaven(maven: 'M3', mavenSettingsConfig: 'mvn-setting-xml') {
                             sh "mvn jar:jar deploy:deploy"
                        }
        			}
        }
	}
}
