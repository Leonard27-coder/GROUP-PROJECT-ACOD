package com.example.acod.ui.screens.add_property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acod.data.Property
import java.text.NumberFormat
import java.util.Locale

private enum class PaymentDuration(val label: String, val months: Int) {
    MONTHLY("Monthly", 1),
    QUARTERLY("Quarterly (3 months in advance)", 3)
}

/** Rental listing form. Pass [onSaveProperty] to the caller's Room-backed ViewModel. */
@Composable
fun AddPropertyScreen(
    onSaveProperty: (Property) -> Unit,
    modifier: Modifier = Modifier,
    onPropertySaved: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var monthlyRentText by remember { mutableStateOf("") }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(PaymentDuration.MONTHLY) }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val monthlyRent = monthlyRentText.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Add a property",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Share the important details so people can find your rental.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PropertyField(
            value = title,
            onValueChange = { title = it },
            label = "Property title",
            error = errors["title"]
        )
        PropertyField(
            value = address,
            onValueChange = { address = it },
            label = "Address or area",
            error = errors["address"]
        )
        PropertyField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            error = errors["description"],
            singleLine = false,
            minLines = 3
        )
        PropertyField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = "Contact number",
            error = errors["contact"],
            keyboardType = KeyboardType.Phone
        )
        PropertyField(
            value = monthlyRentText,
            onValueChange = { monthlyRentText = it },
            label = "Monthly rent (ZMW)",
            error = errors["price"],
            keyboardType = KeyboardType.Decimal
        )

        Text(
            text = "Payment terms",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            PaymentDuration.entries.forEach { duration ->
                FilterChip(
                    selected = selectedDuration == duration,
                    onClick = { selectedDuration = duration },
                    label = { Text(duration.label) }
                )
            }
        }

        if (monthlyRent != null) {
            val advanceAmount = monthlyRent * selectedDuration.months
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Advance amount",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "K${formatAmount(advanceAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Text(
            text = "Property coordinates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PropertyField(
                value = latitudeText,
                onValueChange = { latitudeText = it },
                label = "Latitude",
                error = errors["latitude"],
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            PropertyField(
                value = longitudeText,
                onValueChange = { longitudeText = it },
                label = "Longitude",
                error = errors["longitude"],
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }

        errors["form"]?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                val validationErrors = validateProperty(
                    title = title,
                    address = address,
                    description = description,
                    contactNumber = contactNumber,
                    monthlyRentText = monthlyRentText,
                    latitudeText = latitudeText,
                    longitudeText = longitudeText
                )
                errors = validationErrors

                if (validationErrors.isEmpty()) {
                    onSaveProperty(
                        Property(
                            title = title.trim(),
                            address = address.trim(),
                            description = description.trim(),
                            contactNumber = contactNumber.trim(),
                            price = monthlyRentText.toDouble(),
                            paymentDuration = selectedDuration.label,
                            latitude = latitudeText.toDouble(),
                            longitude = longitudeText.toDouble(),
                            listingType = "RENT"
                        )
                    )
                    onPropertySaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save property", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PropertyField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        isError = error != null,
        supportingText = if (error == null) null else ({ Text(error) }),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp)
    )
}

private fun validateProperty(
    title: String,
    address: String,
    description: String,
    contactNumber: String,
    monthlyRentText: String,
    latitudeText: String,
    longitudeText: String
): Map<String, String> = buildMap {
    if (title.isBlank()) put("title", "Enter a property title.")
    if (address.isBlank()) put("address", "Enter the property address or area.")
    if (description.isBlank()) put("description", "Add a short description.")
    if (contactNumber.isBlank()) put("contact", "Enter a contact number.")

    val rent = monthlyRentText.toDoubleOrNull()
    if (rent == null || !rent.isFinite() || rent <= 0) {
        put("price", "Enter a monthly rent greater than zero.")
    }

    val latitude = latitudeText.toDoubleOrNull()
    if (latitude == null || !latitude.isFinite() || latitude !in -90.0..90.0) {
        put("latitude", "Enter a latitude from -90 to 90.")
    }

    val longitude = longitudeText.toDoubleOrNull()
    if (longitude == null || !longitude.isFinite() || longitude !in -180.0..180.0) {
        put("longitude", "Enter a longitude from -180 to 180.")
    }
}

private fun formatAmount(amount: Double): String = NumberFormat
    .getNumberInstance(Locale("en", "ZM"))
    .apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    .format(amount)
