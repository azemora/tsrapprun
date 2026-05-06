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

import kotlinx.coroutines.flow.MutableStateFlow

object IosNotificationBridge {

    /**
     * Ação pendente vinda de tap em notificação. Compose observa via StateFlow
     * e dispara navegação correspondente. Setado pelo Swift delegate (via
     * `setPendingAction`); limpo pelo App.kt após handle.
     */
    val pendingActionFlow = MutableStateFlow<NotificationAction?>(null)

    /** Setter exposto pra Swift (K/N expõe `value` como get-only no MutableStateFlow). */
    fun setPendingAction(action: NotificationAction?) {
        pendingActionFlow.value = action
    }

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

    /** Notificação de teste — preview do mesversário (firstName usado no texto). */
    var onTestMesversario: (firstName: String) -> Unit = {}

    /** Notificação de teste — preview do aniversário (firstName usado no texto). */
    var onTestAniversario: (firstName: String) -> Unit = {}
}
