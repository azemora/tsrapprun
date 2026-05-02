/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NavigationScreen.kt - Estados de Navegação                  ║
 * ║                                                             ║
 * ║  Sealed class que define todas as telas do app.             ║
 * ║  Substitui booleans por estados tipados para navegação.     ║
 * ║                                                             ║
 * ║  SEGURANÇA: Telas autenticadas só são acessíveis            ║
 * ║  quando AuthState é Authenticated (verificado em App.kt).  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.navigation

/**
 * Todas as telas do app.
 * Navegação controlada por estado — sem deeplinks ou rotas externas.
 */
sealed class NavigationScreen {
    /** Tela principal com perfil, botões e eventos recentes. */
    data object Home : NavigationScreen()

    /** Câmera avulsa (captura única). */
    data object Camera : NavigationScreen()

    /** Câmera contínua para registro de evento. */
    data object EventRegistration : NavigationScreen()

    /** Diálogo para nomear evento após captura contínua. */
    data class EventNaming(val photoIds: List<String>) : NavigationScreen()

    /** Galeria raiz: lista de eventos + "Todas as Fotos". */
    data object Gallery : NavigationScreen()

    /** Grid de fotos de um evento específico. */
    data class EventGallery(val eventId: String, val eventName: String) : NavigationScreen()

    /** Grid de todas as fotos (sem filtro de evento). */
    data object AllPhotos : NavigationScreen()

    /** Visualizador fullscreen com swipe entre fotos. */
    data class PhotoViewer(
        val initialIndex: Int,
        val eventId: String? = null
    ) : NavigationScreen()

    /** Diário "Memória do Dia" — abrir via notificação diária. */
    data class MemoryOfTheDay(val dateKey: String) : NavigationScreen()
}
