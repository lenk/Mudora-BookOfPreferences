package com.mudora.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class Preferences(private val node: File) {
    val root: File = File(node, "root.json")

    companion object {
        val DEFAULT: Preferences = Preferences(File(System.getProperty("user.home"), ".mudora"))
        val mapper = jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }

        val files = object: ArrayList<File>() {
            override fun get(index: Int): File {
                removeIf { System.currentTimeMillis() - it.lastModified() > TimeUnit.MINUTES.toMillis(15) }
                return super.get(index)
            }
        }

    }

    init {
        if (!node.exists()) {
            node.mkdirs()
        }

        if (!root.exists()) {
            init()
        }
    }

    private fun init() {
        root.writeText("{}")
    }

    fun node(name: String): Preferences {
        return Preferences(File(node, name))
    }

    /**
     * fetch [List] of available [Preferences] nodes
     * within the current node
     */
    @Synchronized
    fun nodes(): List<String> {
        return node.listFiles { f -> f.isDirectory }
            ?.map { it.nameWithoutExtension } ?: emptyList()
    }

    /**
     * parse [JSONObject] from [root] [Preferences]
     */
    @Synchronized
    fun get(): JSONObject {
        return try {
            JSONObject(root.readText())
        } catch (i: JSONException) { // if file isn't populated it'll throw exception
            JSONObject()
        }
    }

    fun getMapper(): ObjectMapper {
        return mapper
    }

    /**
     * return available [Set] of keys in [root] [Preferences]
     */
    @Synchronized
    fun keys(): Set<String> {
        return get().keySet()
            ?: return emptySet()
    }

    /**
     * return if [key] is within [root] of [Preferences]
     */
    @Synchronized
    fun hasKey(key: String): Boolean {
        return get().has(key)
    }

    /**
     * return [key] as [JSONObject] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getObject(key: String, default: JSONObject = JSONObject()): JSONObject {
        return get().optJSONObject(key, default)
    }

    /**
     * return [key] as [JSONArray] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getArray(key: String, default: JSONArray = JSONArray()): JSONArray {
        return get().optJSONArray(key) ?: default
    }

    /**
     * return [key] as [Long] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getLong(key: String, default: Long = -1L): Long {
        return get().optLong(key, default)
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getInt(key: String, default: Int = -1): Int {
        return get().optInt(key, default)
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getDouble(key: String, default: Double = -1.0): Double {
        return get().optDouble(key, default)
    }

    /**
     * return [key] as [Boolean] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return get().optBoolean(key, default)
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    @Synchronized
    fun getString(key: String, default: String = ""): String {
        return get().optString(key, default)
    }

    /**
     * delete [key] from [root] of [Preferences]
     * true if successful, false is unsuccessful
     */
    @Synchronized
    fun delete(key: String) = apply {
        set(get().apply {
            remove(key)
        })
    }

    /**
     * add [data] key and value to [root] of [Preferences]
     */
    @Synchronized
    fun set(data: HashMap<String, Any>) = apply {
        val json = get()

        data.entries.forEach {
            json.put(it.key, it.value)
        }

        set(json)
    }

    /**
     * add [key] and [value] to [Preferences]
     * true if successful, false is unsuccessful
     */
    @Synchronized
    fun set(key: String, value: Any) = apply {
        set(get().apply {
            put(key, value)
        })
    }

    /**
     * export [JSONObject] to [root] of [Preferences]
     */
    @Synchronized
    fun set(json: JSONObject) = apply {
        mapper.writeValue(root, json.toString())
    }

    /**
     * return [JSONObject] as a [Map]
     */
    @Synchronized
    fun map(): Map<String, Any> {
        val json = get()
        return json.keySet().associateWith { json.get(it) }
    }

    /**
     * merge provided [preferences] values into the current [root] of [Preferences]
     */
    @Synchronized
    fun merge(preferences: Preferences) {
        val provided = preferences.get()
        val current = get()

        provided.keySet().forEach {
            current.put(it, provided.get(it))
        }

        set(current)
    }

    /**
     * set provided [preferences] values into the current [root] of [Preferences]
     */
    @Synchronized
    fun set(preferences: Preferences) {
        set(preferences.get())
    }

    /**
     * parse [root] of [Preferences] to [Object] of type [T]
     */
     inline fun <reified T : Any> deserialize(): T {
        synchronized(root.canonicalPath.intern()) {
            return mapper.readValue(root.inputStream(), T::class.java)
        }
    }


    /**
     * serialize [any] and export as [root] of [Preferences]
     */
    @Synchronized
    fun serialize(any: Any) {
        synchronized(root.canonicalPath.intern()) {
            mapper.writeValue(root, any)
        }
    }
}