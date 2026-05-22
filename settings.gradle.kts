pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "katsulabs-bi"

include(
    "modules:domain",
    "modules:application",
    "modules:infrastructure",
    "modules:api",
)
