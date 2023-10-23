package data

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Card

class CardRepository {

    private val _cards = MutableStateFlow(emptyList<Card>())

    val cards = _cards.asStateFlow()

    init {
        MainScope().launch {
            val result = KtorCardDataSource.getAllCards()
            _cards.emit(result)
        }
    }
}