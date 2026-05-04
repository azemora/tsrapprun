/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  IosNotificationBridge — ponte Compose ↔ Swift               ║
 * ║                                                              ║
 * ║  Notificações locais são gerenciadas em Swift via            ║
 * ║  UNUserNotificationCenter. Esta bridge expõe os hooks que    ║
 * ║  Compose chama (botão "testar notificação", scheduling).    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

object IosNotificationBridge {
    /** Pede permissão (se necessário) e dispara uma notificação em ~5s. */
    var onTestNotification: () -> Unit = {}

    /** (Re)agenda os lembretes recorrentes diário/semanal. */
    var onScheduleRecurring: () -> Unit = {}

    /**
     * Agenda os lembretes específicos da criança:
     *  - semanal (cada nova semana de vida)
     *  - mesversário (cada novo mês completo, até 12 meses)
     *
     * Em modo simulação: 1 min = 1 semana, 4 min = 1 mês.
     */
    var onScheduleChildNotifications: (firstName: String, birthdateMillis: Long) -> Unit = { _, _ -> }
}
