package com.example.acod

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.acod.ui.screens.role_selection.RoleSelectionScreen
import com.example.acod.ui.screens.role_selection.UserRole

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RoleSelectionScreen(
                    onContinue = { name, role ->
                        val roleLabel = if (role == UserRole.PROPERTY_OWNER) {
                            "Property Owner"
                        } else {
                            "Property Seeker"
                        }
                        Toast.makeText(
                            this,
                            "Welcome, $name ($roleLabel)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
