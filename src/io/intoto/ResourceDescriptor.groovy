package io.intoto

import groovy.json.JsonOutput

/*
 | ----------------------------------------------------------------------------
 | ResourceDescriptor
 | ----------------------------------------------------------------------------
 | A size-efficient description of any software artifact or resource (mutable 
 | or immutable).
 |
 | For more information, please visit: 
 | - https://github.com/in-toto/attestation/blob/main/spec/v1/resource_descriptor.md
 */
 
class ResourceDescriptor {

    def LinkedHashMap payload = [:]

    ResourceDescriptor() {

    }

    def ResourceDescriptor addAnnotation(String key, value) {
        if (!this.payload.containsKey('annotations')) {
            this.payload.put('annotations', [:])
        }
        this.payload.annotations.put(key, value)
        return this
    }

    def ResourceDescriptor annotations(LinkedHashMap annotations) {
        this.payload.put('annotations', annotations)
        return this
    }
    
    def ResourceDescriptor content(String content) {
        this.payload.put('content', content)
        return this
    }

    def ResourceDescriptor digest(LinkedHashMap digestSet) {
        this.payload.put('digest', digestSet)
        return this
    }
    
    def ResourceDescriptor downloadLocation(String location) {
        this.payload.put('downloadLocation', location)
        return this
    }

    def ResourceDescriptor mediaType(String type) {
        this.payload.put('mediaType', type)
        return this
    }

    def ResourceDescriptor name(String name) {
        this.payload.put('name', name)
        return this
    }

    def ResourceDescriptor uri(String uri) {
        this.payload.put('uri', uri)
        return this
    }
    
    def LinkedHashMap get() {
        return payload
    }

    def Void print() {
        println(JsonOutput.prettyPrint(this.toJson()))
    }

    def String toJson() {
        return JsonOutput.toJson(payload)
    }
}