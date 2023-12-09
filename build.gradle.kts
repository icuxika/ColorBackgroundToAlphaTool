import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "studio.attect"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

compose.desktop {
    application {
        mainClass = "studio.attect.tool.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ColorBackgroundToAlphaTool"
            packageVersion = "1.0.0"
            vendor = "Attect"
            description = "Background transparency of multiple pictures of the same subject with different color backgrounds"
            copyright = "@2023 Attect All Rights Reversed"
            macOS {
                dockName = "ColorBackgroundToAlphaTool"
                iconFile.set(project.file("icons/app.icns"))
            }
            linux {
                iconFile.set(project.file("icons/app.png"))
            }
            windows {
                shortcut = true
                dirChooser = true
                upgradeUuid = UUID.randomUUID().toString()
                iconFile.set(project.file("icons/app.ico"))
            }
        }
    }
}
