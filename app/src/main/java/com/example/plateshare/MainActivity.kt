package com.example.plateshare

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.camera.core.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plateshare.ui.theme.PlateShareTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlin.random.Random


// Main Activity
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlateShareTheme {
                val windowSize = calculateWindowSizeClass(this)
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val innerPadding = PaddingValues() // Simplified, as Scaffold is now used inside NavHost
                    MyApplication(innerPadding, windowSize.widthSizeClass)
                }
            }
        }
    }
}

@Composable
fun MyApplication(innerPadding: PaddingValues, widthSizeClass: WindowWidthSizeClass)
{
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        // Pass the widthSizeClass to the main app composable
        PlateShareApp(widthSizeClass = widthSizeClass)
    }
}

// Routes for navigation
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DonorLogin : Screen("donor_login")
    object NgoLogin : Screen("ngo_login")
    object DonorSignUp : Screen("donor_signup")
    object NgoSignUp : Screen("ngo_signup")
    object AboutUs : Screen("about_us")
    object Donations : Screen("donations")
    object DonorDashboard : Screen("donor_dashboard")
    object AddDonation : Screen("add_donation")

}
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PlateShareApp(widthSizeClass: WindowWidthSizeClass) { // Receive widthSizeClass
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            // Pass the received widthSizeClass
            HomeScreen(navController = navController, widthSizeClass = widthSizeClass)
        }

        // Other composables...
        composable(Screen.DonorLogin.route) { LoginScreen(navController, userType = "Donor") }
        composable(Screen.NgoLogin.route) { LoginScreen(navController, userType = "NGO") }
        composable(Screen.DonorSignUp.route) { DonorSignUpScreen(navController) }
        composable(Screen.NgoSignUp.route) { NgoSignUpScreen(navController) }
        composable(Screen.AboutUs.route) { AboutUsScreen(navController) }

        // This line was already correct and serves as a good example
        composable(Screen.Donations.route) {
            // Pass the received widthSizeClass
            DonationsScreen(navController, widthSizeClass)
        }
        composable(Screen.DonorDashboard.route) {
            DonorDashboardScreen(navController = navController)
        }
        composable(Screen.AddDonation.route) {
            AddDonationScreen(navController = navController)
        }
    }
}
// Top bar composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PlateShareTopBar(navController: NavController) {
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    // TopBar can calculate its own size class as it's a self-contained component
    val windowSize = calculateWindowSizeClass(context as Activity)
    val isCompact = windowSize.widthSizeClass == WindowWidthSizeClass.Compact
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.5f)) {
                // Replace with your logo
                Icon(
                    painter = painterResource(id = R.drawable.plateshare_logo),
                    contentDescription = "PlateShare Logo",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified // Keep original colors of the logo
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isCompact) "" else "PlateShare",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (currentRoute == Screen.Home.route) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Home") },
                        onClick = { navController.navigate(Screen.Home.route); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("About Us") },
                        onClick = { navController.navigate(Screen.AboutUs.route); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Donations") },
                        onClick = { navController.navigate(Screen.Donations.route); showMenu = false }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Donor Login") },
                        onClick = { navController.navigate(Screen.DonorLogin.route); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("NGO Login") },
                        onClick = { navController.navigate(Screen.NgoLogin.route); showMenu = false }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFDECDD),
                    Color(0xFFFCF5ED)
                )
            )
        )
    )
}
// Home Screen composable
@Composable
fun HomeScreen(navController: NavController, widthSizeClass: WindowWidthSizeClass) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFCF5ED)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { PlateShareTopBar(navController) }
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF5C282))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Share a Plate,",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6E4E3A)
                    )
                    Text(
                        text = "Share a Smile",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6E4E3A)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Connecting generous donors with surplus food to NGOs who feed the hungry. Together, we can reduce food waste and fight hunger.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6E4E3A)
                    )
                    Spacer(Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate(Screen.DonorLogin.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5734A)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Donate Food", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Donations.route) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6E4E3A)),
                            border = BorderStroke(
                                1.dp, Color(0xFF6E4E3A)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Find Food", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "How It Works",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E4E3A)
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            // Add more content here, for example, steps for how it works
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HowItWorksStep("1.", "Donors list surplus food.")
                HowItWorksStep("2.", "NGOs browse and claim donations.")
                HowItWorksStep("3.", "Food is picked up and distributed.")
            }
        }
        item {
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(Screen.DonorSignUp.route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5734A)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("Join Our Community", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "About Us",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E4E3A)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "PlateShare is a platform dedicated to reducing food waste and fighting hunger. We connect businesses and individuals with surplus food to non-profit organizations that can distribute it to those in need. Our mission is to create a sustainable solution to food insecurity by making it easy for donors to share and for NGOs to receive.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6E4E3A)
            )
        }
        item {
            Spacer(Modifier.height(32.dp))
            Text("© 2024 PlateShare. All Rights Reserved.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
        }
    }
}
@Composable
fun HowItWorksStep(number: String, text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(number, fontWeight = FontWeight.Bold, color = Color(0xFFE5734A), style = MaterialTheme.typography.bodyLarge)
        Text(text, color = Color(0xFF6E4E3A), style = MaterialTheme.typography.bodyLarge)
    }
}
// Login Screen composable
@Composable
fun LoginScreen(navController: NavController, userType: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Scaffold(
        topBar = { PlateShareTopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDECDD))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plateshare_logo),
                        contentDescription = "PlateShare Logo",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "$userType Login",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Welcome back! Please enter your details.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Dummy login logic: just navigate to a relevant screen
                            if (userType == "Donor") {
                                navController.navigate(Screen.DonorDashboard.route)
                            } else { // NGO
                                navController.navigate(Screen.Donations.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Login")
                    }
                    Spacer(Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            navController.navigate(
                                if (userType == "Donor") Screen.DonorSignUp.route
                                else Screen.NgoSignUp.route
                            )
                        }
                    ) {
                        Text(
                            text = "Don't have an account? Sign Up",
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

// Donor Sign Up Screen composable
@Composable
fun DonorSignUpScreen(navController: NavController) {
    SignUpScreen(navController, "Donor")
}

// NGO Sign Up Screen composable
@Composable
fun NgoSignUpScreen(navController: NavController) {
    SignUpScreen(navController, "NGO")
}

@Composable
fun SignUpScreen(navController: NavController, userType: String) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(topBar = { PlateShareTopBar(navController) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDECDD))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Create $userType Account",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Join our community to make a difference.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(Modifier.height(24.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (userType == "Donor") "Full Name" else "NGO Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                // Dummy sign-up logic: just navigate to the login screen after "signing up"
                                val loginRoute = if (userType == "Donor") Screen.DonorLogin.route else Screen.NgoLogin.route
                                navController.navigate(loginRoute)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Sign Up")
                        }
                        Spacer(Modifier.height(16.dp))
                        TextButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text(
                                text = "Already have an account? Login",
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }
        }
    }
}

// About Us Screen composable
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(topBar = { PlateShareTopBar(navController) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFFCF5ED)).padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Our Mission",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E4E3A)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "PlateShare is a platform dedicated to reducing food waste and fighting hunger. We connect businesses and individuals with surplus food to non-profit organizations that can distribute it to those in need. Our mission is to create a sustainable solution to food insecurity by making it easy for donors to share and for NGOs to receive.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6E4E3A)
                )
                Spacer(Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "Our Vision",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E4E3A)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "We envision a world where no food goes to waste and no one goes hungry. By leveraging technology, we aim to build a strong, compassionate community that actively participates in the fight against food insecurity, one shared plate at a time.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6E4E3A)
                )
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(onClick = { navController.navigate(Screen.Home.route) }) {
                    Text("Back to Home")
                }
            }
        }
    }
}

// Dummy data classes for donations
data class Donation(
    val id: String,
    val title: String,
    val donor: String,
    val distance: Double,
    val expiry: String,
    val type: String
)

val dummyDonations = List(10) {
    Donation(
        id = "D$it",
        title = "Fresh Vegetables & Bread #${it + 1}",
        donor = "KindHeart Bakery",
        distance = Random.nextDouble(1.0, 15.0),
        expiry = "${Random.nextInt(2, 24)} hours",
        type = listOf("FreshProduce", "BakedGoods").random()
    )
}

@Composable
fun DonorDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = { PlateShareTopBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddDonation.route) }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add Donation")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFCF5ED))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    "Your Donations",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E4E3A)
                )
                Text(
                    "Manage your active and past food listings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(24.dp))
            }

            if (dummyDonations.isEmpty()) {
                item {
                    Text("You have no active donations. Tap the '+' to add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                items(dummyDonations.size) { index ->
                    DonationCard(donation = dummyDonations[index], isDonorView = true)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

sealed class DonationListState {
    object Loading : DonationListState()
    data class Success(val donations: List<Donation>) : DonationListState()
    data class Error(val message: String) : DonationListState()
}


suspend fun fetchDonations(): DonationListState {
    delay(1500) // Simulate network delay
    // Simulate a random error
    return if (Random.nextBoolean()) {
        DonationListState.Success(dummyDonations.shuffled()) // Return shuffled list on success
    } else {
        DonationListState.Error("Failed to fetch donations: 404 Not Found. Please try again later.")
    }
}

@Composable
fun rememberDonationState(key: Any? = true): MutableState<DonationListState> {
    // **FIX:** Use remember(key) instead of rememberSaveable.
    // Fetched state should not be saved across process death; it should be re-fetched.
    // The `key` will correctly reset the state to Loading when it changes (e.g., on retry).
    val state = remember(key) { mutableStateOf<DonationListState>(DonationListState.Loading) }
    LaunchedEffect(key) {
        state.value = fetchDonations()
    }
    return state
}

// Data class to hold the state of all filters
data class FilterState(
    var location: String = "New York, NY",
    var maxDistance: Float = 10f,
    var priority: String = "Medium",
    val preferredFoodTypes: MutableSet<String> = mutableSetOf(),
    val rejectedFoodTypes: MutableSet<String> = mutableSetOf(),
    val allergens: MutableSet<String> = mutableSetOf()
) {
    // **FIX:** Add a companion object with a Saver to make FilterState saveable
    companion object {
        val Saver = androidx.compose.runtime.saveable.listSaver<FilterState, Any?>(
            save = {
                listOf(
                    it.location,
                    it.maxDistance,
                    it.priority,
                    it.preferredFoodTypes.toList(),
                    it.rejectedFoodTypes.toList(),
                    it.allergens.toList()
                )
            },
            restore = {
                FilterState(
                    location = it[0] as String,
                    maxDistance = it[1] as Float,
                    priority = it[2] as String,
                    preferredFoodTypes = (it[3] as List<String>).toMutableSet(),
                    rejectedFoodTypes = (it[4] as List<String>).toMutableSet(),
                    allergens = (it[5] as List<String>).toMutableSet()
                )
            }
        )
    }
}

// **FIX:** Removed unused and incorrect autoSaver function
// private fun <T : Any> autoSaver() = ...

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DonationsScreenPreview() {
    PlateShareTheme {
        DonationsScreen(
            navController = rememberNavController(),
            widthSizeClass = WindowWidthSizeClass.Compact
        )

    }
}
// Main Donations Screen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationsScreen(navController: NavController, widthSizeClass: WindowWidthSizeClass) {
    // **FIX:** Use rememberSaveable and provide the Saver for FilterState
    var filters by rememberSaveable(stateSaver = FilterState.Saver) { mutableStateOf(FilterState()) }
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact
    val sheetState = rememberModalBottomSheetState()
    var retryKey by remember { mutableStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { PlateShareTopBar(navController) }
    ) { innerPadding ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                FilterPanel(filters) { updatedFilters -> filters = updatedFilters }
            }
        }
        val donationState = rememberDonationState(retryKey)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFCF5ED))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Screen Title
            Text(
                "Find Available Food Donations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E4E3A)
            )
            Text(
                "Browse listings from generous donors in your area.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(Modifier.height(24.dp))

            if (isCompact) {
                // On mobile, show a filter button and the content below it
                Button(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Filter")
                    Spacer(Modifier.width(8.dp))
                    Text("Filter & Sort")
                }
                Spacer(Modifier.height(16.dp))
                DonationContent(donationState.value) { retryKey++ }
            } else {
                // On larger screens, show a two-panel layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(modifier = Modifier.weight(0.35f)) {
                        FilterPanel(filters) { updatedFilters -> filters = updatedFilters }
                    }
                    Box(modifier = Modifier.weight(0.65f)) {
                        DonationContent(donationState.value) { retryKey++ }
                    }
                }
            }
        }
    }
}


// Left Panel with all the filter options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(filters: FilterState, onFiltersChanged: (FilterState) -> Unit) {
    // **FIX:** Use rememberSaveable for transient state to survive rotation
    var location by rememberSaveable { mutableStateOf(filters.location) }
    var distance by rememberSaveable { mutableStateOf(filters.maxDistance) }
    val priorityOptions = listOf("High", "Medium", "Low")
    var selectedPriority by rememberSaveable { mutableStateOf(filters.priority) }

    // NOTE: The checkbox state is NOT saved here.
    // Making this map of MutableStates saveable is complex and requires
    // refactoring how CheckboxGrid and CheckboxRow work.
    val preferredFoodTypes = remember {
        listOf(
            "BakedGoods", "CookedFood", "Diabetic Sweets", "Grains and Cereals",
            "Non Diabetic Sweets", "Sea Food", "Spices and Condiments", "Beverages",
            "DairyProducts", "FreshProduce", "Meat", "Processed and Packaged",
            "Snacks", "Staple Foods"
        ).associateWith { mutableStateOf(it in filters.preferredFoodTypes) }
    }
    val rejectedFoodTypes = remember {
        listOf(
            "BakedGoods", "CookedFood", "Diabetic Sweets", "Grains and Cereals",
            "Non Diabetic Sweets", "Sea Food", "Spices and Condiments", "Beverages",
            "DairyProducts", "FreshProduce", "Meat", "Processed and Packaged",
            "Snacks", "Staple Foods"
        ).associateWith { mutableStateOf(it in filters.rejectedFoodTypes) }
    }
    val avoidAllergens = remember {
        listOf(
            "Crustacean shellfish", "Fish", "Sesame", "Tree Nuts",
            "Eggs", "Milk", "Soybeans", "Wheat"
        ).associateWith { mutableStateOf(false) }
    }

    fun applyFilters() {
        onFiltersChanged(
            filters.copy(
                location = location,
                maxDistance = distance,
                priority = selectedPriority,
                preferredFoodTypes = preferredFoodTypes.filter { it.value.value }.keys.toMutableSet(),
                rejectedFoodTypes = rejectedFoodTypes.filter { it.value.value }.keys.toMutableSet(),
                allergens = avoidAllergens.filter { it.value.value }.keys.toMutableSet()
            )
        )
    }

    fun resetFilters() {
        val defaultFilters = FilterState()
        location = defaultFilters.location
        distance = defaultFilters.maxDistance
        selectedPriority = defaultFilters.priority
        preferredFoodTypes.values.forEach { it.value = false }
        rejectedFoodTypes.values.forEach { it.value = false }
        avoidAllergens.values.forEach { it.value = false }
        onFiltersChanged(defaultFilters)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0277BD))
            .padding(16.dp)
    ) {
        item {
            Text(
                "Filter & Sort",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Your Location") },
                placeholder = { Text("New York, NY") },
                trailingIcon = { Icon(Icons.Default.Place, "Location Pin") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text("Max Distance (km)", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = distance,
                    onValueChange = { distance = it },
                    valueRange = 1f..50f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE5734A),
                        activeTrackColor = Color(0xFFE5734A)
                    )
                )
                Text(
                    "${distance.toInt()} km",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text("Donation Priority", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    RadioRow(text = "High", selected = selectedPriority == "High") { selectedPriority = "High" }
                    val selectedDso = null
                    RadioRow(text = "Low", selected = selectedDso == "Low") { selectedPriority = "Low" }
                }
                Column(Modifier.weight(1f)) {
                    RadioRow(text = "Medium", selected = selectedPriority == "Medium") { selectedPriority = "Medium" }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            CheckboxGrid(title = "Preferred Food Types", items = preferredFoodTypes)
            Spacer(Modifier.height(24.dp))
        }

        item {
            CheckboxGrid(title = "Rejected Food Types", items = rejectedFoodTypes)
            Spacer(Modifier.height(24.dp))
        }

        item {
            CheckboxGrid(title = "Avoid Allergens", items = avoidAllergens)
            Spacer(Modifier.height(32.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { applyFilters() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5734A)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                    Spacer(Modifier.width(8.dp))
                    Text("Apply Filters")
                }
                OutlinedButton(
                    onClick = { resetFilters() },
                    modifier = Modifier.weight(1f)
                ){


                    Icon(Icons.Default.Close, contentDescription = "Reset")
                    Spacer(Modifier.width(8.dp))
                    Text("Reset")
                }
            }
            Spacer(Modifier.height(16.dp)) // Add some padding at the bottom
        }
    }
}

@Composable
private fun RadioRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun CheckboxGrid(title: String, items: Map<String, MutableState<Boolean>>, initiallyExpanded: Boolean = false) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    val keys = items.keys.toList()
    val half = (keys.size + 1) / 2
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.weight(1f)) {
            for (i in 0 until half) {
                CheckboxRow(label = keys[i], state = items[keys[i]]!!)
            }
        }
        Column(Modifier.weight(1f)) {
            for (i in half until keys.size) {
                CheckboxRow(label = keys[i], state = items[keys[i]]!!)
            }
        }
    }
}

@Composable
private fun CheckboxRow(label: String, state: MutableState<Boolean>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { state.value = !state.value }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(checked = state.value, onCheckedChange = { state.value = it })
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}



// Right panel showing the donation list or an error
@Composable
fun DonationContent(state: DonationListState, onRetry: () -> Unit) {
    when (state) {
        is DonationListState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading Donations...")
            }
        }
        is DonationListState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.donations.size) { index ->
                    DonationCard(donation = state.donations[index])
                }
            }
        }
        is DonationListState.Error -> {
            ErrorStateContent(errorMessage = state.message, onRetry = onRetry)
        }
    }
}

@Composable
fun DonationCard(donation: Donation, isDonorView: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(donation.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("From: ${donation.donor}", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
            Spacer(Modifier.height(4.dp))
            Text("Expires in: ${donation.expiry}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { /* Implement view details logic */ }) {
                    Text("View Details")
                }
                if (isDonorView) {
                    Button(
                        onClick = { /* Implement edit donation logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5734A))
                    ) {
                        Text("Edit")
                    }
                } else {
                    Button(
                        onClick = { /* Implement claim donation logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5734A))
                    ) {
                        Text("Claim")
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorStateContent(errorMessage: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE), // A light red background
            contentColor = Color(0xFFB71C1C) // A dark red for text
        ),
        border = BorderStroke(1.dp, Color(0xFFD32F2F))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Error")
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Error Loading Donations", fontWeight = FontWeight.Bold)
                Text(errorMessage, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onRetry) {
                Text("Retry", color = Color(0xFF0D47A1))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDonationScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var foodType by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    val foodTypes = listOf("Fresh Produce", "Baked Goods", "Cooked Food", "Canned Goods", "Other")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { PlateShareTopBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFCF5ED))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Add a New Donation",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E4E3A)
                )
                Text(
                    "Fill out the details below to list your surplus food.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Donation Title") },
                    placeholder = { Text("e.g., Fresh Loaf of Bread") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe the food item") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("e.g., 2 loaves, 5 kg") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Best Before / Expiry Date") },
                    placeholder = { Text("e.g., DD/MM/YYYY or in 2 days") },
                    modifier = Modifier.fillMaxWidth(),
                    // **FIX:** Removed the trailingIcon to prevent crash
                    // from missing R.drawable.ic_menu_today
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = foodType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Food Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        foodTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { foodType = type; expanded = false }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        // Add validation and saving logic here
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit Donation")
                }
            }
        }
    }
}