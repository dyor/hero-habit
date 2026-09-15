package com.dyor.habithero.presentation.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.entity.toModel
import com.dyor.habithero.domain.model.HeroRole
import com.dyor.habithero.util.logging.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GalleryViewModel(
    comicCoverDao: ComicCoverDao,
) : ViewModel() {
    private val selectedIndexFlow = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<GalleryUiState> = combine(
        comicCoverDao.getAllFlow(),
        selectedIndexFlow,
    ) { entities, selectedIndex ->
        val covers = entities.map { it.toModel() }.filter { cover ->
            cover.imageUrl.isNotBlank() &&
                !cover.imageUrl.startsWith("selfie:") &&
                !cover.imageUrl.startsWith("temp:") &&
                cover.heroRole == HeroRole.SUPERHERO
        }
        GalleryUiState(
            comicCovers = covers,
            selectedCoverIndex = selectedIndex,
            isLoading = false,
        )
    }.catch { error ->
        AppLogger.e("Error loading comic covers: ${error.message}")
        emit(GalleryUiState(comicCovers = emptyList(), isLoading = false))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GalleryUiState(isLoading = true),
    )

    fun onUiEvent(event: GalleryUiEvent) {
        when (event) {
            is GalleryUiEvent.OnClickCover -> selectedIndexFlow.value = event.index
            GalleryUiEvent.OnDismissFullScreen -> selectedIndexFlow.value = null
            GalleryUiEvent.OnClickGoToQuests -> Unit
        }
    }
}
