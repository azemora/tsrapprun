import SwiftUI
import GoogleSignIn
import shared

@main
struct iOSApp: App {
    /// Mantém os handlers vivos enquanto o app roda — bridges guardam só weak refs.
    private let cameraHandler: EventCameraHandler
    private let audioHandler: AudioRecorderHandler
    private let photoPickerHandler: PhotoPickerHandler
    private let notificationScheduler: MomentNotificationScheduler

    init() {
        let scheduler = MomentNotificationScheduler()
        cameraHandler = EventCameraHandler()
        audioHandler = AudioRecorderHandler()
        photoPickerHandler = PhotoPickerHandler()
        notificationScheduler = scheduler

        // Pede permissão na primeira execução e agenda os lembretes.
        // Captura `scheduler` local para evitar capturar `self` mutating no init.
        scheduler.requestAuthorizationIfNeeded { granted in
            if granted {
                scheduler.scheduleRecurring()
            }
        }

        guard
            let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
            let plist = NSDictionary(contentsOfFile: path),
            let clientId = plist["CLIENT_ID"] as? String
        else {
            assertionFailure("GoogleService-Info.plist missing CLIENT_ID")
            return
        }
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientId)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
