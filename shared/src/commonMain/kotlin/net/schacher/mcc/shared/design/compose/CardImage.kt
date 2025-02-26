package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import io.kamel.core.DataSource
import io.kamel.core.Resource
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.ResourceConfig
import io.kamel.core.fetcher.Fetcher
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import org.koin.compose.koinInject
import kotlin.reflect.KClass

@Composable
fun CardImage(
    cardCode: String,
    filterQuality: FilterQuality = FilterQuality.High,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onLoading: @Composable (BoxScope.(Float) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float>? = null,
    databaseDao: CardDatabaseDao = koinInject(),
    marvelCDbDataSource: MarvelCDbDataSource = koinInject()
) {
    CompositionLocalProvider(
        LocalKamelConfig provides getKamelConfig(
            databaseDao,
            marvelCDbDataSource
        )
    ) {
        KamelImage(
            resource = asyncPainterResource(
                data = cardCode,
                filterQuality = filterQuality
            ),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            onLoading = onLoading,
            onFailure = onFailure,
            contentAlignment = contentAlignment,
            animationSpec = animationSpec,
        )
    }
}

fun getKamelConfig(
    databaseDao: CardDatabaseDao,
    marvelCDbDataSource: MarvelCDbDataSource
) = KamelConfig {
//    takeFrom(KamelConfig.Default)
    imageBitmapDecoder()

    fetcher(object : Fetcher<String> {
        override val inputDataKClass: KClass<String>
            get() = String::class
        override val source: DataSource
            get() = DataSource.Memory
        override val String.isSupported: Boolean
            get() = true

        override fun fetch(
            data: String,
            resourceConfig: ResourceConfig
        ): Flow<Resource<ByteReadChannel>> = flow {
            var cardImage = databaseDao.getCardImage(data)
            if (cardImage == null) {
                cardImage = marvelCDbDataSource.getCardImage(data).getOrNull()?.also {
                    databaseDao.addCardImage(data, it)
                }
            }

            if (cardImage != null) {
                val byteReadChannel = ByteReadChannel(cardImage)
                emit(Resource.Success(byteReadChannel, source))
            } else {
                emit(Resource.Failure(Exception("Failed to load image")))
            }
        }
    })
}