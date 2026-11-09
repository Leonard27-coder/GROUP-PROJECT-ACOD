# ACOD — Rental Property Discovery App

ACOD is a local Android app concept for helping property seekers find rental accommodation and property owners advertise available homes. This foundation is organized around the four proposed screens so the team can implement and demonstrate them separately.

## Problem statement

Rental details such as availability, price, payment terms, location, and owner contact information are often scattered across sources. ACOD is intended to bring those details together so seekers can compare listings and locate properties, while owners can enter clear rental information.

## Target users

- **Property owners** add rental listings with descriptions, contact details, prices, and payment terms.
- **Property seekers** browse listings, compare rent and payment terms, view details, and open a property location.

## Proposed screens and package status

| Screen | Kotlin package | Foundation status |
|---|---|---|
| Login / Role Selection | `com.example.acod.ui.screens.role_selection` | Starter screen included. Name and role selection are local only. |
| Discover Homes | `com.example.acod.ui.screens.discover_homes` | Empty package reserved for team implementation. |
| Property Details | `com.example.acod.ui.screens.property_details` | Empty package reserved for team implementation. |
| Add Property | `com.example.acod.ui.screens.add_property` | Empty package reserved for team implementation. |

The empty packages contain `.gitkeep` files so Git preserves them. `MainActivity` currently opens the role-selection starter screen; the remaining proposed screens are not implemented in this foundation.

## Intended scope

- A local role-selection flow for property owners and seekers. It is not identity verification or secure authentication.
- Validated rental-property entry with title, address, description, contact number, price in Zambian kwacha, monthly or quarterly payment duration, and coordinates.
- A scrollable discovery list and property detail view backed by local Room storage.
- An advance amount calculation for monthly or quarterly payment terms.
- A map intent that opens coordinates in a compatible maps app. The app does not track the seeker’s live location.

## Technologies

- Kotlin and Jetpack Compose with Material 3
- AndroidX Navigation Compose
- Room for local persistence, with Kotlin coroutines and Flow
- Coil for Compose image loading
- Gradle Kotlin DSL and KSP

## Project structure

```text
app/src/main/java/com/example/acod/
├── MainActivity.kt                    # App entry point; opens the starter screen
├── data/                              # Room entity, DAO, database, repository, image storage
├── navigation/                        # Reserved package for app navigation
└── ui/
    ├── PropertyViewModel.kt            # Listing state and actions
    ├── screens/
    │   ├── role_selection/             # Starter screen included
    │   ├── discover_homes/             # Empty team package
    │   ├── property_details/            # Empty team package
    │   └── add_property/                # Empty team package
    └── theme/                          # Compose theme, colors, and typography
gradle/                                  # Gradle wrapper and dependency versions
```

## Setup and run

1. Install Android Studio with an Android SDK platform for API 37. The app's minimum supported Android version is API 24.
2. Open the project root in Android Studio and allow Gradle to sync.
3. Select an emulator or connected Android device running API 24 or later and run the `app` configuration.

The Gradle wrapper is included. On Windows, build with:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Research topics

- **Room Database:** use Android Room to save and retrieve listings locally so they remain after the app closes.
- **Location and Maps:** store latitude and longitude and open a compatible maps app through an Android map intent. Do not request or track the seeker’s live location.

## Project collaborators

| Name | Student ID |
|---|---:|
| Leonard Nyirenda | 2300797 |
| Kennedy Chibuye | 2420985 |
| Matthews Goma | 2300876 |
| Kitangala Mulenga | 2300732 |

## Current limitations

- Only the role-selection starter screen is present. Discover Homes, Property Details, and Add Property are intentionally empty packages for team implementation.
- Role selection stores no account or authentication data.
- The listing data layer is local; this foundation has no server or cross-device synchronization.
- Property entry, browsing, advance calculations, and map opening are proposed work and are not yet connected to the starter screen.
