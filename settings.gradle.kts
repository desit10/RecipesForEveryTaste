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
        google()
        mavenCentral()
        maven("https://storage.zego.im/maven")
        maven("https://jitpack.io")
    }
}

rootProject.name = "RecipesForEveryTaste"
include(":app")
 