import Foundation
import UserNotifications
import shared

// ╔══════════════════════════════════════════════════════════════╗
// ║  MomentNotificationScheduler.swift                           ║
// ║                                                              ║
// ║  Espelha MomentNotificationWorker (Android):                 ║
// ║   - lembrete diário às 20:00                                 ║
// ║   - lembrete semanal aos domingos às 18:00                   ║
// ║                                                              ║
// ║  Usa UNUserNotificationCenter (notificações locais — não     ║
// ║  precisa de servidor de push, funciona offline).             ║
// ╚══════════════════════════════════════════════════════════════╝

final class MomentNotificationScheduler {

    private let center = UNUserNotificationCenter.current()

    private static let dailyId = "moment_reminder_daily"
    private static let weeklyId = "moment_reminder_weekly"
    private static let testId = "moment_reminder_test"
    private static let childWeekIdPrefix = "child_week_"
    private static let childMonthIdPrefix = "child_month_"

    /// Em modo simulação: 1 minuto = 1 semana. Trocar em release.
    private let simulationFastForward = true

    init() {
        registerWithBridge()
    }

    // MARK: - Registro na bridge Compose

    private func registerWithBridge() {
        let bridge = IosNotificationBridge.shared
        bridge.onScheduleRecurring = { [weak self] in
            self?.requestAuthorizationIfNeeded { granted in
                if granted { self?.scheduleRecurring() }
            }
        }
        bridge.onTestNotification = { [weak self] in
            self?.requestAuthorizationIfNeeded { granted in
                if granted { self?.scheduleTest(after: 5) }
            }
        }
        bridge.onScheduleChildNotifications = { [weak self] firstName, birthdateMillis in
            self?.requestAuthorizationIfNeeded { granted in
                if granted {
                    self?.scheduleChildNotifications(
                        firstName: firstName as String,
                        birthdateMillis: birthdateMillis.int64Value
                    )
                }
            }
        }
    }

    // MARK: - Notificações da criança

    /// Agenda 12 lembretes semanais e 12 mesversários. Idempotente —
    /// remove os antigos antes de re-agendar.
    func scheduleChildNotifications(firstName: String, birthdateMillis: Int64) {
        // Cancela qualquer agendamento prévio dessa categoria
        cancelChildNotifications()

        // Modo simulação: 1h = 1 semana, 4h = 1 mês.
        let weekSeconds: TimeInterval = simulationFastForward ? 3600.0 : 7.0 * 24 * 60 * 60
        let monthSeconds: TimeInterval = simulationFastForward ? 14_400.0 : 30.44 * 24 * 60 * 60
        let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)
        let elapsedSec = TimeInterval((nowMs - birthdateMillis)) / 1000.0

        // Sanitiza nome (mesmo que Compose já tenha sanitizado, defesa em
        // profundidade — protege contra strings com caracteres exóticos).
        let safeName = sanitize(firstName)

        // Semanas: só agenda lembretes para as primeiras 4 semanas
        // (depois disso o ciclo vira mensal — mesversários).
        let currentWeek = Int(elapsedSec / weekSeconds)
        let firstMonthSec = monthSeconds
        for week in 1...4 {
            let targetWeek = week
            let triggerSec = TimeInterval(targetWeek) * weekSeconds - elapsedSec
            guard triggerSec > 0 else { continue }
            // Para se ultrapassar 1 mês de vida.
            if TimeInterval(targetWeek) * weekSeconds > firstMonthSec { break }
            // Já passou dessa semana?
            guard targetWeek > currentWeek else { continue }
            scheduleOneTime(
                identifier: "\(Self.childWeekIdPrefix)\(targetWeek)",
                title: "🌱 \(safeName) está com \(targetWeek) semana\(targetWeek == 1 ? "" : "s")",
                body: "registre o que aconteceu nessa semana.",
                interval: triggerSec
            )
        }

        // Mesversários: cap em 12 meses
        let currentMonth = Int(elapsedSec / monthSeconds)
        let monthsRemaining = max(0, 12 - currentMonth)
        for offset in 1...max(monthsRemaining, 1) {
            let targetMonth = currentMonth + offset
            if targetMonth > 12 { break }
            let triggerSec = TimeInterval(targetMonth) * monthSeconds - elapsedSec
            guard triggerSec > 0 else { continue }
            scheduleOneTime(
                identifier: "\(Self.childMonthIdPrefix)\(targetMonth)",
                title: "🎉 \(safeName) faz \(targetMonth) mês\(targetMonth == 1 ? "" : "es")!",
                body: "abra o app pra ver a comemoração.",
                interval: triggerSec
            )
        }
    }

    private func cancelChildNotifications() {
        center.getPendingNotificationRequests { requests in
            let ids = requests
                .map { $0.identifier }
                .filter {
                    $0.hasPrefix(Self.childWeekIdPrefix) ||
                    $0.hasPrefix(Self.childMonthIdPrefix)
                }
            self.center.removePendingNotificationRequests(withIdentifiers: ids)
        }
    }

    private func scheduleOneTime(
        identifier: String,
        title: String,
        body: String,
        interval: TimeInterval
    ) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: max(interval, 1),
            repeats: false
        )
        let request = UNNotificationRequest(
            identifier: identifier,
            content: content,
            trigger: trigger
        )
        center.add(request, withCompletionHandler: nil)
    }

    /// Remove caracteres de controle e limita comprimento — defesa em
    /// profundidade contra valores corrompidos chegando à API de
    /// notificações.
    private func sanitize(_ raw: String) -> String {
        let stripped = raw.unicodeScalars
            .filter { !($0.value < 0x20 || $0.value == 0x7F) }
            .map { String($0) }
            .joined()
        let trimmed = stripped.trimmingCharacters(in: .whitespacesAndNewlines)
        return String(trimmed.prefix(50))
    }

    // MARK: - Permissão

    func requestAuthorizationIfNeeded(_ completion: @escaping (Bool) -> Void) {
        center.getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized, .provisional, .ephemeral:
                DispatchQueue.main.async { completion(true) }
            case .notDetermined:
                self.center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
                    DispatchQueue.main.async { completion(granted) }
                }
            case .denied:
                DispatchQueue.main.async { completion(false) }
            @unknown default:
                DispatchQueue.main.async { completion(false) }
            }
        }
    }

    // MARK: - Agendamento

    /// Diário às 20h + semanal domingo às 18h. Idempotente — substitui
    /// requests anteriores com mesmo identifier.
    func scheduleRecurring() {
        // Diário
        var dailyComponents = DateComponents()
        dailyComponents.hour = 20
        dailyComponents.minute = 0

        let dailyTrigger = UNCalendarNotificationTrigger(
            dateMatching: dailyComponents,
            repeats: true
        )
        let dailyContent = UNMutableNotificationContent()
        dailyContent.title = "O que aconteceu hoje?"
        dailyContent.body = "Tire um momento para registrar o seu dia."
        dailyContent.sound = .default

        let dailyRequest = UNNotificationRequest(
            identifier: Self.dailyId,
            content: dailyContent,
            trigger: dailyTrigger
        )
        center.add(dailyRequest, withCompletionHandler: nil)

        // Semanal — domingo às 18h
        var weeklyComponents = DateComponents()
        weeklyComponents.weekday = 1 // 1 = domingo no calendário gregoriano da Apple
        weeklyComponents.hour = 18
        weeklyComponents.minute = 0

        let weeklyTrigger = UNCalendarNotificationTrigger(
            dateMatching: weeklyComponents,
            repeats: true
        )
        let weeklyContent = UNMutableNotificationContent()
        weeklyContent.title = "O que aconteceu essa semana?"
        weeklyContent.body = "Registre os momentos da sua semana antes que eles passem!"
        weeklyContent.sound = .default

        let weeklyRequest = UNNotificationRequest(
            identifier: Self.weeklyId,
            content: weeklyContent,
            trigger: weeklyTrigger
        )
        center.add(weeklyRequest, withCompletionHandler: nil)
    }

    /// Dispara uma notificação de teste após N segundos. Útil pra validar
    /// que a permissão foi concedida e o canal funciona sem esperar 20h.
    func scheduleTest(after seconds: TimeInterval) {
        let content = UNMutableNotificationContent()
        content.title = "🌿 teste de notificação"
        content.body = "se você tá vendo isso, tá funcionando!"
        content.sound = .default

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: max(seconds, 1),
            repeats: false
        )
        let request = UNNotificationRequest(
            identifier: Self.testId,
            content: content,
            trigger: trigger
        )
        center.add(request, withCompletionHandler: nil)
    }
}
