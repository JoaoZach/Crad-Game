package com.example.unogame.repositories

import com.example.unogame.models.Game
import com.example.unogame.ui.theme.game.UnoDeck
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GameRepository {

    private val db = FirebaseFirestore.getInstance()
    private val gamesCollection = db.collection("games")

    fun createGame(playerId: String, onSuccess: (String) -> Unit) {
        val gameRef = gamesCollection.document()
        val game = Game(
            gameId = gameRef.id,
            players = listOf(playerId),
            started = false
        )
        gameRef.set(game)
            .addOnSuccessListener { onSuccess(gameRef.id) }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun joinGame(gameId: String, playerId: String, onSuccess: () -> Unit) {
        val gameRef = gamesCollection.document(gameId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(gameRef)
            val players = snapshot.get("players") as? List<String> ?: emptyList()
            if (!players.contains(playerId)) {
                transaction.update(gameRef, "players", players + playerId)
            }
        }.addOnSuccessListener { onSuccess() }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun updateGame(game: Game) {
        gamesCollection.document(game.gameId)
            .set(game)
            .addOnFailureListener { it.printStackTrace() }
    }

    fun listenGame(gameId: String, onUpdate: (Game) -> Unit) {
        gamesCollection.document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                snapshot?.toObject(Game::class.java)?.let { onUpdate(it) }
            }
    }
}