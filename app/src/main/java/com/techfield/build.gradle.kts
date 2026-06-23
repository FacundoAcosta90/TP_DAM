// Compose BOM (OBLIGATORIO)
implementation(platform("androidx.compose:compose-bom:2024.10.00"))

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Room
implementation("androidx.room:room-runtime:2.7.2")
implementation("androidx.room:room-ktx:2.7.2")
ksp("androidx.room:room-compiler:2.7.2")

// CameraX
implementation("androidx.camera:camera-camera2:1.4.2")
implementation("androidx.camera:camera-lifecycle:1.4.2")
implementation("androidx.camera:camera-view:1.4.2")

// Coil
implementation("io.coil-kt:coil-compose:2.6.0")


