package com.dyor.habithero.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.cover_hero
import com.dyor.habithero.generated.resources.cover_meditation
import com.dyor.habithero.generated.resources.cover_pumping_iron
import com.dyor.habithero.generated.resources.cover_read_20_mins
import org.jetbrains.compose.resources.painterResource

@Composable
fun ComicCoverImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    when {
        imageUrl.contains("read") || imageUrl.contains("cover_hero") || imageUrl.contains("cover1") -> {
            Image(
                painter = painterResource(Res.drawable.cover_read_20_mins),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
            )
        }

        imageUrl.contains("pumping") || imageUrl.contains("jogging") || imageUrl.contains("cover2") -> {
            Image(
                painter = painterResource(Res.drawable.cover_pumping_iron),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
            )
        }

        imageUrl.contains("meditation") || imageUrl.contains("cover3") -> {
            Image(
                painter = painterResource(Res.drawable.cover_meditation),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
            )
        }

        imageUrl.isNotBlank() -> {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
                error = painterResource(Res.drawable.cover_hero),
                placeholder = painterResource(Res.drawable.cover_hero),
            )
        }
    }
}
