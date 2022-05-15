package com.mudora.json.jackson

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

/**
 * extend this class to have extra fields fallback onto HashMap
 * containing all missing fields from the POJO class
 */
open class FallbackTemplate {
    private val other: MutableMap<String, Any> = HashMap()

    @JsonAnyGetter
    fun any(): Map<String, Any> {
        return other
    }

    @JsonAnySetter
    operator fun set(name: String, value: Any?) {
        if (value == null) {
            other.remove(name)
        } else {
            other[name] = value
        }
    }
}