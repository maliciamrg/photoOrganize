pipeline {
    agent any
    stages {
        stage('queryLrcatApi : Build') {
            steps {
                script {
                    dir("queryLrcatApi") {
                        sh 'mvn -B -DskipTests clean package'
                    }
                }
            }
        }
        stage('photoOrganize_Back : Build') {
            steps {
                script {
                    dir("photoOrganize_Back") {
                        sh 'mvn -B -DskipTests clean package'
                    }
                }
            }
        }
        stage('photoOrganize_Front : Build') {
            steps {
                script {
                    dir("photoOrganize_Front") {
                        sh 'mvn -B -DskipTests clean package'
                    }
                }
            }
        }

        stage("queryLrcatApi : Create/Push Docker Image") {
            steps {
                script {
                    dir("queryLrcatApi") {
                        pom = readMavenPom file: 'pom.xml'
                        withCredentials([usernamePassword(credentialsId: 'hub.docker.com', passwordVariable: 'HUB_REPO_PASS', usernameVariable: 'HUB_REPO_USER')]) {
                            def user = env.HUB_REPO_USER
                            def password = env.HUB_REPO_PASS
                            sh "docker version"
                            sh "docker login -u $user -p $password"
                            sh "docker build -t maliciamrg/${pom.getArtifactId().toLowerCase()}:${pom.getVersion()} . "
                            sh "docker push maliciamrg/${pom.getArtifactId().toLowerCase()}:${pom.getVersion()}"
                        }
                    }
                }
            }
        }
        stage("photoOrganize_Back : Create/Push Docker Image") {
            steps {
                script {
                    dir("photoOrganize_Back") {
                        pom = readMavenPom file: 'pom.xml'
                        withCredentials([usernamePassword(credentialsId: 'hub.docker.com', passwordVariable: 'HUB_REPO_PASS', usernameVariable: 'HUB_REPO_USER')]) {
                            def user = env.HUB_REPO_USER
                            def password = env.HUB_REPO_PASS
                            sh "docker version"
                            sh "docker login -u $user -p $password"
                            sh "docker build -t maliciamrg/${pom.getArtifactId().toLowerCase()}:${pom.getVersion()} . "
                            sh "docker push maliciamrg/${pom.getArtifactId().toLowerCase()}:${pom.getVersion()}"
                        }
                    }
                }
            }
        }
        stage("photoOrganize_Front : Create/Push Docker Image") {
            steps {
                script {
                    dir("photoOrganize_Front") {
                        // Read the package.json file into a variable
                        def packageJson = readJSON file: './package.json'

                        // Extract the name and version
                        def packageName = packageJson.name
                        def packageVersion = packageJson.version
                        withCredentials([usernamePassword(credentialsId: 'hub.docker.com', passwordVariable: 'HUB_REPO_PASS', usernameVariable: 'HUB_REPO_USER')]) {
                            def user = env.HUB_REPO_USER
                            def password = env.HUB_REPO_PASS
                            dir("photoOrganize-infrastructure") {
                                sh "docker version"
                                sh "docker login -u $user -p $password"
                                sh "docker build -t maliciamrg/${packageName}:${packageVersion} . "
                                sh "docker push maliciamrg/${packageName}:${packageVersion}"
                            }
                        }
                    }
                }
            }
        }

        stage("queryLrcatApi : install Docker Image into 200") {
            steps {
                script {
                    dir("queryLrcatApi") {
                        sh "docker --context remote compose up -d"
                    }
                }
            }
        }
        stage("photoOrganize_Back : install Docker Image into 200") {
            steps {
                script {
                    dir("photoOrganize_Back") {
                        dir("photoOrganize-infrastructure") {
                            sh "docker --context remote compose up -d"
                        }
                    }
                }
            }
        }
        stage("photoOrganize_Front : install Docker Image into 200") {
            steps {
                script {
                    dir("photoOrganize_Front") {
                        sh "docker --context remote compose up -d"
                    }
                }
            }
        }

    }
    post {
        always {
            print "always"
        }
        changed {
            print "changed"
        }
        fixed {
            print "fixed"
            discordSend (
                    description: "Jenkins Pipeline Build",
                    footer: "Status fixed",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    title: JOB_NAME,
                    webhookURL: "https://discord.com/api/webhooks/1251803129004032030/Ms-4v3aw3MMkIHIECMYMiP48NTV_F1IazsvwQmAqGGFw4OOR9FRX-DwjFG5V1dV-zKg6"
            )
        }
        regression {
            print "regression"
        }
        aborted {
            print "aborted"
        }
        failure {
            print "failure"
            script {
                if (!currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')){
                    discordSend (
                        description: "Jenkins Pipeline Build",
                        footer: "Status failure",
                        link: env.BUILD_URL,
                        result: currentBuild.currentResult,
                        title: JOB_NAME,
                        webhookURL: "https://discord.com/api/webhooks/1251803129004032030/Ms-4v3aw3MMkIHIECMYMiP48NTV_F1IazsvwQmAqGGFw4OOR9FRX-DwjFG5V1dV-zKg6"
                    )
                }
            }
        }
        success {
            print "success"
        }
        unstable {
            print "unstable"
        }
        unsuccessful {
            print "unsuccessful"
        }
        cleanup {
            print "cleanup"
        }
    }
}