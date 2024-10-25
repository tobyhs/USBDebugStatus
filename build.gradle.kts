// Top-level build file where you can add configuration options common to all sub-projects/modules.

allprojects {
    dependencyLocking {
        lockAllConfigurations()
    }
}

plugins {
    id("com.android.application") version "8.7.1" apply false
}
