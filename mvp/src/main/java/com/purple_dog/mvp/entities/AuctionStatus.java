package com.purple_dog.mvp.entities;

public enum AuctionStatus {
    PENDING, // En attente de validation du vendeur
    ACTIVE, // Enchères en cours
    EXTENDED, // Enchères prolongées (bataille à H-1)
    ENDED, // Enchères terminées
    SOLD, // Objet vendu (prix de réserve atteint)
    UNSOLD // Objet non vendu (prix de réserve non atteint)
}
