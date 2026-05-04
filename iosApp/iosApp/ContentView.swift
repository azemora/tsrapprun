import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    let bridge: IosAuthBridge
    let storage: LocalPhotoStorage

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(authBridge: bridge, storage: storage)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @StateObject private var session = AppSession()

    var body: some View {
        ComposeView(bridge: session.bridge, storage: session.storage)
            // Sem `.ignoresSafeArea(.all)` — UI respeita notch e home indicator.
            // Telas que querem aparência full-bleed (câmera) podem fazer isso
            // dentro do Compose; o background preto do app preenche a área
            // de safe area visualmente.
            .background(Color.black.ignoresSafeArea())
            .onAppear { session.bootstrap() }
    }
}

private final class AppSession: ObservableObject {
    let bridge: IosAuthBridge
    let storage: LocalPhotoStorage
    private let authHandler: AuthHandler
    private var bootstrapped = false

    init() {
        let bridge = IosAuthBridge()
        let handler = AuthHandler(bridge: bridge)
        bridge.onSignInClick = { [weak handler] in handler?.signIn() }
        bridge.onSignOutClick = { [weak handler] in handler?.signOut() }
        self.bridge = bridge
        self.authHandler = handler
        self.storage = LocalPhotoStorage()
    }

    func bootstrap() {
        guard !bootstrapped else { return }
        bootstrapped = true
        authHandler.bootstrap()
    }
}
