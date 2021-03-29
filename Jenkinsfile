pipeline{
	agent any

	tools {
		maven "M3"
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
