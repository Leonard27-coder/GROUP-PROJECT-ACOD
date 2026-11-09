package com.example.acod.ui.screens.property_details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.fillParentMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/** Shows a property's saved information, contact and map location. */
@Composable
fun PropertyDetailsScreen(
    property: Property,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSale = property.listingType.equals("SALE", ignoreCase = true)
    val imageFiles = property.getImageFileNames().mapNotNull { fileName ->
        PropertyImageStorage.getImageFile(context, fileName).takeIf(File::exists)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (imageFiles.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🏠", style = MaterialTheme.typography.displayMedium)
                            Text(
                                "No property photos",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                items(imageFiles, key = { it.name }) { imageFile ->
                    AsyncImage(
                        model = imageFile,
                        contentDescription = "Photo of ${property.title}",
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (isSale) "For sale" else "For rent",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Text(
                text = property.address,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "K${formatAmount(property.price)}${if (isSale) "" else " / month"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        DetailsCard(title = "About this property") {
            Text(
                text = property.description.ifBlank { "No description provided." },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (!isSale) {
            val advanceMonths = if (property.paymentDuration.contains("quarter", ignoreCase = true)) 3 else 1
            DetailsCard(title = "Payment terms") {
                DetailLine("Payment schedule", property.paymentDuration.ifBlank { "Not specified" })
                DetailLine("Monthly rent", "K${formatAmount(property.price)}")
                DetailLine(
                    "Advance amount",
                    "K${formatAmount(property.price * advanceMonths)}"
                )
            }
        }

        DetailsCard(title = "Contact the owner") {
            Text(
                text = property.contactNumber.ifBlank { "No contact number provided." },
                style = MaterialTheme.typography.bodyLarge
            )
            if (property.contactNumber.isNotBlank()) {
                OutlinedButton(
                    onClick = { openDialer(context, property.contactNumber) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Call owner")
                }
            }
        }

        DetailsCard(title = "Location") {
            DetailLine("Latitude", property.latitude.toString())
            DetailLine("Longitude", property.longitude.toString())
            Button(
                onClick = { openMap(context, property) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in maps")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DetailsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(value, fontWeight = FontWeight.Medium)
    }
}

private fun openMap(context: android.content.Context, property: Property) {
    val coordinates = "${property.latitude},${property.longitude}"
    val label = Uri.encode(property.title)
    val mapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:$coordinates?q=$coordinates($label)")
    )

    try {
        context.startActivity(mapIntent)
    } catch (_: ActivityNotFoundException) {
        val browserMapUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(coordinates)}"
        )
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, browserMapUri))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No maps app is available.", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openDialer(context: android.content.Context, contactNumber: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactNumber, null)))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "Unable to open the phone app.", Toast.LENGTH_SHORT).show()
    }
}

private fun formatAmount(amount: Double): String = NumberFormat
    .getNumberInstance(Locale("en", "ZM"))
    .apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    .format(amount)
