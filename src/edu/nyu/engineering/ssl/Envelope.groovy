package edu.nyu.engineering.ssl

import groovy.json.JsonOutput
import io.intoto.Statement

def LinkedHashMap envelope = [:]

def Void construct() {

    def LinkedHashMap payload = [:]

    payload.put('payload', '')
    payload.put('payloadType', '')
    payload.put('signatures', [])

    envelope = payload
}

def Void addInTotoStatement(Statement statement) {
    envelope.payload = statement.toBase64()
    envelope.payloadType = 'application/vnd.in-toto+json'
}

def String getPayload() {
    def byte[] decoded = envelope.payload.encoded.decodeBase64()
    return new String(decoded)
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toBase64() {
    return toJson().bytes.encodeBase64().toString()
}

def String toJson() {
    return JsonOutput.toJson(envelope)
}

def Void write(Boolean pretty = true) {
        
    def String payload = pretty 
        ? JsonOutput.prettyPrint(toJson())
        : toJson()
    
    writeFile encoding: 'UTF-8', file: "${env.JOB_BASE_NAME}-${currentBuild.getNumber()}.envelope.json", text: payload
}