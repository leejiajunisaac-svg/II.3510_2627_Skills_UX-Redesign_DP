package com.example.swapcampus_isaaclee.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.swapcampus_isaaclee.domain.Annonce
import com.example.swapcampus_isaaclee.domain.Categorie
import com.example.swapcampus_isaaclee.domain.EtatAnnonce

@Composable
fun MarketplaceListingCard(
    annonce: Annonce,
    formattedPrice: String,
    languageTag: String = "fr",
    auditMode: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEn = languageTag.startsWith("en", ignoreCase = true)
    val haptic = LocalHapticFeedback.current
    var isLiked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CardPressScale"
    )

    val statusText = when (annonce.etat) {
        is EtatAnnonce.Disponible -> if (isEn) "Available" else "Disponible"
        is EtatAnnonce.Reservee -> if (isEn) "Reserved" else "Réservée"
        is EtatAnnonce.Vendue -> if (isEn) "Sold" else "Vendue"
    }

    val transition = updateTransition(targetState = annonce.etat, label = "StatusTransition")
    val badgeBg by transition.animateColor(label = "BadgeBg") { state ->
        when (state) {
            is EtatAnnonce.Disponible -> MaterialTheme.colorScheme.secondaryContainer
            is EtatAnnonce.Reservee -> MaterialTheme.colorScheme.errorContainer
            is EtatAnnonce.Vendue -> MaterialTheme.colorScheme.surfaceVariant
        }
    }
    val badgeFg by transition.animateColor(label = "BadgeFg") { state ->
        when (state) {
            is EtatAnnonce.Disponible -> MaterialTheme.colorScheme.onSecondaryContainer
            is EtatAnnonce.Reservee -> MaterialTheme.colorScheme.onErrorContainer
            is EtatAnnonce.Vendue -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    val badgeScale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) },
        label = "BadgeScale"
    ) { state ->
        if (state is EtatAnnonce.Disponible) 1.0f else 0.95f
    }

    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.22f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "LikeScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .auditTouchTarget(auditMode)
            .semantics(mergeDescendants = true) {
                contentDescription = "${annonce.titre}, $formattedPrice, $statusText, ${annonce.categorie.name}"
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = if (isEn) "Open listing ${annonce.titre}" else "Ouvrir l'annonce ${annonce.titre}"
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1:1 Product Photo Container with Vector Fallback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(categoryGradient(annonce.categorie))
            ) {
                // 1. Vector illustration renders immediately while loading or if offline
                ProductArtwork(
                    title = annonce.titre,
                    categorie = annonce.categorie,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )

                // 2. Null-safe Coil 3 AsyncImage loads real online photo when available
                annonce.photoUrl?.let { url ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. Single Floating Status Pill
                Surface(
                    shape = CircleShape,
                    color = badgeBg,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .scale(badgeScale)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = badgeFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // 4. Top-Right Favorite Button (>= 48dp touch target)
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .auditTouchTarget(auditMode)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isLiked) {
                                    if (isEn) "Remove from favorites" else "Retirer des favoris"
                                } else {
                                    if (isEn) "Add to favorites" else "Ajouter aux favoris"
                                },
                                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(18.dp)
                                    .scale(likeScale)
                            )
                        }
                    }
                }
            }

            // Product Metadata Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (annonce.etat is EtatAnnonce.Disponible)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = annonce.titre,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = localizedCategoryName(annonce.categorie, isEn),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ProductArtwork(
    title: String,
    categorie: Categorie,
    modifier: Modifier = Modifier
) {
    val lowerTitle = title.lowercase()
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        drawOval(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(w * 0.22f, h * 0.84f),
            size = Size(w * 0.56f, h * 0.08f)
        )

        when {
            lowerTitle.contains("calculatrice") || lowerTitle.contains("ti-") || categorie == Categorie.ELECTRONIQUE -> {
                val bodyW = w * 0.52f
                val bodyH = h * 0.72f
                val left = cx - bodyW / 2f
                val top = cy - bodyH / 2f

                drawRoundRect(
                    color = Color(0xFF2C3E50),
                    topLeft = Offset(left, top),
                    size = Size(bodyW, bodyH),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFFA3C9A8),
                    topLeft = Offset(left + bodyW * 0.14f, top + bodyH * 0.10f),
                    size = Size(bodyW * 0.72f, bodyH * 0.28f),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
                val graphPath = Path().apply {
                    moveTo(left + bodyW * 0.20f, top + bodyH * 0.30f)
                    quadraticBezierTo(
                        cx, top + bodyH * 0.12f,
                        left + bodyW * 0.80f, top + bodyH * 0.22f
                    )
                }
                drawPath(graphPath, color = Color(0xFF1E3A2F), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

                val cols = 4
                val rows = 4
                val padStartX = left + bodyW * 0.16f
                val padStartY = top + bodyH * 0.45f
                val btnW = (bodyW * 0.68f) / cols
                val btnH = (bodyH * 0.44f) / rows
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val isAccent = (r == rows - 1 && c == cols - 1)
                        drawRoundRect(
                            color = if (isAccent) Color(0xFFD92B2B) else Color(0xFFECF0F1),
                            topLeft = Offset(padStartX + c * btnW + 2.dp.toPx(), padStartY + r * btnH + 2.dp.toPx()),
                            size = Size(btnW - 4.dp.toPx(), btnH - 4.dp.toPx()),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )
                    }
                }
            }

            lowerTitle.contains("lampe") || categorie == Categorie.MOBILIER -> {
                val baseW = w * 0.42f
                val baseH = h * 0.08f
                drawRoundRect(
                    color = Color(0xFF34495E),
                    topLeft = Offset(cx - baseW / 2f, h * 0.76f),
                    size = Size(baseW, baseH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
                drawLine(
                    color = Color(0xFF5D6D7E),
                    start = Offset(cx - w * 0.05f, h * 0.76f),
                    end = Offset(cx - w * 0.18f, h * 0.48f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF5D6D7E),
                    start = Offset(cx - w * 0.18f, h * 0.48f),
                    end = Offset(cx + w * 0.06f, h * 0.26f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color(0xFFD92B2B),
                    radius = 4.dp.toPx(),
                    center = Offset(cx - w * 0.18f, h * 0.48f)
                )
                val lightCone = Path().apply {
                    moveTo(cx + w * 0.06f, h * 0.34f)
                    lineTo(cx + w * 0.30f, h * 0.65f)
                    lineTo(cx - w * 0.06f, h * 0.65f)
                    close()
                }
                drawPath(lightCone, color = Color(0xFFFDEBD0).copy(alpha = 0.65f))
                val shade = Path().apply {
                    moveTo(cx + w * 0.02f, h * 0.24f)
                    lineTo(cx + w * 0.24f, h * 0.36f)
                    lineTo(cx + w * 0.04f, h * 0.42f)
                    close()
                }
                drawPath(shade, color = Color(0xFFD92B2B))
            }

            categorie == Categorie.LIVRES -> {
                val bookW = w * 0.56f
                val bookH = h * 0.70f
                val left = cx - bookW / 2f
                val top = cy - bookH / 2f

                drawRoundRect(
                    color = Color(0xFF1B4F72),
                    topLeft = Offset(left + 4.dp.toPx(), top + 4.dp.toPx()),
                    size = Size(bookW, bookH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(left + 2.dp.toPx(), top + 2.dp.toPx()),
                    size = Size(bookW - 2.dp.toPx(), bookH - 2.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF2874A6),
                    topLeft = Offset(left, top),
                    size = Size(bookW - 4.dp.toPx(), bookH - 4.dp.toPx()),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRect(
                    color = Color(0xFF1A5276),
                    topLeft = Offset(left, top),
                    size = Size(bookW * 0.14f, bookH - 4.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(left + bookW * 0.26f, top + bookH * 0.24f),
                    size = Size(bookW * 0.54f, 6.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF85C1E9),
                    topLeft = Offset(left + bookW * 0.26f, top + bookH * 0.36f),
                    size = Size(bookW * 0.38f, 5.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }

            categorie == Categorie.COURS -> {
                val docW = w * 0.54f
                val docH = h * 0.68f
                val left = cx - docW / 2f
                val top = cy - docH / 2f

                drawRoundRect(
                    color = Color(0xFFE5E8E8),
                    topLeft = Offset(left + 6.dp.toPx(), top + 6.dp.toPx()),
                    size = Size(docW, docH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(left, top),
                    size = Size(docW, docH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFFD92B2B),
                    topLeft = Offset(left + docW * 0.16f, top + docH * 0.18f),
                    size = Size(docW * 0.45f, 6.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
                for (i in 0..3) {
                    drawRoundRect(
                        color = Color(0xFFBDC3C7),
                        topLeft = Offset(left + docW * 0.16f, top + docH * (0.34f + i * 0.13f)),
                        size = Size(docW * (if (i == 3) 0.48f else 0.68f), 4.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }

            else -> {
                drawCircle(
                    color = Color(0xFF006B5E),
                    radius = w * 0.28f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.18f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

private fun localizedCategoryName(categorie: Categorie, isEn: Boolean): String = when (categorie) {
    Categorie.LIVRES -> if (isEn) "Books" else "Livres"
    Categorie.COURS -> if (isEn) "Lecture Notes" else "Cours"
    Categorie.ELECTRONIQUE -> if (isEn) "Electronics" else "Électronique"
    Categorie.SPORT_LOISIRS -> if (isEn) "Sports & Leisure" else "Sport & Loisirs"
    Categorie.MOBILIER -> if (isEn) "Furniture" else "Mobilier"
    Categorie.VETEMENTS -> if (isEn) "Apparel" else "Vêtements"
    Categorie.AUTRE -> if (isEn) "Other" else "Autre"
}

private fun categoryGradient(categorie: Categorie): Brush {
    val colors = when (categorie) {
        Categorie.LIVRES -> listOf(Color(0xFFE5F2F8), Color(0xFFD6E6F2))
        Categorie.COURS -> listOf(Color(0xFFF3EFEA), Color(0xFFE7DFD5))
        Categorie.ELECTRONIQUE -> listOf(Color(0xFFE8EEF5), Color(0xFFD9E3F0))
        Categorie.SPORT_LOISIRS -> listOf(Color(0xFFE4F5EE), Color(0xFFD0ECE1))
        Categorie.MOBILIER -> listOf(Color(0xFFF6EFEA), Color(0xFFEADDD4))
        Categorie.VETEMENTS -> listOf(Color(0xFFF5ECF3), Color(0xFFE8DAE5))
        Categorie.AUTRE -> listOf(Color(0xFFEEF0F3), Color(0xFFE0E3E8))
    }
    return Brush.verticalGradient(colors)
}

