/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  tsrapprun - Shared Module (Kotlin Multiplatform)           ║
 * ║                                                             ║
 * ║  Código compartilhado entre Android e iOS.                  ║
 * ║  Usa expect/actual para abstrair implementações             ║
 * ║  específicas de cada plataforma.                            ║
 * ║                                                             ║
 * ║  Estrutura:                                                 ║
 * ║  - commonMain: interfaces e lógica compartilhada            ║
 * ║  - androidMain: implementações Android (Firebase Auth)      ║
 * ║  - iosMain: implementações iOS (Apple Sign-In/Google)       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Serialização para converter objetos Kotlin <-> JSON de forma segura
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // ── Target Android ──
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
                // Suprime warning de expect/actual classes (beta no Kotlin 2.1)
                freeCompilerArgs += "-Xexpect-actual-classes"
            }
        }
    }

    // ── Targets iOS (todas as arquiteturas suportadas) ──
    listOf(
        iosX64(),         // Simulador Intel Mac
        iosArm64(),       // Dispositivo físico iPhone
        iosSimulatorArm64() // Simulador Apple Silicon Mac
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        // ── Código Comum (compartilhado entre Android e iOS) ──
        commonMain.dependencies {
            // Compose Multiplatform - UI compartilhada
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            // Coroutines para operações assíncronas seguras
            implementation(libs.kotlinx.coroutines.core)
            // Serialização JSON para dados do usuário
            implementation(libs.kotlinx.serialization.json)
        }

        // ── Código Android ──
        androidMain.dependencies {
            // Coroutines com dispatcher Android (Main thread)
            implementation(libs.kotlinx.coroutines.android)

            // AndroidX Security — EncryptedSharedPreferences + Android Keystore
            implementation(libs.androidx.security.crypto)

            // Firebase Auth para Google Sign-In no Android
            // NOTA: Em KMP, o BoM do Firebase não funciona via platform()
            // Então referenciamos firebase-auth com versão explícita
            implementation("com.google.firebase:firebase-auth-ktx:23.1.0")

            // Credential Manager para login seguro
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play)
            implementation(libs.google.id)

            // Activity Compose — necessário para permission launcher no shared module
            implementation(libs.androidx.activity.compose)

            // ExifInterface — leitura de metadados de fotos importadas
            implementation(libs.androidx.exifinterface)

            // CameraX — captura de fotos moderna e segura
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)

            // WorkManager — agendamento persistente de notificações locais
            implementation(libs.androidx.work.runtime)
        }
    }
}

android {
    namespace = "com.tsrapprun.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
