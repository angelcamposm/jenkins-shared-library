package edu.nyu.engineering.ssl

import groovy.json.JsonOutput
import io.intoto.Statement

def LinkedHashMap envelope = [:]

def Void construct() {

    def LinkedHashMap payload = [:]

    payload.add('payload', '')
    payload.add('payloadType', '')
    payload.add('signatures', [])

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