package com.mudora.json.annotations

import kotlin.reflect.KClass

/**
 * instructions on serializing extras
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Instructions(vararg val deserialize: Instruction)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Instruction(val key: String, val cls: KClass<*>)