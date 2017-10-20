pipeline {
  agent {
    node {
      label 'Build'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        build(job: 'test', quietPeriod: 5)
        sleep 5
      }
    }
  }
}