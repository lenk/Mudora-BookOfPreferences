package com.mudora.json.annotations

/**
 * use this on the hashmap you'd like to default all missing fields to
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Fallback