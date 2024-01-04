package net.schacher.mcc.shared.database

import net.schacher.mcc.shared.model.Deck

interface DeckDatabaseDao {
    fun addDeck(deck: Deck)

    fun removeAllDecks()

    fun getDecks(): List<Deck>
    
    fun removeDeck(deckId: Int)
}