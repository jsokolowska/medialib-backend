pipeline{
	agent any

	tools {
		maven "M3"
		sonarQube "sonar-scanner"
	}

	stages {
		stage('Build') {
			steps {
				sh "mvn clean compile"
			}
		}
		stage('Test') {
			steps {
				sh "mvn test"
			}
		}
		stage('SonarQube analysis') {
			steps {
				withSonarQubeEnv("sonar-scanner") {
					sh "sonar-scanner"
				}
			}
		}
		stage('Deploy to nexus') {
			steps {
				sh "mvn jar:jar deploy:deploy"
			}
		}
		stage('Deploy') {
			steps {
				sh "mvn heroku:deploy"
			}
		}
	}
}
