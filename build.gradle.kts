/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  tsrapprun - Build Configuration (Root)                     ║
 * ║                                                             ║
 * ║  Este arquivo declara todos os plugins usados no projeto.   ║
 * ║  Os plugins são aplicados nos módulos individuais           ║
 * ║  (androidApp, shared) conforme necessário.                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
plugins {
    // Plugin do Android Application (usado no módulo androidApp)
    alias(libs.plugins.androidApplication) apply false
    // Plugin do Android Library (usado no módulo shared)
    alias(libs.plugins.androidLibrary) apply false
    // Plugin Kotlin para Android
    alias(libs.plugins.kotlinAndroid) apply false
    // Plugin Kotlin Multiplatform (compartilha código entre Android e iOS)
    alias(libs.plugins.kotlinMultiplatform) apply false
    // Plugin Compose Multiplatform (UI compartilhada)
    alias(libs.plugins.composeMultiplatform) apply false
    // Plugin do compilador Compose
    alias(libs.plugins.composeCompiler) apply false
    // Plugin de serialização Kotlin (para JSON seguro)
    alias(libs.plugins.kotlinSerialization) apply false
    // Plugin Google Services (necessário para Firebase)
    alias(libs.plugins.googleServices) apply false
}
