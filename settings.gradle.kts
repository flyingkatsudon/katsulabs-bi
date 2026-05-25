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

rootProject.name = "insight-board"

include(
    "modules:domain",
    "modules:application",
    "modules:infrastructure",
    "modules:api",
)
