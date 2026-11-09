package com.example.acod.ui.screens.discover_homes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.acod.data.Property
import com.example.acod.data.PropertyImageStorage
import com.example.acod.data.getImageFileNames
import java.io.File
import java.text.NumberFormat
import java.util.Locale

private enum class ListingFilter(val label: String) {
    ALL("All"), RENT("Rent"), SALE("For sale")
}

/** Displays locally saved listings. The caller supplies the Room-backed property list. */
@Composable
fun DiscoverHomesScreen(
    properties: List<Property>,
    modifier: Modifier = Modifier,
    onPropertySelected: (Property) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ListingFilter.ALL) }

    val visibleProperties = remember(properties, searchText, selectedFilter) {
        properties.filter { property ->
            val matchesSearch = searchText.isBlank() ||
                property.title.contains(searchText, ignoreCase = true) ||
                property.address.contains(searchText, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                ListingFilter.ALL -> true
                ListingFilter.RENT -> property.listingType.equals("RENT", ignoreCase = true)
                ListingFilter.SALE -> property.listingType.equals("SALE", ignoreCase = true)
            }
            matchesSearch && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Discover homes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Find a place that feels like home.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Search homes") },
            placeholder = { Text("Search by title or location") },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ListingFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.label) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (visibleProperties.isEmpty()) {
            EmptyListings(searching = properties.isNotEmpty() && searchText.isNotBlank())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(visibleProperties, key = { it.id }) { property ->
                    PropertyListingCard(
                        property = property,
                        onClick = { onPropertySelected(property) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyListingCard(
    property: Property,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageFile: File? = property.getImageFileNames().firstOrNull()?.let { fileName ->
        PropertyImageStorage.getImageFile(context, fileName).takeIf { it.exists() }
    }
    val isSale = property.listingType.equals("SALE", ignoreCase = true)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (imageFile != null) {
                AsyncImage(
                    model = imageFile,
                    contentDescription = "Photo of ${property.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🏠", style = MaterialTheme.typography.displaySmall)
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = if (isSale) "For sale" else "For rent",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatPrice(property.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!isSale && property.paymentDuration.isNotBlank()) {
                    Text(
                        text = "${property.paymentDuration} payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListings(searching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🏡", style = MaterialTheme.typography.displaySmall)
        Text(
            text = if (searching) "No homes match your search" else "No homes listed yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = if (searching) "Try another title or location." else "Saved properties will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatPrice(price: Double): String {
    val amount = NumberFormat.getNumberInstance(Locale("en", "ZM")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }.format(price)
    return "K$amount"
}
