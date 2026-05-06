/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  IosPhotoPickerBridge — ponte para PHPicker do iOS.          ║
 * ║                                                              ║
 * ║  Swift apresenta o PHPickerViewController, lê os items como  ║
 * ║  Data e devolve a lista via callback. Compose dispara via    ║
 * ║  AppCallbacks.onImportPhotos / onImportPhotosToEvent.        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.photos

import platform.Foundation.NSData

object IosPhotoPickerBridge {
    /**
     * Abre o picker para seleção múltipla.
     *  - eventId: se não-null, fotos serão linkadas a esse evento ao salvar
     *  - callback: (data list, errorMessage)
     */
    var onPick: (eventId: String?, callback: (List<NSData>, String?) -> Unit) -> Unit = { _, cb ->
        cb(emptyList(), "Picker não configurado")
    }

    /** True se o handler Swift está registrado. */
    var isAvailable: Boolean = false
}
