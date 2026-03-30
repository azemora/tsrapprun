/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  BookPageData.kt - Modelo de páginas do Livro de Memórias   ║
 * ║                                                             ║
 * ║  Mapeia eventos e fotos para páginas sequenciais.           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData

/**
 * Representa uma página do livro de memórias.
 */
sealed class BookPage {
    /** Capa do livro. */
    data object Cover : BookPage()

    /** Página de evento com fotos e metadados. */
    data class EventSpread(
        val event: EventData,
        val photos: List<PhotoData>
    ) : BookPage()

    /** Contracapa do livro. */
    data object BackCover : BookPage()
}

/**
 * Constrói a lista ordenada de páginas do livro.
 * Eventos em ordem cronológica (mais antigo primeiro).
 */
fun buildBookPages(
    events: List<EventData>,
    allPhotos: List<PhotoData>
): List<BookPage> {
    val pages = mutableListOf<BookPage>(BookPage.Cover)

    events.sortedBy { it.createdAt }.forEach { event ->
        val eventPhotos = allPhotos
            .filter { it.eventId == event.id }
            .sortedBy { it.capturedAt }
        pages.add(BookPage.EventSpread(event, eventPhotos))
    }

    pages.add(BookPage.BackCover)
    return pages
}
