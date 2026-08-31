package com.dyor.habithero.presentation.screens.gallery

import com.dyor.habithero.domain.model.ComicCover

data class GalleryUiState(
    val comicCovers: List<ComicCover> = emptyList(),
    val selectedCoverIndex: Int? = null,
    val isLoading: Boolean = false,
)

sealed interface GalleryUiEvent {
    data class OnClickCover(val index: Int) : GalleryUiEvent
    data object OnDismissFullScreen : GalleryUiEvent
    data object OnClickGoToQuests : GalleryUiEvent
}
