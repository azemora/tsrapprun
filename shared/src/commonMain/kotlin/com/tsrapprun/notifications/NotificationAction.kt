/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NotificationAction.kt — payload de tap em notificação.      ║
 * ║                                                              ║
 * ║  Quando o usuário toca uma notificação, o iOS dispara o      ║
 * ║  delegate. O delegate parseia `userInfo` e expõe via bridge  ║
 * ║  uma NotificationAction. App.kt (Compose) observa e navega.  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

data class NotificationAction(
    /** Tipo da ação. Valores: open_registration, open_mesversario, open_anniversary. */
    val type: String,
    /** Mês do mesversário (1..12). Só usado em open_mesversario. */
    val month: Int = 0
) {
    companion object {
        const val OPEN_REGISTRATION = "open_registration"
        const val OPEN_MESVERSARIO = "open_mesversario"
        const val OPEN_ANNIVERSARY = "open_anniversary"
    }
}
