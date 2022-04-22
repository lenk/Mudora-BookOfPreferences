package com.mudora.json.adapter

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.mudora.json.annotations.Fallback
import com.mudora.json.annotations.Instructions
import java.lang.reflect.Field
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class ExtraDeserializer(private val type: KClass<*>) : JsonDeserializer<Any> {

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Any {
        val parsed = Gson().fromJson(element, this.type.java)
        if (!element.isJsonObject) {
            return parsed
        }

        // obtain kotlin properties for Fallback if any
        val fallbackProperty = parsed::class.members.filterIsInstance<KMutableProperty<*>>()
            .firstOrNull { it.hasAnnotation<Fallback>() } ?: return parsed

        // create fallback hashmap to store extra missing fields
        val fallbackMap = HashMap<String, Any?>()

        // pre-mapped keys
        val parsedKeys = this.type.java.declaredFields.map { getFieldKey(it) }

        // map missing fields to hashmap
        val jsonObject = element.asJsonObject
        for (key in jsonObject.keySet()) {
            if (!parsedKeys.contains(key)) {
                val value = jsonObject[key]

                fallbackMap[key] = parse(fallbackProperty, key, value)
            }
        }

        // set fallback results
        fallbackProperty.setter.call(parsed, fallbackMap)

        // return results
        return parsed
    }

    /**
     * cast [value] to [Any] based on the [JsonElement] primitive type,
     * if custom [Instructions] are provided for JsonObject, it'll attempt to deserialize it
     */
    private fun parse(property: KMutableProperty<*>, key: String, value: JsonElement): Any? {
        return if (value.isJsonPrimitive) {
            val primitive = value.asJsonPrimitive

            if (primitive.isBoolean) {
                primitive.asBoolean

            } else if (primitive.isNumber) {
                primitive.asNumber

            } else if (primitive.isString) {
                primitive.asString

            } else {
                null
            }

        } else {
            if (value.isJsonObject) {

                // check if there's any instructions to deserialize JsonObject to Type
                if (property.hasAnnotation<Instructions>()) {

                    val deserializeTo = property.findAnnotation<Instructions>()?.deserialize?.firstOrNull { it.key == key }
                    if (deserializeTo != null) {
                        deserialize(value, deserializeTo.cls)
                    } else {
                        value.asJsonObject
                    }

                } else {
                    value.asJsonObject
                }
            } else {
                null
            }
        }
    }

    /**
     * deserialize [jsonElement] to the provided [cls]
     * and return generic [Any] object
     */
    private fun deserialize(jsonElement: JsonElement, cls: KClass<*>): Any {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(cls.java, ExtraDeserializer(cls))

        return gsonBuilder.create().fromJson(jsonElement, cls.java)
    }


    /**
     * obtain json key from the provided [field]
     * it'll check if [field] is annotated with [SerializedName] for custom (un-matching) keys
     * otherwise returns the field's name as key
     */
    private fun getFieldKey(field: Field): String {
        if (field.isAnnotationPresent(SerializedName::class.java)) {
            return field.getAnnotation(SerializedName::class.java).value
        }

        return field.name
    }
}