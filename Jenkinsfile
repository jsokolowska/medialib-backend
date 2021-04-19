pipeline{
	agent any

    tools {
        maven 'M3'
    }

	stages {
		stage('Build') {
			steps {
			script {
			    def pom = readMavenPom file: 'pom.xml'
                // replace last number in version with Jenkins build number
                def version = pom.version.replace("0-SNAPSHOT", "${currentBuild.number}")
                echo "${version}"
                sh "mvn versions:set -DnewVersion=${version}"

			}
			withMaven(maven: 'M3', mavenSettingsConfig: 'mvn-setting-xml') {
                          		sh "mvn clean compile"
                      		}
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
// 		stage('Deploy to nexus') {
//         	steps {
//         		withMaven(maven: 'M3', mavenSettingsConfig: 'mvn-setting-xml') {
//               		sh "mvn jar:jar deploy:deploy"
//           		}
//     		}
//         }
		stage('Deploy') {
			steps {
				sh "mvn heroku:deploy"
			}
		}
		stage("Tag push"){
		    steps{
        	    script{
        	        def pom = readMavenPom file: 'pom.xml'
        	        sh 'git config --global user.email "jenkins@example.com"'
                    sh  'git config --global user.name "JenkinsJob"'
                    sh 'git tag -a ${version} -m "Jenkins Job version update"'
                    sh 'git push -- tags'
        	    }
        	}
		}
	}
}
