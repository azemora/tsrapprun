import Foundation
import UIKit
import GoogleSignIn
import shared

final class AuthHandler {
    let bridge: IosAuthBridge

    init(bridge: IosAuthBridge) {
        self.bridge = bridge
    }

    func bootstrap() {
        if GIDSignIn.sharedInstance.hasPreviousSignIn() {
            bridge.setLoading()
            GIDSignIn.sharedInstance.restorePreviousSignIn { [weak self] user, error in
                guard let self = self else { return }
                if let user = user {
                    self.publish(user: user)
                } else {
                    self.bridge.setUnauthenticated()
                }
            }
        } else {
            bridge.setUnauthenticated()
        }
    }

    func signIn() {
        bridge.setLoading()
        guard let presenter = Self.topViewController() else {
            bridge.setError(message: "Unable to present sign-in screen.")
            return
        }
        GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { [weak self] result, error in
            guard let self = self else { return }
            if let error = error as NSError? {
                if error.code == GIDSignInError.canceled.rawValue {
                    self.bridge.setUnauthenticated()
                } else {
                    self.bridge.setError(message: error.localizedDescription)
                }
                return
            }
            guard let user = result?.user else {
                self.bridge.setError(message: "Google did not return a user.")
                return
            }
            self.publish(user: user)
        }
    }

    func signOut() {
        GIDSignIn.sharedInstance.signOut()
        bridge.setUnauthenticated()
    }

    private func publish(user: GIDGoogleUser) {
        let profile = user.profile
        let photo = profile?.imageURL(withDimension: 200)?.absoluteString
        bridge.setAuthenticated(
            userId: user.userID ?? "",
            displayName: profile?.name,
            email: profile?.email,
            photoUrl: photo
        )
    }

    private static func topViewController() -> UIViewController? {
        let scene = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first { $0.activationState == .foregroundActive } ??
            UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .first
        guard var top = scene?.keyWindow?.rootViewController else { return nil }
        while let presented = top.presentedViewController {
            top = presented
        }
        return top
    }
}
    