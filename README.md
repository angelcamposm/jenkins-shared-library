# jenkins-shared-library

This Jenkins Shared Library is a MVP of a SLSA Provenance and in-toto attestation for Jenkins builds. 


## Jenkinsfile

```groovy
@Library('pipeline-library@master') _ 

// Import stateful classes from Jenkins shared library
import dev.slsa.Provenance
import edu.nyu.engineering.ssl.Envelope
import io.intoto.ResourceDescriptors.*
import io.intoto.ResourceDescriptor
import io.intoto.Statement

// Create new instances
def Provenance provenance = new Provenance()
def FileResourceDescriptor fileRd = new FileResourceDescriptor()
def GitRepositoryResourceDescriptor gitRd = new GitRepositoryResourceDescriptor()
def Statement statement = new Statement()
def Envelope envelope = new Envelope()

pipeline {
    agent any
    stages {
        stage('init') {
            steps {
                script {
                    // Instantiate a SLSA Provenance
                    provenance.construct()
                    
                    def String path = ''
                    dir("/var/jenkins_home/workspace/${env.JOB_BASE_NAME}@libs") {
                        path = sh(
                            script: 'find . -type d -name .git -maxdepth 2 -print -quit',
                            returnStdout: true
                        ).trim().replace('./', '').replace('/.git', '')
                    }
                    dir("/var/jenkins_home/workspace/${env.JOB_BASE_NAME}@libs/${path}") {
                        // Initialize a ResourceDescriptor for a git repository
                        gitRd.construct()
                        // Set the name of the ResourceDescriptor 
                        gitRd.setName('pipeline-library')
                    }
                }
                script {
                    sh 'touch test.json'
                    sh 'echo "{}" > test.json'
                    
                    // Create a ResourceDescriptor for file test.json
                    fileRd.file('test.json')
                }
            }
        }
    }
    post {
        always {
            script {
                // Add git ResourceDescriptor as a builder dependency
                provenance.addBuilderDependency(gitRd.get())
                // Mark build as finished. (this step adds finishedAt Timestmap)
                provenance.finish()
                // Write to a file
                provenance.write()
                
                // Create an In-Toto Statement
                statement.construct()
                // Add SLSA provenance to InToto attestation
                statement.addProvenance(provenance.get())
                // Add subject (ResourceDescriptor) to InToto attestation
                statement.addSubject(fileRd.get())
                // Save to a file
                statement.write()

                // Create a DSSEv1 envelope
                envelope.construct()
                // Add in-toto statement to the envelope
                envelope.addInTotoStatement(statement)
                // Save to a file
                envelope.write()
                
                archiveArtifacts artifacts: '*.json', fingerprint: true, followSymlinks: false
            }
        }
        cleanup {
            cleanWs()
        }
    }
}
```

## Artifacts

Here you have the samples of artifacts it can be produced during the build.

### SLSA Provenance

Example of SLSA Provenance produced.

```json
{
  "buildDefinition": {
    "buildType": "https://www.jenkins.io/Pipeline",
    "externalParameters": {},
    "internalParameters": {
      "BRANCH": "*/master",
      "ENVIRONMENT": "DEV"
    },
    "resolvedDependencies": [
      {
        "name": "pipeline-library",
        "digest": {
          "gitCommit": {
            "gitCommit": "ccff45a82df61ab4efe41bf20a9bd22f8db1706c"
          }
        },
        "uri": "git@github.com:angelcamposm/jenkins-shared-library.git"
      }
    ]
  },
  "runDetails": {
    "builder": {
      "id": "http://localhost:8080/job/test/103/",
      "builderDependencies": [],
      "version": ""
    },
    "metadata": {
      "invocationId": "#103",
      "startedOn": "2023-08-17T13:24:27Z",
      "finishedOn": "2023-08-17T13:24:34Z"
    },
    "byproducts": []
  }
}
```

### in-toto attestation

Example of in-toto Statement (attestation) produced.

```json
{
  "_type": "https://in-toto.io/Statement/v1",
  "subject": [
    {
      "name": "test.json",
      "digest": {
        "md5": "8a80554c91d9fca8acb82f023de02f11",
        "sha1": "5f36b2ea290645ee34d943220a14b54ee5ea5be5",
        "sha256": "ca3d163bab055381827226140568f3bef7eaac187cebd76878e0b63e9e442356",
        "sha512": "ca4b6defb8adcc010050bc8b1bb8f8092c4928b8a0fba32146abcfb256e4d91672f88ca2cdf6210e754e5b8ac5e23fb023806ccd749ac8b701f79a691f03c87a"
      },
      "annotations": {
        "size": 3,
        "createdAt": "2023-08-17T12:14:57Z"
      }
    }
  ],
  "predicateType": "https://slsa.dev/provenance/v1",
  "predicate": {
    "buildDefinition": {
      "buildType": "https://www.jenkins.io/Pipeline",
      "externalParameters": {},
      "internalParameters": {
        "BRANCH": "*/master",
        "ENVIRONMENT": "DEV"
      },
      "resolvedDependencies": [
        {
          "name": "pipeline-library",
          "digest": {
            "gitCommit": {
              "gitCommit": "9750e594fc52252416b84e225b29791286f9cdbd"
            }
          },
          "uri": "git@github.com:angelcamposm/jenkins-shared-library.git"
        }
      ]
    },
    "runDetails": {
      "builder": {
        "id": "http://localhost:8080/job/test/100/",
        "builderDependencies": [],
        "version": ""
      },
      "metadata": {
        "invocationId": "#100",
        "startedOn": "2023-08-17T12:14:52Z",
        "finishedOn": "2023-08-17T12:14:59Z"
      },
      "byproducts": []
    }
  }
}
```

### SSL DSSEv1

Example of DSSEv1 envelope produced.

```json
{
    "payload": "eyJfdHlwZSI6Imh0dHBzOi8vaW4tdG90by5pby9TdGF0ZW1lbnQvdjEiLCJzdWJqZWN0IjpbeyJuYW1lIjoidGVzdC5qc29uIiwiZGlnZXN0Ijp7Im1kNSI6IjhhODA1NTRjOTFkOWZjYThhY2I4MmYwMjNkZTAyZjExIiwic2hhMSI6IjVmMzZiMmVhMjkwNjQ1ZWUzNGQ5NDMyMjBhMTRiNTRlZTVlYTViZTUiLCJzaGEyNTYiOiJjYTNkMTYzYmFiMDU1MzgxODI3MjI2MTQwNTY4ZjNiZWY3ZWFhYzE4N2NlYmQ3Njg3OGUwYjYzZTllNDQyMzU2Iiwic2hhNTEyIjoiY2E0YjZkZWZiOGFkY2MwMTAwNTBiYzhiMWJiOGY4MDkyYzQ5MjhiOGEwZmJhMzIxNDZhYmNmYjI1NmU0ZDkxNjcyZjg4Y2EyY2RmNjIxMGU3NTRlNWI4YWM1ZTIzZmIwMjM4MDZjY2Q3NDlhYzhiNzAxZjc5YTY5MWYwM2M4N2EifSwiYW5ub3RhdGlvbnMiOnsic2l6ZSI6MywiY3JlYXRlZEF0IjoiMjAyMy0wOC0xN1QxMzo5OjMyWiJ9fV0sInByZWRpY2F0ZVR5cGUiOiJodHRwczovL3Nsc2EuZGV2L3Byb3ZlbmFuY2UvdjEiLCJwcmVkaWNhdGUiOnsiYnVpbGREZWZpbml0aW9uIjp7ImJ1aWxkVHlwZSI6Imh0dHBzOi8vd3d3LmplbmtpbnMuaW8vUGlwZWxpbmUiLCJleHRlcm5hbFBhcmFtZXRlcnMiOnt9LCJpbnRlcm5hbFBhcmFtZXRlcnMiOnsiQlJBTkNIIjoiKi9tYXN0ZXIiLCJFTlZJUk9OTUVOVCI6IkRFViJ9LCJyZXNvbHZlZERlcGVuZGVuY2llcyI6W3sibmFtZSI6InBpcGVsaW5lLWxpYnJhcnkiLCJkaWdlc3QiOnsiZ2l0Q29tbWl0Ijp7ImdpdENvbW1pdCI6IjMzYWIxMWJjMjNmMmRkZDgzNTgyNzQ0NWM1ZmRmYWUzNjIxNTBkODAifX0sInVyaSI6ImdpdEBnaXRodWIuY29tOmFuZ2VsY2FtcG9zbS9qZW5raW5zLXNoYXJlZC1saWJyYXJ5LmdpdCJ9XX0sInJ1bkRldGFpbHMiOnsiYnVpbGRlciI6eyJpZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9qb2IvdGVzdC8xMDIvIiwiYnVpbGRlckRlcGVuZGVuY2llcyI6W10sInZlcnNpb24iOiIifSwibWV0YWRhdGEiOnsiaW52b2NhdGlvbklkIjoiIzEwMiIsInN0YXJ0ZWRPbiI6IjIwMjMtMDgtMTdUMTM6OToyN1oiLCJmaW5pc2hlZE9uIjoiMjAyMy0wOC0xN1QxMzo5OjM0WiJ9LCJieXByb2R1Y3RzIjpbXX19fQ==",
    "payloadType": "application/vnd.in-toto+json",
    "signatures": []
}
```
