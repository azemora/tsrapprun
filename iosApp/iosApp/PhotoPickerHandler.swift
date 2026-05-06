import Foundation
import PhotosUI
import UIKit
import shared

// ╔══════════════════════════════════════════════════════════════╗
// ║  PhotoPickerHandler.swift — PHPicker + Compose bridge.       ║
// ║                                                              ║
// ║  Apresenta PHPickerViewController em cima da rootViewController,│
// ║  permite seleção múltipla, lê cada item como Data e devolve   ║
// ║  via callback Kotlin. Sem permissão de Photos exigida — o     ║
// ║  PHPicker roda fora do sandbox do app (iOS 14+).              ║
// ╚══════════════════════════════════════════════════════════════╝

final class PhotoPickerHandler: NSObject, PHPickerViewControllerDelegate {

    private var pendingCallback: (([Data], String?) -> KotlinUnit)?

    override init() {
        super.init()
        registerWithBridge()
    }

    private func registerWithBridge() {
        let bridge = IosPhotoPickerBridge.shared
        bridge.isAvailable = true
        bridge.onPick = { [weak self] _, callback in
            // eventId é tratado em Kotlin; aqui só entregamos os bytes.
            self?.present(callback: callback)
        }
    }

    private func present(callback: @escaping ([Data], String?) -> KotlinUnit) {
        guard let root = topMostViewController() else {
            _ = callback([], "Nenhuma view ativa pra apresentar o picker.")
            return
        }
        if pendingCallback != nil {
            _ = callback([], "Picker já em uso.")
            return
        }
        pendingCallback = callback

        var config = PHPickerConfiguration()
        config.filter = .images
        config.selectionLimit = 0   // 0 = sem limite (multi-seleção)
        config.preferredAssetRepresentationMode = .current

        let picker = PHPickerViewController(configuration: config)
        picker.delegate = self
        DispatchQueue.main.async {
            root.present(picker, animated: true)
        }
    }

    // MARK: - Delegate

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        let cb = pendingCallback
        pendingCallback = nil
        picker.dismiss(animated: true)

        if results.isEmpty {
            _ = cb?([], nil)
            return
        }

        var collected: [Data] = []
        let group = DispatchGroup()
        let lock = NSLock()

        for result in results {
            let provider = result.itemProvider
            guard provider.canLoadObject(ofClass: UIImage.self) else { continue }
            group.enter()
            provider.loadObject(ofClass: UIImage.self) { object, _ in
                defer { group.leave() }
                guard
                    let img = object as? UIImage,
                    let data = img.jpegData(compressionQuality: 0.9)
                else { return }
                lock.lock()
                collected.append(data)
                lock.unlock()
            }
        }
        group.notify(queue: .main) {
            _ = cb?(collected, nil)
        }
    }

    // MARK: - Helpers

    private func topMostViewController() -> UIViewController? {
        let scenes = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
        let window = scenes.flatMap { $0.windows }.first { $0.isKeyWindow }
            ?? scenes.flatMap { $0.windows }.first
        var top = window?.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}
