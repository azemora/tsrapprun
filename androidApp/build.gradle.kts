/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  tsrapprun - Android App Module                             ║
 * ║                                                             ║
 * ║  Módulo principal do app Android. Contém a Activity,        ║
 * ║  configuração do Firebase e Google Sign-In.                 ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Nenhum secret/token é armazenado no código               ║
 * ║  - google-services.json é lido em runtime pelo Firebase     ║
 * ║  - Credenciais ficam no Android Keystore via CredentialMgr  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    // Google Services processa o google-services.json em build time
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.tsrapprun.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tsrapprun.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ── Módulo compartilhado (código comum Android + iOS) ──
    implementation(project(":shared"))

    // ── AndroidX Core ──
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)

    // ── Firebase (BoM garante compatibilidade de versões) ──
    // O BoM é um "Bill of Materials" - define versões compatíveis
    // de todas as bibliotecas Firebase automaticamente
    implementation(platform(libs.firebase.bom))
    // Firebase Auth para autenticação com Google
    implementation(libs.firebase.auth)
    // Firebase Firestore — banco NoSQL para metadados de eventos/fotos
    implementation(libs.firebase.firestore)
    // Firebase Storage — armazenamento de fotos na nuvem
    implementation(libs.firebase.storage)

    // ── Google Sign-In via Credential Manager ──
    // API moderna e segura que substitui o antigo GoogleSignInClient
    // Usa o Android Keystore para armazenar credenciais com segurança
    implementation(libs.androidx.credentials)
    // Integração com Google Play Services
    implementation(libs.androidx.credentials.play)
    // Biblioteca para criar requests de Google ID Token
    implementation(libs.google.id)

    // ExifInterface — leitura de data EXIF ao importar fotos
    implementation(libs.androidx.exifinterface)
}
