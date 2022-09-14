buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}
allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
