package com.mudora.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileReader

@Deprecated("much slower than org.json Preferences")
class Preferences(private val node: File) {
    private val root: File = File(node, "root.json")

    fun node(name: String): Preferences {
        return Preferences(File(node, name))
    }

    /**
     * fetch [List] of available [Preferences] nodes
     * within the current node
     */
    fun nodes(): List<String> {
        return node.listFiles { f -> f.isDirectory }
            ?.map { it.nameWithoutExtension } ?: emptyList()
    }

    /**
     * parse [JsonElement] from [root] [Preferences]
     */
    fun get(): JsonObject? {
        try {
            return JsonParser.parseReader(FileReader(root))?.asJsonObject
                ?: return null
        } catch (i: IllegalStateException) { // if file isn't populated it'll throw exception
            return JsonParser.parseString("{}").asJsonObject
        }
    }

    /**
     * return available [Set] of keys in [root] [Preferences]
     */
    fun keys(): Set<String> {
        return get()?.asJsonObject?.keySet()
            ?: return emptySet()
    }

    /**
     * return if [key] is within [root] of [Preferences]
     */
    fun hasKey(key: String): Boolean {
        return get()?.has(key) ?: false
    }

    /**
     * return [key] as [Long] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    fun getLong(key: String, default: Long = -1L): Long {
        return get()?.get(key)?.asLong ?: default
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    fun getInt(key: String, default: Int = -1): Int {
        return get()?.get(key)?.asInt ?: default
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    fun getDouble(key: String, default: Double = -1.0): Double {
        return get()?.get(key)?.asDouble ?: default
    }

    /**
     * return [key] as [Boolean] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return get()?.get(key)?.asBoolean ?: default
    }

    /**
     * return [key] as [Int] if available within [root] of [Preferences]
     * otherwise return [default]
     */
    fun getString(key: String, default: String = ""): String {
        return get()?.get(key)?.asString ?: default
    }

    /**
     * delete [key] from [root] of [Preferences]
     * true if successful, false is unsuccessful
     */
    fun delete(key: String): Boolean {
        val data = get() ?: return false

        root.writeText(Gson().toJson(data.remove(key)))
        return true
    }

    /**
     * add [key] and [value] to [Preferences]
     * true if successful, false is unsuccessful
     */
    fun set(key: String, value: String): Boolean {
        val data = get() ?: return false
        data.addProperty(key, value)

        root.writeText(data.toString())
        return true
    }

    /**
     * add [key] and [value] to [Preferences]
     * true if successful, false is unsuccessful
     */
    fun set(key: String, value: Boolean): Boolean {
        val data = get() ?: return false
        data.addProperty(key, value)

        root.writeText(Gson().toJson(data))
        return true
    }

    /**
     * add [key] and [value] to [Preferences]
     * true if successful, false is unsuccessful
     */
    fun set(key: String, value: Char): Boolean {
        val data = get() ?: return false
        data.addProperty(key, value)

        root.writeText(Gson().toJson(data))
        return true
    }

    /**
     * add [key] and [value] to [Preferences]
     * true if successful, false is unsuccessful
     */
    fun set(key: String, value: Number): Boolean {
        val data = get() ?: return false
        data.addProperty(key, value)

        root.writeText(Gson().toJson(data))
        return true
    }
}