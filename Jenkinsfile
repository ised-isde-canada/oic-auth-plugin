@Library(["ised-cicd-lib", "ised-cicd-lib-api"]) _

pipeline {
	agent {
       	label 'maven'
   	}
    tools {
      jdk 'openjdk-8'
    }
     	
    options {
        disableConcurrentBuilds()
    }
  
    stages {
    	stage('build') {
			steps {
				script{
        			builder.buildMavenLibrary()
				}
			}
    	}
    }
}
