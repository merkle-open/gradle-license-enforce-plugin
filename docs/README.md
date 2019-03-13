# license-enforce-plugin

Gradle plugin enforces licenses of dependencies to comply with definitions.

## Usage

`build.gralde.kts`
```kotlin
plugins {
	// ...
    id("com.namics.oss.gradle.license-enforce-plugin") version "1.0-SNAPSHOT"
}

tasks.enforceLicenses {
    allowedCategories = listOf("Apache", "BSD", "LGPL", "MIT", "ISC", "MPL")
}
```

Invoke:
```bash
gradle enforceLicenses
```
