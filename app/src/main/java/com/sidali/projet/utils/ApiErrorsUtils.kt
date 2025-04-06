package com.sidali.projet.utils

import android.app.Activity
import android.widget.Toast

// Fonction pour afficher un message Toast en fonction du code de réponse de l'API

fun Activity.showApiErrorToast(code: Int) {
    val message = when (code) {
        400 -> "Données invalides. Vérifiez les champs saisis."
        403 -> "Accès interdit. Veuillez vous reconnecter."
        404 -> "Utilisateur ou ressource introuvable."
        409 -> "Conflit : action déjà effectuée."
        500 -> "Erreur serveur. Réessayez plus tard."
        else -> "Erreur inattendue ($code)."
    }

    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
