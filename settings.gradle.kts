pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Fitlog"
include(":app")
include(":core")
include(":feature")
include(":core:ui")
include(":core:database")
include(":core:datastore")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:notification")
include(":core:common")
include(":feature:login")
include(":feature:onboarding")
include(":feature:dashboard")
include(":feature:workout")
include(":feature:gymwork")
