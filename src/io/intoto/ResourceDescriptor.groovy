package io.intoto

import groovy.json.JsonOutput

class ResourceDescriptor {

    def LinkedHashMap payload = [:]

    ResourceDescriptor() {

    }

    def ResourceDescriptor addAnnotation(String key, value) {
        this.assign('annotations', [:])
        this.annotations.put(key, value)
        return this
    }

    def ResourceDescriptors annotations(LinkedHashMap annotations) {
        this.assign('annotations', annotations)
        return this
    }
    
    def ResourceDescriptor content(String content) {
        this.assign('content', content)
        return this
    }

    def ResourceDescriptor digest(LinkedHashMap digestSet) {
        this.assign('digest', digestSet)
        return this
    }
    
    def ResourceDescriptor downloadLocation(String location) {
        this.assign('downloadLocation', location)
        return this
    }

    def ResourceDescriptor mediaType(String type) {
        this.assign('mediaType', type)
        return this
    }

    def ResourceDescriptor name(String name) {
        this.assign('name', name)
        return this
    }

    def ResourceDescriptor uri(String uri) {
        this.assign('uri', uri)
        return this
    }

    def Void assign(String key, value) {
        if (this.payload.containsKey(key)) {
            this.payload."$key" = value
        } else {
            this.payload.put(key, value)
        }
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