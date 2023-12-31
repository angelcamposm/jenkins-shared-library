package io.intoto

import java.util.Date

class Timestamp {

    static final FORMAT = "yyyy-MM-dd'T'HH:m:ss"

    static String fromMilliseconds(Long millis) {
        return toString(new Date(millis))
    }
    
    static String fromSeconds(Long seconds) {
        return toString(new Date(seconds * 1000))
    }

    static String get() {
        return toString(new Date())
    }
    
    static String toString(Date date) {
        return "${date.format(FORMAT)}Z"
    }
}