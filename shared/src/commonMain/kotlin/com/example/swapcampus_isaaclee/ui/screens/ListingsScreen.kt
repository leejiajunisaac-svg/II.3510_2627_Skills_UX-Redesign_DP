package com.example.swapcampus_isaaclee.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.swapcampus_isaaclee.domain.Categorie
import com.example.swapcampus_isaaclee.ui.components.MarketplaceListingCard
import com.example.swapcampus_isaaclee.ui.components.auditTouchTarget
import com.example.swapcampus_isaaclee.ui.components.thumbZoneArcOverlay
import com.example.swapcampus_isaaclee.ui.i18n.currentLanguageTag
import com.example.swapcampus_isaaclee.ui.i18n.formatPrice
import com.example.swapcampus_isaaclee.ui.viewmodel.ListingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    uiState: ListingsUiState,
    onSearch: (String?, Categorie?) -> Unit,
    onListingClick: (Int) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val systemLang = currentLanguageTag()
    var activeLang by remember(systemLang) { mutableStateOf(if (systemLang.startsWith("en")) "en" else "fr") }
    val isEn = activeLang == "en"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categorie?>(null) }
    var auditMode by remember { mutableStateOf(false) }

    val listingCount = (uiState as? ListingsUiState.Success)?.listings?.size ?: 0

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .thumbZoneArcOverlay(auditMode),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Swap",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Campus",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = if (isEn) "$listingCount listings" else "$listingCount annonces",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Top-Right Action Pills: Language Switcher (FR/EN) + Accessibility X-Ray Toggle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { activeLang = if (isEn) "fr" else "en" },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .auditTouchTarget(auditMode)
                                .semantics {
                                    contentDescription = if (isEn) "Switch language to French" else "Passer la langue en anglais"
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEn) "EN" else "FR",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        FilledTonalIconToggleButton(
                            checked = auditMode,
                            onCheckedChange = { auditMode = it },
                            colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                                checkedContainerColor = Color(0xFF10B981),
                                checkedContentColor = Color.White
                            ),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .auditTouchTarget(auditMode)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessibilityNew,
                                contentDescription = if (isEn) "Toggle Accessibility X-Ray Audit" else "Activer l'audit d'accessibilité X-Ray"
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(top = 10.dp, bottom = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            onSearch(it.ifBlank { null }, selectedCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .defaultMinSize(minHeight = 48.dp)
                            .auditTouchTarget(auditMode),
                        placeholder = {
                            Text(if (isEn) "Search SwapCampus..." else "Rechercher sur SwapCampus...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = if (isEn) "Search" else "Rechercher",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        onSearch(null, selectedCategory)
                                    },
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        .auditTouchTarget(auditMode)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = if (isEn) "Clear search" else "Effacer la recherche"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = {
                                selectedCategory = null
                                onSearch(searchQuery.ifBlank { null }, null)
                            },
                            label = { Text(if (isEn) "All" else "Tout") },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .auditTouchTarget(auditMode)
                        )

                        Categorie.entries.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = if (selectedCategory == cat) null else cat
                                    onSearch(searchQuery.ifBlank { null }, selectedCategory)
                                },
                                label = {
                                    Text(
                                        when (cat) {
                                            Categorie.LIVRES -> if (isEn) "Books" else "Livres"
                                            Categorie.COURS -> if (isEn) "Notes" else "Cours"
                                            Categorie.ELECTRONIQUE -> if (isEn) "Electronics" else "Électronique"
                                            Categorie.SPORT_LOISIRS -> if (isEn) "Sports" else "Sport"
                                            Categorie.MOBILIER -> if (isEn) "Furniture" else "Mobilier"
                                            Categorie.VETEMENTS -> if (isEn) "Apparel" else "Vêtements"
                                            Categorie.AUTRE -> if (isEn) "Other" else "Autre"
                                        }
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .auditTouchTarget(auditMode)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (isEn) "Sell" else "Vendre", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .defaultMinSize(minHeight = 48.dp)
                    .auditTouchTarget(auditMode)
                    .semantics {
                        contentDescription = if (isEn) "Post a new listing" else "Publier une nouvelle annonce"
                    }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ListingsUiState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ListingsUiState.Success -> {
                    if (state.listings.isEmpty()) {
                        Text(
                            text = if (isEn) "No listings found." else "Aucune annonce trouvée.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 14.dp,
                                end = 14.dp,
                                top = 8.dp,
                                bottom = 104.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.listings, key = { it.id }) { item ->
                                MarketplaceListingCard(
                                    annonce = item,
                                    formattedPrice = formatPrice(item.prix, languageTag = activeLang),
                                    languageTag = activeLang,
                                    auditMode = auditMode,
                                    onClick = { onListingClick(item.id) }
                                )
                            }
                        }
                    }
                }
                is ListingsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}