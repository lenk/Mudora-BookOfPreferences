
# Mudora - Book of Preferences
<p align="center">
    <b>Simple library for preference storage</b><br>
    This library is dedicated for storing settings (preferences) in a JSON format for easy transportability. This library depends on Jackson & org.json to accomplish these goals between loading, storing, serializing, and de-serializing!
</p>

<p align="center">
    <img src="blob/mudora.png" />
</p>

## Samples
Load preference location
```kotlin
// create a root preference location
// this will create root.json file within the provided path
val preferences = Preferences(File(System.getProperty("user.home")), ".mudora")
```

Store data within [Preferences]
```kotlin
// use default preference location for this example
// refer to first example for custom preference location
val preferences = Preferences.DEFAULT

// store under [key] the provided [value] String
preferences.set("key", "value")
```

Get data within [Preferences]
```kotlin
// use default preference location for this example
// refer to first example for custom preference location
val preferences = Preferences.DEFAULT

// get String [value] from the provided [key]
val value = preferences.getString("key")
```
Delete data within [Preferences]
```kotlin
// use default preference location for this example
// refer to first example for custom preference location
val preferences = Preferences.DEFAULT

// delete [value] from the provided [key]
preferences.delete("key")
```

De-serialize [Preferences] to Object
```kotlin
// use default preference location for this example
// refer to first example for custom preference location
val preferences = Preferences.DEFAULT

// parse [root] of [Preferences] to [Object] of type [Target]
val value = preferences.deserialize<Target>()
```

Serialize Object to [Preferences]
```kotlin
// use default preference location for this example
// refer to first example for custom preference location
val preferences = Preferences.DEFAULT

// the object you want to serialize, '__________' is just a placeholder
val obj = __________

// serialize [object] and export to [Preferences]
preferences.serialize(obj)
```