pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()        // REQUIRED for Firebase
        mavenCentral()  // REQUIRED
    }
}

rootProject.name = "TrackRunner"
include(":app")
