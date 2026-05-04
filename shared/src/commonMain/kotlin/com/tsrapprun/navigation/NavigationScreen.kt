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
    /** Nova tela inicial: reels, grid de eventos, botão Criar. */
    data object FrontPage : NavigationScreen()

    /** Tela de gerenciamento: perfil, usage, eventos, logout. */
    data object Home : NavigationScreen()

    /** Câmera avulsa (captura única). */
    data object Camera : NavigationScreen()

    /** Câmera contínua para registro de evento. */
    data object EventRegistration : NavigationScreen()

    /** Diálogo para nomear evento após captura contínua. */
    data class EventNaming(val photoIds: List<String>) : NavigationScreen()

    /** Lista completa de eventos (acessada via "Ver tudo"). */
    data object EventList : NavigationScreen()

    /** Galeria raiz: lista de eventos + "Todas as Fotos". */
    data object Gallery : NavigationScreen()

    /** Grid de fotos de um evento específico. */
    data class EventGallery(val eventId: String, val eventName: String) : NavigationScreen()

    /** Grid de todas as fotos (sem filtro de evento). */
    data object AllPhotos : NavigationScreen()

    /** Livro de Memórias — scrapbook com todos os eventos. */
    data object MemoryBook : NavigationScreen()

    /** Lista de registros de momentos (aba "Registros"). */
    data object MomentsList : NavigationScreen()

    /** Tela de registro de momento (diário ou semanal). */
    data class MomentRegistration(val type: String) : NavigationScreen()

    /** Visualizador fullscreen com swipe entre fotos. */
    data class PhotoViewer(
        val initialIndex: Int,
        val eventId: String? = null
    ) : NavigationScreen()

    /** Cadastro inicial / edição de perfil da criança. */
    data class ChildRegistration(val isEditing: Boolean = false) : NavigationScreen()

    /** Tela de comemoração de mesversário. */
    data class MesversarioAnnouncement(val monthsCompleted: Int) : NavigationScreen()
}
