package io.intoto

import groovy.json.JsonOutput

def LinkedHashMap rsd = [:]

def String resourceName = ''

def Void initialize(String fileName) {

    resourceName = fileName

    def LinkedHashMap resourceDescriptor = [:]

    resourceDescriptor.put('name', fileName)
    resourceDescriptor.put('size', getFileSize())
    resourceDescriptor.put('digest', getDigestSet())
    resourceDescriptor.put('createdAt', getTimestamp())

    rsd = resourceDescriptor
}

def LinkedHashMap getDigestSet() {
    def LinkedHashMap digestSet = [:]

    digestSet.put('md5', getMd5Sum())
    digestSet.put('sha1', getSha1Sum())
    digestSet.put('sha256', getSha256Sum())
    digestSet.put('sha512', getSha512Sum())

    return digestSet
}

def String getMd5Sum() {
    return sh(
        label: 'Get MD5 (128-bit) checksums.',
        script: "md5sum ${resourceName} | awk '{print \$1}'",
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha1Sum() {
    return sh(
        label: 'Get SHA1 (160-bit) checksums.', 
        script: "sha1sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha256Sum() {
    return sh(
        label: 'Get SHA256 (256-bit) checksums.', 
        script: "sha256sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha512Sum() {
    return sh(
        label: 'Get SHA512 (512-bit) checksums.', 
        script: "sha512sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def Integer getFileSize() {
    return sh(
        label: 'Get file size in bytes.',
        script: "stat -c%s ${resourceName}",
        returnStdout: true
    ).trim().toInteger()
}

def String getTimestamp() {

    def Integer timestamp = sh(
        label: 'Get file Birth date.',
        script: "stat -c%W ${resourceName}",
        returnStdout: true
    ).trim().toInteger()
    
    if (timestamp == 0) {
        timestamp = sh(
            label: 'Get file Access date.',
            script: "stat -c%X ${fileName}",
            returnStdout: true
        ).trim().toInteger()
    }

    return sh(
        script: "date --date=@${timestamp} +%Y-%m-%dT%H:%M:%S.%sZ",
        returnStdout: true
    ).trim().toString()
}

def LinkedHashMap get() {
    return rsd
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toJson() {
    return JsonOutput.toJson(rsd)
}
