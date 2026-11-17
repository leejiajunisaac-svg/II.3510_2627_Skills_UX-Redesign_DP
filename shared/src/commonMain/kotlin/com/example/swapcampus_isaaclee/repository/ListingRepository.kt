package com.example.swapcampus_isaaclee.repository

import com.example.swapcampus_isaaclee.domain.Annonce
import com.example.swapcampus_isaaclee.domain.CatalogueAnnonces
import com.example.swapcampus_isaaclee.domain.Categorie
import com.example.swapcampus_isaaclee.domain.EtatAnnonce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ListingRepository {
    val listings: StateFlow<List<Annonce>>
    fun getAllListings(): List<Annonce>
    fun getListingById(id: Int): Annonce?
    fun searchListings(query: String?, categorie: Categorie?): List<Annonce>
    fun createListing(
        titre: String,
        description: String,
        prix: Double,
        categorie: Categorie,
        proprietaire: String,
        photoUrl: String? = null
    ): Annonce
    fun postListing(
        id: Int,
        titre: String,
        description: String,
        prix: Double,
        categorie: Categorie,
        proprietaire: String,
        photoUrl: String? = null
    ): Annonce
    fun changeStatus(id: Int, newStatus: EtatAnnonce): Annonce?
}

class InMemoryListingRepository(
    private val catalogue: CatalogueAnnonces = CatalogueAnnonces()
) : ListingRepository {
    private val _listings = MutableStateFlow<List<Annonce>>(emptyList())
    override val listings: StateFlow<List<Annonce>> = _listings.asStateFlow()

    init {
        seedInitialListings()
    }

    private fun seedInitialListings() {
        catalogue.publierAnnonce(
            id = 1,
            titre = "Livre Algorithmique (Cormen)",
            description = "Édition complète en excellent état, idéal pour le cursus I2.",
            prix = 25.0,
            categorie = Categorie.LIVRES,
            proprietaire = "Isaac",
            photoUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80"
        )
        catalogue.publierAnnonce(
            id = 2,
            titre = "Calculatrice TI-84 Plus CE",
            description = "Avec câble de recharge USB et étui de protection rigide.",
            prix = 45.0,
            categorie = Categorie.ELECTRONIQUE,
            proprietaire = "Lucas",
            photoUrl = "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=600&q=80"
        )
        catalogue.publierAnnonce(
            id = 3,
            titre = "Annales de Maths & Fiches S1",
            description = "Sujets corrigés et fiches de révision reliées.",
            prix = 10.0,
            categorie = Categorie.COURS,
            proprietaire = "Chloé",
            photoUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=600&q=80"
        )
        catalogue.publierAnnonce(
            id = 4,
            titre = "Lampe de bureau LED articulée",
            description = "3 températures de lumière réglables, parfait pour le studio.",
            prix = 35.0,
            categorie = Categorie.MOBILIER,
            proprietaire = "Camille",
            photoUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=600&q=80"
        )
        catalogue.publierAnnonce(
            id = 5,
            titre = "Raquette de Tennis Wilson",
            description = "Grip neuf, housse incluse, peu utilisée.",
            prix = 50.0,
            categorie = Categorie.SPORT_LOISIRS,
            proprietaire = "Damiano",
            photoUrl = "https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&w=600&q=80"
        )
        catalogue.publierAnnonce(
            id = 6,
            titre = "Sweat à capuche Campus ISEP",
            description = "Taille M, coton épais bleu marine, très bon état.",
            prix = 20.0,
            categorie = Categorie.VETEMENTS,
            proprietaire = "Ange",
            photoUrl = "https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=600&q=80"
        )
        refresh()
    }

    override fun getAllListings(): List<Annonce> = catalogue.toutesLesAnnonces()

    override fun getListingById(id: Int): Annonce? =
        catalogue.toutesLesAnnonces().find { it.id == id }

    override fun searchListings(query: String?, categorie: Categorie?): List<Annonce> {
        return catalogue.toutesLesAnnonces().filter { annonce ->
            val matchesQuery = query.isNullOrBlank() ||
                    annonce.titre.contains(query, ignoreCase = true) ||
                    annonce.description.contains(query, ignoreCase = true)
            val matchesCategory = categorie == null || annonce.categorie == categorie
            matchesQuery && matchesCategory
        }
    }

    override fun createListing(
        titre: String,
        description: String,
        prix: Double,
        categorie: Categorie,
        proprietaire: String,
        photoUrl: String?
    ): Annonce {
        val nextId = (catalogue.toutesLesAnnonces().maxOfOrNull { it.id } ?: 0) + 1
        val created = catalogue.publierAnnonce(
            id = nextId,
            titre = titre,
            description = description,
            prix = prix,
            categorie = categorie,
            proprietaire = proprietaire,
            photoUrl = photoUrl
        )
        refresh()
        return created
    }

    override fun postListing(
        id: Int,
        titre: String,
        description: String,
        prix: Double,
        categorie: Categorie,
        proprietaire: String,
        photoUrl: String?
    ): Annonce {
        val created = catalogue.publierAnnonce(
            id = id,
            titre = titre,
            description = description,
            prix = prix,
            categorie = categorie,
            proprietaire = proprietaire,
            photoUrl = photoUrl
        )
        refresh()
        return created
    }

    override fun changeStatus(id: Int, newStatus: EtatAnnonce): Annonce? {
        val updated = catalogue.changerEtat(id, newStatus)
        refresh()
        return updated
    }

    private fun refresh() {
        _listings.value = catalogue.toutesLesAnnonces()
    }
}
