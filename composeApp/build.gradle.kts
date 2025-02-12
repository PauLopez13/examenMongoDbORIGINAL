import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("app.cash.sqldelight") version "2.0.2"
    kotlin("plugin.serialization") version "2.1.0"

}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation("androidx.collection:collection:1.2.0")

            implementation("org.mongodb:mongodb-driver-sync:4.11.1")
            implementation("org.mongodb:mongodb-driver-core:4.11.1")
            implementation("org.mongodb:mongodb-driver-legacy:4.11.1")
            implementation("org.mongodb:bson:4.11.1")
            implementation("org.slf4j:slf4j-nop:1.7.36")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.compose.ui:ui:1.4.0")
            implementation("org.jetbrains.compose.material:material:1.4.0")
            implementation("org.litote.kmongo:kmongo:4.10.0")
            implementation("org.xerial:sqlite-jdbc:3.36.0.3")
            implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
            implementation("com.squareup.sqldelight:runtime:1.5.3")
            implementation("com.squareup.sqldelight:coroutines-extensions:1.5.3")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")

        }
    }
}


compose.desktop {
    application {
        mainClass = "ipar.plo9215.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ipar.plo9215"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("zty.plo9215")
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")
        }
    }
}

