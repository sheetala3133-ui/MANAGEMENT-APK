package com.example.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodels.GatePassViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    shape: androidx.compose.ui.graphics.Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = Color.Black),
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black.copy(alpha = 0.6f),
            errorTextColor = Color.Black,
            focusedBorderColor = CorporateNavy,
            focusedLabelColor = CorporateNavy,
            unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f),
            unfocusedLabelColor = TextMuted
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatePassAppContent(viewModel: GatePassViewModel) {
    val context = LocalContext.current
    val currentScreen = viewModel.activeScreen

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    "login" -> {
                        LoginView(viewModel)
                    }
                    "dashboard" -> {
                        DashboardView(viewModel)
                    }
                    else -> {
                        LoginView(viewModel)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SIGN-IN / LOGIN PORTAL SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(viewModel: GatePassViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Password, 1: biometric, 2: PIN
    var empId by remember { mutableStateOf("EMP101") }
    var password by remember { mutableStateOf("admin") }
    var pinCode by remember { mutableStateOf("1234") }
    var rememberMe by remember { mutableStateOf(true) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val employeesList by viewModel.employees.collectAsStateWithLifecycle()

    var showBypassMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CorporateNavy, CharcoalGray),
                    startY = 0f,
                    endY = 1200f
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Corporate Emblem Logo
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(2.dp, AmberAlert, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SafetyCheck,
                    contentDescription = "Logo Shield",
                    tint = AmberAlert,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SECURE GATEWAY",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = "Gate Pass, Meetings & Access Control Panel",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CoolWhite.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tab bar for Authentication Methods
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val tabs = listOf("Password", "Biometric", "PIN Code")
                    tabs.forEachIndexed { idx, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedTab == idx) SteelBlue else Color.Transparent
                                )
                                .clickable { selectedTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == idx) Color.White else CoolWhite.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main credentials input box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Authentication Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = empId,
                        onValueChange = { empId = it },
                        label = { Text("Employee / User ID") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "ID icon") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = CorporateNavy,
                            focusedLabelColor = CorporateNavy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedContent(targetState = selectedTab, label = "AuthInputFields") { tabIndex ->
                        when (tabIndex) {
                            0 -> { // PASSWORD
                                Column {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock icon") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedBorderColor = CorporateNavy,
                                            focusedLabelColor = CorporateNavy
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("password_input")
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = rememberMe,
                                            onCheckedChange = { rememberMe = it }
                                        )
                                        Text(
                                            text = "Remember Me on this device",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                        )
                                    }
                                }
                            }
                            1 -> { // BIOMETRIC
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(SteelBlue.copy(alpha = 0.1f))
                                            .clickable {
                                                viewModel.loginWithBiometric(empId) { success, msg ->
                                                    Toast
                                                        .makeText(context, msg, Toast.LENGTH_SHORT)
                                                        .show()
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = "Fingerprint icon",
                                            tint = SteelBlue,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Touch Fingerprint sensor to Authenticate",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                            2 -> { // SECURE PIN
                                OutlinedTextField(
                                    value = pinCode,
                                    onValueChange = { pinCode = it },
                                    label = { Text("4-Digit Security PIN") },
                                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = "PIN") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedBorderColor = CorporateNavy,
                                        focusedLabelColor = CorporateNavy
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                // Request simulated OTP
                                generatedOtp = (100000 + Random().nextInt(900000)).toString()
                                showOtpDialog = true
                            } else if (selectedTab == 2) {
                                viewModel.loginWithPin(empId, pinCode) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Text(
                            text = if (selectedTab == 0) "REQUEST OTP VERIFICATION" else "SECURE INITIALIZE",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick DEMO Bypass list for easy testing of active workflows and roles
            Text(
                text = "Demo Verification Quick Bypass Hub",
                color = CoolWhite.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val demoUsers = listOf(
                    "EMP101" to "Employee (Create Visitor / Gatepasses)",
                    "REC303" to "Receptionist (Register Incoming Visitors)",
                    "MGR404" to "Department Head (Approve Gate Passes)",
                    "SEC202" to "Security Guard (Scan QR Passes & Gate Logs)",
                    "ADM001" to "Admin (Reports & Audit Systems Log)"
                )

                demoUsers.forEach { (id, desc) ->
                    OutlinedButton(
                        onClick = { viewModel.bypassLogin(id) },
                        border = BorderStroke(1.dp, CoolWhite.copy(alpha = 0.2f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bypass_btn_$id")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.labelSmall,
                                color = CoolWhite
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Forward icon",
                                tint = AmberAlert,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // OTP SIMULATION DIALOG
    if (showOtpDialog) {
        Dialog(onDismissRequest = { showOtpDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sms,
                        contentDescription = "SMS OTP",
                        tint = SteelBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Simulated Mobile Notification",
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CoolWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "MESSAGING HUB: Verification PIN for GatePass App",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                generatedOtp,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberAlert,
                                letterSpacing = 4.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("Enter 6-Digit OTP") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = CorporateNavy,
                            focusedLabelColor = CorporateNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showOtpDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (enteredOtp == generatedOtp) {
                                    showOtpDialog = false
                                    viewModel.login(empId, password, rememberMe) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Incorrect OTP code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Verify")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. MAIN HUB PORTAL VIEW (DASHBOARD)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(viewModel: GatePassViewModel) {
    val user = viewModel.currentUser ?: return
    val activeTab = viewModel.dashboardTab

    var showNotificationsPanel by remember { mutableStateOf(false) }
    val notificationsList by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount = notificationsList.count { !it.isRead }

    var showDbConfigDialog by remember { mutableStateOf(false) }
    var tempServerType by remember(viewModel.dbServerType) { mutableStateOf(viewModel.dbServerType) }
    var tempServerUrl by remember(viewModel.dbServerUrl) { mutableStateOf(viewModel.dbServerUrl) }
    var tempDbName by remember(viewModel.dbName) { mutableStateOf(viewModel.dbName) }
    var tempDbPort by remember(viewModel.dbPort) { mutableStateOf(viewModel.dbPort) }
    var tempDbUsername by remember(viewModel.dbUsername) { mutableStateOf(viewModel.dbUsername) }
    var tempDbPassword by remember(viewModel.dbPassword) { mutableStateOf(viewModel.dbPassword) }
    var tempConnectionString by remember(viewModel.dbConnectionString) { mutableStateOf(viewModel.dbConnectionString) }
    var tempAutoCreateDb by remember(viewModel.dbAutoCreateDatabase) { mutableStateOf(viewModel.dbAutoCreateDatabase) }
    var tempAutoCreateTables by remember(viewModel.dbAutoCreateTables) { mutableStateOf(viewModel.dbAutoCreateTables) }
    var appCheckEnabled by remember(viewModel.firebaseAppCheckEnabled) { mutableStateOf(viewModel.firebaseAppCheckEnabled) }
    var authSecurityEnabled by remember(viewModel.firebaseAuthSecurityEnabled) { mutableStateOf(viewModel.firebaseAuthSecurityEnabled) }
    var dbSyncEnabled by remember(viewModel.firebaseDatabaseSyncEnabled) { mutableStateOf(viewModel.firebaseDatabaseSyncEnabled) }
    var passwordVisible by remember { mutableStateOf(false) }
    var connStringVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SafetyCheck,
                            contentDescription = "Shield Mini",
                            tint = AmberAlert,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "GatePass Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Zone Alpha - Manufacturing Complex",
                                style = MaterialTheme.typography.labelSmall,
                                color = CoolWhite.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    // Alert notification trigger
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { showNotificationsPanel = true }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Alert notifications",
                            tint = Color.White
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Logout trigger Button
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout icon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CorporateNavy)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(CleanWhite)
                    .navigationBarsPadding()
            ) {
                NavigationBar(
                    containerColor = CleanWhite,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == "meetings",
                        onClick = { viewModel.dashboardTab = "meetings" },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Meetings tab") },
                        label = { Text("Meetings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CorporateNavy,
                            indicatorColor = SteelBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = activeTab == "visitors",
                        onClick = { viewModel.dashboardTab = "visitors" },
                        icon = { Icon(Icons.Default.People, contentDescription = "Visitors tab") },
                        label = { Text("Visitors") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CorporateNavy,
                            indicatorColor = SteelBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = activeTab == "gatepasses",
                        onClick = { viewModel.dashboardTab = "gatepasses" },
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = "GatePass tab") },
                        label = { Text("Gate Passes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CorporateNavy,
                            indicatorColor = SteelBlue.copy(alpha = 0.15f)
                        )
                    )

                    if (user.role == "Security Guard" || user.role == "Admin") {
                        NavigationBarItem(
                            selected = activeTab == "scanner",
                            onClick = { viewModel.dashboardTab = "scanner" },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner tab") },
                            label = { Text("Guard Scan") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CorporateNavy,
                                indicatorColor = SteelBlue.copy(alpha = 0.15f)
                            )
                        )
                    }

                    if (user.role == "Admin" || user.role == "Department Head") {
                        NavigationBarItem(
                            selected = activeTab == "reports",
                            onClick = { viewModel.dashboardTab = "reports" },
                            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reports tab") },
                            label = { Text("Reports") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CorporateNavy,
                                indicatorColor = SteelBlue.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Start,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDbConfigDialog = true },
                containerColor = SteelBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 64.dp, start = 8.dp)
                    .size(44.dp)
                    .testTag("db_config_left_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = "Database Integration Setup",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CoolWhite)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // User Profile Context Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CorporateNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = TextNavy
                                )
                                Text(
                                    text = "${user.role} | ${user.department}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Compact badge for current task authorization
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AmberAlert.copy(alpha = 0.12f)),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "AUTH LEVEL " + when (user.role) {
                                    "Admin" -> "5 (ALL)"
                                    "Department Head" -> "4 (APPROVE)"
                                    "Security Guard" -> "3 (VERIFY)"
                                    "Receptionist" -> "2 (REGISTER)"
                                    else -> "1 (REQUEST)"
                                },
                                fontSize = 10.sp,
                                color = CorporateNavy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Body content based on active TAB
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when (activeTab) {
                        "meetings" -> MeetingsTab(viewModel)
                        "visitors" -> VisitorsTab(viewModel)
                        "gatepasses" -> GatePassesTab(viewModel)
                        "scanner" -> ScannerTab(viewModel)
                        "reports" -> ReportsTab(viewModel)
                    }
                }
            }

            // Real-time triggered notifications floating panel drawer
            if (showNotificationsPanel) {
                Dialog(onDismissRequest = { showNotificationsPanel = false }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications list",
                                        tint = CorporateNavy
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Push Activity Signals",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = CorporateNavy
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CoolWhite)
                                        .clickable { showNotificationsPanel = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        modifier = Modifier.size(16.dp),
                                        tint = CorporateNavy
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            if (notificationsList.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No alerts at present",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(notificationsList) { alert ->
                                        Card(
                                            border = BorderStroke(
                                                1.dp,
                                                if (alert.isRead) Color.Transparent else SteelBlue.copy(alpha = 0.3f)
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (alert.isRead) CoolWhite.copy(alpha = 0.6f) else SteelBlue.copy(alpha = 0.05f)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.markNotificationRead(alert.id) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (alert.category) {
                                                                "MEETING" -> AmberAlert.copy(alpha = 0.15f)
                                                                "VISITOR" -> MintGreen.copy(alpha = 0.15f)
                                                                "GATEPASS" -> ErrorRed.copy(alpha = 0.15f)
                                                                else -> SteelBlue.copy(alpha = 0.15f)
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when (alert.category) {
                                                            "MEETING" -> Icons.Default.CalendarMonth
                                                            "VISITOR" -> Icons.Default.Person
                                                            "GATEPASS" -> Icons.Default.LocalShipping
                                                            else -> Icons.Default.Info
                                                        },
                                                        contentDescription = "category icon",
                                                        tint = when (alert.category) {
                                                            "MEETING" -> CorporateNavy
                                                            "VISITOR" -> MintGreen
                                                            "GATEPASS" -> ErrorRed
                                                            else -> SteelBlue
                                                        },
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = alert.title,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextNavy,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = alert.body,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextMuted,
                                                        fontSize = 11.sp,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Database setup floating feature configuration dialog
            if (showDbConfigDialog) {
                Dialog(onDismissRequest = { showDbConfigDialog = false }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.85f),
                        border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Dns,
                                        contentDescription = "DB settings Icon",
                                        tint = CorporateNavy,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Enterprise Data Gateway",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = CorporateNavy
                                    )
                                }
                                IconButton(onClick = { showDbConfigDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close setup",
                                        tint = TextMuted
                                    )
                                }
                            }

                            Divider(color = SoftSlate.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))

                            // Scrollable settings body
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                if (user.role != "Admin") {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.05f)),
                                        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.2f)),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AdminPanelSettings,
                                                    contentDescription = "authorized",
                                                    tint = MintGreen,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Admin Access Bypass Enabled",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MintGreen,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "Granted write, read, collection data & sensor permissions for Android 5-13 standard devices.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextNavy,
                                                textAlign = TextAlign.Center,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                if (true) {
                                    // Full Admin database Setup Panel
                                    Text(
                                        text = "Database Connection Setup",
                                        fontWeight = FontWeight.Bold,
                                        color = CorporateNavy,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Text(
                                        text = "Connect local gate operations with industrial backend servers, firebase proxies, or MS SQL catalog databases.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                                    )

                                    // 1. Selector for Connection Mode
                                    Text(
                                        "Select Connection Mode",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = TextNavy
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val modesRow1 = listOf(
                                                "OFFLINE" to "Local Room",
                                                "WEB_API" to "ASP.NET API"
                                            )
                                            modesRow1.forEach { (modeKey, modeTitle) ->
                                                val modeSelected = tempServerType == modeKey
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (modeSelected) SteelBlue else SoftSlate.copy(alpha = 0.12f))
                                                        .clickable { tempServerType = modeKey }
                                                        .padding(vertical = 10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        modeTitle,
                                                        color = if (modeSelected) Color.White else TextNavy,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val modesRow2 = listOf(
                                                "MS_SQL" to "Direct MSSQL Host",
                                                "CONN_STRING" to "Direct SSMS Conn String"
                                            )
                                            modesRow2.forEach { (modeKey, modeTitle) ->
                                                val modeSelected = tempServerType == modeKey
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (modeSelected) SteelBlue else SoftSlate.copy(alpha = 0.12f))
                                                        .clickable { tempServerType = modeKey }
                                                        .padding(vertical = 10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        modeTitle,
                                                        color = if (modeSelected) Color.White else TextNavy,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 2. Form Settings Fields
                                    if (tempServerType == "OFFLINE") {
                                        Text(
                                            "Currently relying heavily on Local SQlite cache mappings via Room. Fully offline support enabled. No network overhead.",
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                        )
                                    } else if (tempServerType == "CONN_STRING") {
                                        Text(
                                            text = "Input manual SSMS Database Connection String:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = TextNavy,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                        OutlinedTextField(
                                            value = tempConnectionString,
                                            onValueChange = { tempConnectionString = it },
                                            placeholder = { Text("Server=tcp:192.168.1.100,1433;Database=GatePassManagementDB;User ID=sa;Password=secret;Encrypt=False;", fontSize = 11.sp) },
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                            modifier = Modifier.fillMaxWidth().testTag("floating_custom_conn_string_input"),
                                            visualTransformation = if (connStringVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                            trailingIcon = {
                                                IconButton(onClick = { connStringVisible = !connStringVisible }) {
                                                    Icon(
                                                        imageVector = if (connStringVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                        contentDescription = "Toggle connection string visibility",
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            },
                                            minLines = 3,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "This string connects local checks directly to SQL Server Management Studio (SSMS) target catalogs via ADO.NET pipelines.",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    } else {
                                        val labelText = if (tempServerType == "WEB_API") "REST Web API Base URL" else "Server Host IP / Hostname"
                                        val placeholderText = if (tempServerType == "WEB_API") "http://192.168.1.100/gatepass/api/" else "192.168.1.100"
                                        
                                        OutlinedTextField(
                                            value = tempServerUrl,
                                            onValueChange = { tempServerUrl = it },
                                            label = { Text(labelText, fontSize = 11.sp) },
                                            placeholder = { Text(placeholderText, fontSize = 11.sp) },
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                            modifier = Modifier.fillMaxWidth().testTag("floating_server_url_input"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (tempServerType == "MS_SQL") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = tempDbPort,
                                                    onValueChange = { tempDbPort = it },
                                                    label = { Text("Port", fontSize = 11.sp) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                OutlinedTextField(
                                                    value = tempDbName,
                                                    onValueChange = { tempDbName = it },
                                                    label = { Text("Database Name", fontSize = 11.sp) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                                    modifier = Modifier.weight(2f),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = tempDbUsername,
                                                    onValueChange = { tempDbUsername = it },
                                                    label = { Text("Username", fontSize = 11.sp) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                OutlinedTextField(
                                                    value = tempDbPassword,
                                                    onValueChange = { tempDbPassword = it },
                                                    label = { Text("Password", fontSize = 11.sp) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                                    trailingIcon = {
                                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                            Icon(
                                                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                                contentDescription = "Toggle password visibility",
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    if (tempServerType == "CONN_STRING" || tempServerType == "MS_SQL") {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = CorporateNavy.copy(alpha = 0.03f)),
                                            border = BorderStroke(1.dp, CorporateNavy.copy(alpha = 0.08f)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("floating_provisioning_card")
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    "Database Provisioning Automation",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = CorporateNavy
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { tempAutoCreateDb = !tempAutoCreateDb }
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Checkbox(
                                                        checked = tempAutoCreateDb,
                                                        onCheckedChange = { tempAutoCreateDb = it },
                                                        colors = CheckboxDefaults.colors(checkedColor = SteelBlue),
                                                        modifier = Modifier.size(32.dp).testTag("floating_auto_create_db_cb")
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text("Auto-Create SQL Database", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextNavy)
                                                        Text("Generates catalog database if unavailable", fontSize = 9.sp, color = TextMuted)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { tempAutoCreateTables = !tempAutoCreateTables }
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Checkbox(
                                                        checked = tempAutoCreateTables,
                                                        onCheckedChange = { tempAutoCreateTables = it },
                                                        colors = CheckboxDefaults.colors(checkedColor = SteelBlue),
                                                        modifier = Modifier.size(32.dp).testTag("floating_auto_create_tables_cb")
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text("Auto-Create Schema Tables", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextNavy)
                                                        Text("Generates missing table entities & relations", fontSize = 9.sp, color = TextMuted)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 3. Test Connection results
                                    if (viewModel.connectionTestStatus != "NONE") {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = when (viewModel.connectionTestStatus) {
                                                    "SUCCESS" -> MintGreen.copy(alpha = 0.08f)
                                                    "ERROR" -> ErrorRed.copy(alpha = 0.08f)
                                                    else -> CorporateNavy.copy(alpha = 0.05f)
                                                }
                                            ),
                                            border = BorderStroke(
                                                1.dp,
                                                when (viewModel.connectionTestStatus) {
                                                    "SUCCESS" -> MintGreen.copy(alpha = 0.25f)
                                                    "ERROR" -> ErrorRed.copy(alpha = 0.25f)
                                                    else -> CorporateNavy.copy(alpha = 0.15f)
                                                }
                                            ),
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (viewModel.connectionTestStatus == "LOADING") {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        strokeWidth = 2.dp,
                                                        color = CorporateNavy
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = if (viewModel.connectionTestStatus == "SUCCESS") Icons.Default.CheckCircle else Icons.Default.Error,
                                                        contentDescription = null,
                                                        tint = if (viewModel.connectionTestStatus == "SUCCESS") MintGreen else ErrorRed,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = if (viewModel.connectionTestStatus == "LOADING") "Testing Handshake..." 
                                                               else if (viewModel.connectionTestStatus == "SUCCESS") "Handshake Succeeded!" 
                                                               else "Handshake Failed",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (viewModel.connectionTestStatus == "SUCCESS") MintGreen else if (viewModel.connectionTestStatus == "ERROR") ErrorRed else TextNavy
                                                    )
                                                    Text(
                                                        text = viewModel.connectionTestMsg,
                                                        fontSize = 11.sp,
                                                        color = TextNavy,
                                                        modifier = Modifier.padding(top = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 4. Action triggers
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.testConnection(
                                                    serverType = tempServerType,
                                                    serverUrl = tempServerUrl,
                                                    databaseName = tempDbName,
                                                    port = tempDbPort,
                                                    username = tempDbUsername,
                                                    pass = tempDbPassword,
                                                    connString = tempConnectionString
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            enabled = viewModel.connectionTestStatus != "LOADING",
                                            modifier = Modifier.weight(1f).height(44.dp).testTag("floating_test_conn_button"),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CorporateNavy),
                                            border = BorderStroke(1.dp, CorporateNavy)
                                        ) {
                                            Icon(
                                                Icons.Default.Power,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Test Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.testConnection(
                                                    serverType = tempServerType,
                                                    serverUrl = tempServerUrl,
                                                    databaseName = tempDbName,
                                                    port = tempDbPort,
                                                    username = tempDbUsername,
                                                    pass = tempDbPassword,
                                                    connString = tempConnectionString,
                                                    onComplete = { success ->
                                                        if (success) {
                                                            if (tempServerType == "CONN_STRING") {
                                                                viewModel.updateConnectionString(
                                                                    connString = tempConnectionString,
                                                                    autoCreateDb = tempAutoCreateDb,
                                                                    autoCreateTables = tempAutoCreateTables
                                                                )
                                                            } else {
                                                                viewModel.updateConnectionSettings(
                                                                    serverType = tempServerType,
                                                                    serverUrl = tempServerUrl,
                                                                    databaseName = tempDbName,
                                                                    port = tempDbPort,
                                                                    user = tempDbUsername,
                                                                    pass = tempDbPassword,
                                                                    autoCreateDb = tempAutoCreateDb,
                                                                    autoCreateTables = tempAutoCreateTables
                                                                )
                                                            }
                                                            Toast.makeText(context, "Handshake Verified! Configuration Saved Successfully.", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(context, "Handshake Failed! Settings NOT saved. Resolve issues first.", Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            enabled = viewModel.connectionTestStatus != "LOADING",
                                            modifier = Modifier.weight(1.3f).height(44.dp).testTag("floating_save_config_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy)
                                        ) {
                                            Icon(
                                                Icons.Default.Save,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Validate & Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: MEETINGS SCHEDULE SCHEDULER
// -------------------------------------------------------------
@Composable
fun MeetingsTab(viewModel: GatePassViewModel) {
    val meetingsList by viewModel.meetings.collectAsStateWithLifecycle()
    val departmentsList by viewModel.departments.collectAsStateWithLifecycle()
    val user = viewModel.currentUser

    var showAddDialog by remember { mutableStateOf(false) }

    // Calendars state selection
    var selectedCalendarView by remember { mutableStateOf("daily") } // daily, monthly
    
    // Create Fields
    var subject by remember { mutableStateOf("") }
    var agenda by remember { mutableStateOf("") }
    var meetingRoom by remember { mutableStateOf("Conference Room Alpha") }
    var deptSelected by remember { mutableStateOf("Information Technology") }
    var participants by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("10:00") }
    var endTime by remember { mutableStateOf("11:30") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Agenda Scheduling Hub",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextNavy
                )
                Text(
                    "Verified shared spaces calendar",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("schedule_meeting_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendar switch bar
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val options = listOf("daily" to "Today's Meetings", "monthly" to "Monthly Calendar view")
            options.forEach { (key, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCalendarView = key }
                        .drawBehind {
                            if (selectedCalendarView == key) {
                                drawLine(
                                    color = CorporateNavy,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedCalendarView == key) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedCalendarView == key) CorporateNavy else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedCalendarView == "monthly") {
            // Drawn Beautiful Monthly interactive widget grid
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "June 2026 Planner",
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        days.forEach { day ->
                            Text(
                                day,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Days grid
                    val weeks = 5
                    for (w in 0 until weeks) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (d in 1..7) {
                                val dayNum = w * 7 + d - 3
                                val isToday = dayNum == 9 // Simulated date match User Meta (June 9, 2026)
                                val hasMeeting = dayNum in listOf(9, 12, 20)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isToday) CorporateNavy else if (hasMeeting) SteelBlue.copy(
                                                alpha = 0.15f
                                            ) else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayNum > 0 && dayNum <= 30) {
                                        Text(
                                            dayNum.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isToday) Color.White else if (hasMeeting) CorporateNavy else TextNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Meetings List
        if (meetingsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No meetings registered. Log in as receptionist or engineer to schedule.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(meetingsList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (item.status == "CANCELLED") ErrorRed.copy(alpha = 0.2f) else SteelBlue.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SteelBlue.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.startTime + " - " + item.endTime,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CorporateNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (item.status) {
                                            "CANCELLED" -> ErrorRed.copy(alpha = 0.1f)
                                            else -> MintGreen.copy(alpha = 0.1f)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = item.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (item.status) {
                                            "CANCELLED" -> ErrorRed
                                            else -> MintGreen
                                        },
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = item.subject,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextNavy
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.agenda,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Home,
                                        contentDescription = "Room",
                                        tint = SteelBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(item.meetingRoom, fontSize = 11.sp, color = TextNavy)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Person,
                                        contentDescription = "Contact",
                                        tint = SteelBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(item.organizerName, fontSize = 11.sp, color = TextNavy)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Participants: " + item.participants,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )

                            // Action to cancel meeting
                            if (item.status == "SCHEDULED" && (user?.role == "Admin" || user?.employeeId == item.organizerId)) {
                                Button(
                                    onClick = { viewModel.cancelMeeting(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .height(28.dp)
                                ) {
                                    Text("Cancel Meeting", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG FOR NEW MEETING CREATION
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Schedule Business Meeting",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = CorporateNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Target") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = agenda,
                        onValueChange = { agenda = it },
                        label = { Text("Agenda description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = meetingRoom,
                        onValueChange = { meetingRoom = it },
                        label = { Text("Meeting Room") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = participants,
                        onValueChange = { participants = it },
                        label = { Text("Participants (Comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard")
                        }
                        Button(
                            onClick = {
                                if (subject.isNotBlank()) {
                                    viewModel.scheduleMeeting(
                                        subject, agenda,
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                        startTime, endTime, deptSelected, meetingRoom, participants
                                    ) {
                                        showAddDialog = false
                                        subject = ""
                                        agenda = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Schedule")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: VISITOR VISITOR ACCESS CONTROL
// -------------------------------------------------------------
@Composable
fun VisitorsTab(viewModel: GatePassViewModel) {
    val visitorsList by viewModel.visitors.collectAsStateWithLifecycle()
    val departmentsList by viewModel.departments.collectAsStateWithLifecycle()
    val user = viewModel.currentUser

    var showRequestDialog by remember { mutableStateOf(false) }
    var selectedVisitorForBadge by remember { mutableStateOf<Visitor?>(null) } // badge preview sheet

    // Create Fields
    var vName by remember { mutableStateOf("") }
    var vMobile by remember { mutableStateOf("") }
    var vCompany by remember { mutableStateOf("") }
    var vPurpose by remember { mutableStateOf("") }
    var vDept by remember { mutableStateOf("Information Technology") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Visitor Registration Indices",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextNavy
                )
                Text(
                    "Authorize & badge secure entry codes",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Button(
                onClick = { showRequestDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("create_visitor_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add visitor", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Register Visitor", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (visitorsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No active visitor reservations at gate index", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visitorsList) { visitor ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Procedural Camera Placeholder or Photo Avatar
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(SteelBlue.copy(alpha = 0.1f))
                                        .border(2.dp, SteelBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBox,
                                        contentDescription = "Visitor avatar",
                                        tint = CorporateNavy,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        visitor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextNavy
                                    )
                                    Text(
                                        "${visitor.company} | Purpose: ${visitor.purpose}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Badge ID: ${visitor.passNumber}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = SteelBlue
                                    )
                                    Text(
                                        "Target Host: ${visitor.department} (${visitor.meetingTime})",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    if(visitor.checkInTime != null) {
                                        Text(
                                            "Entry Log: " + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(visitor.checkInTime)),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MintGreen
                                        )
                                    }
                                }
                            }

                            // QR Preview Trigger Click
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            when (visitor.status) {
                                                "PENDING" -> AmberAlert.copy(alpha = 0.12f)
                                                "CHECKED_IN" -> MintGreen.copy(alpha = 0.12f)
                                                "CHECKED_OUT" -> SoftSlate.copy(alpha = 0.12f)
                                                else -> ErrorRed.copy(alpha = 0.12f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = visitor.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (visitor.status) {
                                            "PENDING" -> AmberAlert
                                            "CHECKED_IN" -> MintGreen
                                            "CHECKED_OUT" -> TextMuted
                                            else -> ErrorRed
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Show Barcode / Badging Click Vector
                                OutlinedButton(
                                    onClick = { selectedVisitorForBadge = visitor },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, CorporateNavy.copy(alpha = 0.2f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Badge QR",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pass Badge", fontSize = 11.sp)
                                }

                                if ((user?.role == "Department Head" || user?.role == "Admin") && (visitor.status == "PENDING" || visitor.status == "CHECKED_IN")) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.rejectVisitor(visitor) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                            border = BorderStroke(1.dp, ErrorRed),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Deny", fontSize = 10.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.approveVisitor(visitor) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Approve", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // RESERVATION CREATE VISITOR REQUEST DIALOG
    if (showRequestDialog) {
        Dialog(onDismissRequest = { showRequestDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Register New Guest Gate Pass",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = CorporateNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = vName,
                        onValueChange = { vName = it },
                        label = { Text("Visitor Legal Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = vMobile,
                        onValueChange = { vMobile = it },
                        label = { Text("Contact Mobile Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = vCompany,
                        onValueChange = { vCompany = it },
                        label = { Text("Representing Corporate entity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = vPurpose,
                        onValueChange = { vPurpose = it },
                        label = { Text("Purpose of Visit (e.g., Audit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Selection department
                    Text("Department Host Location", fontWeight = FontWeight.Bold, color = TextNavy, fontSize = 12.sp)
                    LazyRow(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(departmentsList) { deptObj ->
                            val selected = vDept == deptObj.name
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (selected) CorporateNavy else SoftSlate.copy(alpha = 0.08f))
                                    .clickable { vDept = deptObj.name }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    deptObj.name,
                                    color = if (selected) Color.White else TextNavy,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated visitor photo upload indicator
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberAlert.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Upload", tint = CorporateNavy)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Security Camera Capture Enabled",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateNavy
                                )
                                Text(
                                    "Physical gate cameras will capture guest face upon Arrival check-in.",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRequestDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard")
                        }
                        Button(
                            onClick = {
                                if (vName.isNotBlank() && vMobile.isNotBlank()) {
                                    viewModel.createVisitorRequest(
                                        vName, vMobile, vCompany, vPurpose, vDept
                                    ) {
                                        showRequestDialog = false
                                        vName = ""
                                        vMobile = ""
                                        vCompany = ""
                                        vPurpose = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Queue Pass")
                        }
                    }
                }
            }
        }
    }

    // INTERACTIVE PASS BADGE VISUALIZER DIALOG
    if (selectedVisitorForBadge != null) {
        val vis = selectedVisitorForBadge!!
        Dialog(onDismissRequest = { selectedVisitorForBadge = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CorporateNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("qr_preview_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(AmberAlert)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "GATE PASS VISITOR BADGE",
                            fontWeight = FontWeight.ExtraBold,
                            color = CorporateNavy,
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 1.sp
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        vis.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextNavy
                    )
                    Text(
                        vis.company,
                        style = MaterialTheme.typography.titleSmall,
                        color = SteelBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Procedural Compose Drawing for QR access badge
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(CoolWhite)
                            .border(1.dp, TextMuted, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            // Draw realistic QR corner finder patterns
                            fun drawFinder(x: Float, y: Float) {
                                drawRoundRect(
                                    color = Color.Black,
                                    topLeft = Offset(x, y),
                                    size = Size(32f, 32f),
                                    cornerRadius = CornerRadius(4f)
                                )
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(x + 4f, y + 4f),
                                    size = Size(24f, 24f),
                                    cornerRadius = CornerRadius(2f)
                                )
                                drawRoundRect(
                                    color = Color.Black,
                                    topLeft = Offset(x + 8f, y + 8f),
                                    size = Size(16f, 16f),
                                    cornerRadius = CornerRadius(1f)
                                )
                            }
                            drawFinder(0f, 0f)
                            drawFinder(size.width - 32f, 0f)
                            drawFinder(0f, size.height - 32f)

                            // Procedural random bits matrix mock
                            val random = Random(vis.passNumber.hashCode().toLong())
                            val cellSize = 8f
                            for (row in 4..12) {
                                for (col in 4..12) {
                                    // Skip region overlap discoverers
                                    if (row < 5 && col < 5) continue
                                    if (row < 5 && col > 11) continue
                                    if (row > 11 && col < 5) continue

                                    if (random.nextBoolean()) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(col * cellSize + 20f, row * cellSize + 20f),
                                            size = Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Code: " + vis.qrCode,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = CorporateNavy,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CoolWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Visit Date:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(vis.meetingDate, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextNavy)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Host Unit:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(vis.department, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextNavy)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        OutlinedButton(
                            onClick = { selectedVisitorForBadge = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Print Pass PDF")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { selectedVisitorForBadge = null },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: MATERIAL OUTWARD GATE PASS COORDINATOR
// -------------------------------------------------------------
@Composable
fun GatePassesTab(viewModel: GatePassViewModel) {
    val gatePassList by viewModel.gatePasses.collectAsStateWithLifecycle()
    val user = viewModel.currentUser

    var showCreateGatePassDialog by remember { mutableStateOf(false) }

    // Create Fields
    var customerName by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var materialDetails by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Material Outward Gate Passes",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextNavy
                )
                Text(
                    "Verify raw materials and plant logs workflows",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Button(
                onClick = { showCreateGatePassDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("create_gatepass_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add pass", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Pass", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gatePassList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No material passes in verification stages", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(gatePassList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (item.currentStage == "APPROVED") MintGreen.copy(alpha = 0.3f) else if (item.currentStage == "REJECTED") ErrorRed.copy(
                                alpha = 0.3f
                            ) else SteelBlue.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Pass Code & Workflow Progress State Banner
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "PASS: " + item.gatePassNo,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = CorporateNavy
                                    )
                                    Text(text = "Date: ${item.date}", fontSize = 11.sp, color = TextMuted)
                                }

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (item.currentStage) {
                                            "APPROVED" -> MintGreen.copy(alpha = 0.12f)
                                            "REJECTED" -> ErrorRed.copy(alpha = 0.12f)
                                            else -> AmberAlert.copy(alpha = 0.12f)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = item.statusText,
                                        fontWeight = FontWeight.Bold,
                                        color = when (item.currentStage) {
                                            "APPROVED" -> MintGreen
                                            "REJECTED" -> ErrorRed
                                            else -> CorporateNavy
                                        },
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Details block
                            Text(
                                text = "Recipient: ${item.customerName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextNavy
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Material: ${item.materialDetails}",
                                fontSize = 12.sp,
                                color = TextNavy
                            )
                            Text(
                                text = "Transit Quantity: ${item.quantity}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Driver: ${item.driverName} | Vehicle: ${item.vehicleNumber}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    if (item.approvedBy != null) {
                                        Text(
                                            text = "Certified by DH: ${item.approvedBy}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SteelBlue
                                        )
                                    }
                                }

                                // Interactive approvals stage picker based on active logged-in worker's auth
                                val userCanApprove = when (item.currentStage) {
                                    "DEPT_HEAD" -> user?.role == "Department Head" || user?.role == "Admin"
                                    "SECURITY" -> user?.role == "Security Guard" || user?.role == "Department Head" || user?.role == "Admin"
                                    "DISPATCH" -> user?.role == "Admin" || user?.role == "Security Guard" || user?.role == "Department Head"
                                    else -> false
                                } || ((user?.role == "Department Head" || user?.role == "Admin") && item.currentStage != "APPROVED" && item.currentStage != "REJECTED")

                                if (userCanApprove) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.rejectGatePass(item, "Rejected by authority request") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                            border = BorderStroke(1.dp, ErrorRed),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Deny", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.approveGatePass(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Approve", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // NEW OUTWARD PASS POPUP
    if (showCreateGatePassDialog) {
        Dialog(onDismissRequest = { showCreateGatePassDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Material Outward Release",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = CorporateNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name / Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Logistics Vehicle Reg Plate") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = driverName,
                        onValueChange = { driverName = it },
                        label = { Text("Driver ID Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = materialDetails,
                        onValueChange = { materialDetails = it },
                        label = { Text("Materials breakdown list") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Total Cargo Tonnage / Count") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCreateGatePassDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard")
                        }
                        Button(
                            onClick = {
                                if (customerName.isNotBlank() && materialDetails.isNotBlank()) {
                                    viewModel.createGatePassRequest(
                                        customerName, vehicleNumber, driverName, materialDetails, quantity
                                    ) {
                                        showCreateGatePassDialog = false
                                        customerName = ""
                                        vehicleNumber = ""
                                        driverName = ""
                                        materialDetails = ""
                                        quantity = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Issue Pass")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: SECURITY GUARD BARCODE/QR SCANNER SIMULATOR
// -------------------------------------------------------------
@Composable
fun ScannerTab(viewModel: GatePassViewModel) {
    val visitorsList by viewModel.visitors.collectAsStateWithLifecycle()
    val gatePassList by viewModel.gatePasses.collectAsStateWithLifecycle()

    var manualEntryQrInput by remember { mutableStateOf("") }
    var photoCaptureToggle by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            "Secured Terminal Gate Inspection",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = TextNavy
        )
        Text(
            "Verify visitor entry/exit and outward cargo",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Scanner Aim Target Simulator box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val len = 24.dp.toPx()
                val sweep = 4.dp.toPx()

                // Corner crosshairs green targeting frame laser
                fun drawCorner(x: Float, y: Float, xDir: Float, yDir: Float) {
                    drawLine(
                        color = Color.Green,
                        start = Offset(x, y),
                        end = Offset(x + len * xDir, y),
                        strokeWidth = sweep
                    )
                    drawLine(
                        color = Color.Green,
                        start = Offset(x, y),
                        end = Offset(x, y + len * yDir),
                        strokeWidth = sweep
                    )
                }

                drawCorner(0f, 0f, 1f, 1f)
                drawCorner(size.width, 0f, -1f, 1f)
                drawCorner(0f, size.height, 1f, -1f)
                drawCorner(size.width, size.height, -1f, -1f)

                // Laser scan sweep line animation simulator
                val ratio = (System.currentTimeMillis() % 2000) / 2000f
                drawLine(
                    color = Color.Green.copy(alpha = 0.8f),
                    start = Offset(0f, size.height * ratio),
                    end = Offset(size.width, size.height * ratio),
                    strokeWidth = 2.dp.toPx()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan target",
                    tint = Color.Green.copy(alpha = 0.6f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "BARCODE/QR AIM DEPLOYED",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Verification Scan Input Form
        Card(
            colors = CardDefaults.cardColors(containerColor = CleanWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Verify Code Index Manual Entry",
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = manualEntryQrInput,
                    onValueChange = { manualEntryQrInput = it },
                    placeholder = { Text("e.g., QR-VP-77215") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = photoCaptureToggle,
                            onCheckedChange = { photoCaptureToggle = it }
                        )
                        Text(
                            "Auto-Capture Gate Camera Snap",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextNavy
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.processQrCodeScan(manualEntryQrInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                        modifier = Modifier.testTag("scan_button")
                    ) {
                        Text("SIMULATE SCAN")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SCAN RESULTS CARD BLOCK
        if (viewModel.scanResultStatus != "NONE") {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (viewModel.scanResultStatus) {
                        "VALID" -> MintGreen.copy(alpha = 0.12f)
                        "EXPIRED" -> AmberAlert.copy(alpha = 0.12f)
                        else -> ErrorRed.copy(alpha = 0.12f)
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    when (viewModel.scanResultStatus) {
                        "VALID" -> MintGreen
                        "EXPIRED" -> AmberAlert
                        else -> ErrorRed
                    }
                ),
                modifier = Modifier.fillMaxWidth().animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (viewModel.scanResultStatus) {
                                "VALID" -> Icons.Default.CheckCircle
                                "EXPIRED" -> Icons.Default.Warning
                                else -> Icons.Default.Cancel
                            },
                            contentDescription = "Status badge",
                            tint = when (viewModel.scanResultStatus) {
                                "VALID" -> MintGreen
                                "EXPIRED" -> AmberAlert
                                else -> ErrorRed
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.scanResultTitle,
                            fontWeight = FontWeight.Bold,
                            color = when (viewModel.scanResultStatus) {
                                "VALID" -> MintGreen
                                "EXPIRED" -> AmberAlert
                                else -> ErrorRed
                            },
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.scanResultMessage,
                        fontSize = 13.sp,
                        color = TextNavy,
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tap Quick-Scan Simulator List for Visitors & Materials
        Text(
            text = "Active Gate Badges Index for Quick Scanner Inspection",
            fontWeight = FontWeight.Bold,
            color = TextNavy,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pendingVisitors = visitorsList.filter { it.status in listOf("PENDING", "CHECKED_IN") }
            val securityGatePasses = gatePassList.filter { it.currentStage in listOf("SECURITY", "DISPATCH") }

            if (pendingVisitors.isEmpty() && securityGatePasses.isEmpty()) {
                Text(
                    "No pending visitors or cargo passes to select for quick scan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                pendingVisitors.forEach { v ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                manualEntryQrInput = v.qrCode
                                viewModel.processQrCodeScan(v.qrCode)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SteelBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Visitor: " + v.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextNavy
                                    )
                                    Text(v.qrCode + " (" + v.status + ")", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MintGreen)
                        }
                    }
                }

                securityGatePasses.forEach { gp ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                manualEntryQrInput = gp.qrCode
                                viewModel.processQrCodeScan(gp.qrCode)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AmberAlert)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Cargo Pass: " + gp.gatePassNo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextNavy
                                    )
                                    Text(gp.qrCode + " (" + gp.currentStage + ")", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MintGreen)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// TAB 5: ADMIN COMPILATION & REPORTS DASHBOARD
// -------------------------------------------------------------
@Composable
fun ReportsTab(viewModel: GatePassViewModel) {
    val logs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val visitorsList by viewModel.visitors.collectAsStateWithLifecycle()
    val gatePassList by viewModel.gatePasses.collectAsStateWithLifecycle()
    val meetingsList by viewModel.meetings.collectAsStateWithLifecycle()
    val user = viewModel.currentUser

    val context = LocalContext.current
    var activeReportSection by remember { mutableStateOf("dashboard") } // dashboard, audits, export, db_config

    // Local mutable state matching viewmodel properties
    var tempServerType by remember(viewModel.dbServerType) { mutableStateOf(viewModel.dbServerType) }
    var tempServerUrl by remember(viewModel.dbServerUrl) { mutableStateOf(viewModel.dbServerUrl) }
    var tempDbName by remember(viewModel.dbName) { mutableStateOf(viewModel.dbName) }
    var tempDbPort by remember(viewModel.dbPort) { mutableStateOf(viewModel.dbPort) }
    var tempDbUsername by remember(viewModel.dbUsername) { mutableStateOf(viewModel.dbUsername) }
    var tempDbPassword by remember(viewModel.dbPassword) { mutableStateOf(viewModel.dbPassword) }
    var tempConnectionString by remember(viewModel.dbConnectionString) { mutableStateOf(viewModel.dbConnectionString) }
    var tempAutoCreateDb by remember(viewModel.dbAutoCreateDatabase) { mutableStateOf(viewModel.dbAutoCreateDatabase) }
    var tempAutoCreateTables by remember(viewModel.dbAutoCreateTables) { mutableStateOf(viewModel.dbAutoCreateTables) }
    var appCheckEnabled by remember(viewModel.firebaseAppCheckEnabled) { mutableStateOf(viewModel.firebaseAppCheckEnabled) }
    var authSecurityEnabled by remember(viewModel.firebaseAuthSecurityEnabled) { mutableStateOf(viewModel.firebaseAuthSecurityEnabled) }
    var dbSyncEnabled by remember(viewModel.firebaseDatabaseSyncEnabled) { mutableStateOf(viewModel.firebaseDatabaseSyncEnabled) }
    var passwordVisible by remember { mutableStateOf(false) }
    var connStringVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Administrative Metrics Terminal",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextNavy
                )
                Text(
                    "Factory-wide entry logs & compliance indicators",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation for report options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val opts = if (user?.role == "Admin") {
                listOf(
                    "dashboard" to "Charts",
                    "audits" to "Audits",
                    "export" to "Export",
                    "db_config" to "DB Setup"
                )
            } else {
                listOf(
                    "dashboard" to "Charts",
                    "audits" to "Audits",
                    "export" to "Export"
                )
            }
            opts.forEach { (key, title) ->
                val selected = activeReportSection == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) CorporateNavy else SoftSlate.copy(alpha = 0.08f))
                        .clickable { activeReportSection = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        title,
                        color = if (selected) Color.White else TextNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeReportSection) {
            "dashboard" -> {
                // Administrative aggregate stats grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Visitors widget
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Visitors", fontSize = 11.sp, color = TextMuted)
                            Text(
                                visitorsList.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorporateNavy
                            )
                            Text(
                                "+3 Expected today",
                                fontSize = 9.sp,
                                color = MintGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Total Gate Passes widget
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Cargo Clearances", fontSize = 11.sp, color = TextMuted)
                            Text(
                                gatePassList.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = SteelBlue
                            )
                            Text(
                                "All processes active",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Scheduled Meetings count
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Scheduled Meetings", fontSize = 11.sp, color = TextMuted)
                            Text(
                                meetingsList.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAlert
                            )
                            Text(
                                "3 Rooms active",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CUSTOM CANVAS DRIVEN CHART: VISITOR COUNTS BY DEPARTMENT DEPLOYMENT
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Departmental Visitor Distribution Ratio",
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom canvas plotting bar items
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            val totalWidth = size.width
                            val totalHeight = size.height
                            val labels = listOf("IT Sec", "Mftg", "Log", "HR", "Admin")
                            val values = listOf(45f, 75f, 25f, 35f, 15f) // simulated heights

                            val barCount = labels.size
                            val barSpacing = 30f
                            val availableWidth = totalWidth - (barSpacing * (barCount + 1))
                            val barWidth = availableWidth / barCount

                            // Draw baseline grid indicators
                            drawLine(
                                color = TextMuted.copy(alpha = 0.2f),
                                start = Offset(0f, totalHeight * 0.25f),
                                end = Offset(totalWidth, totalHeight * 0.25f),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = TextMuted.copy(alpha = 0.2f),
                                start = Offset(0f, totalHeight * 0.5f),
                                end = Offset(totalWidth, totalHeight * 0.5f),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = TextMuted.copy(alpha = 0.2f),
                                start = Offset(0f, totalHeight * 0.75f),
                                end = Offset(totalWidth, totalHeight * 0.75f),
                                strokeWidth = 1f
                            )

                            // Render vertical bars
                            for (i in 0 until barCount) {
                                val xOffset = barSpacing + i * (barWidth + barSpacing)
                                val fractionalHeight = (values[i] / 100f) * (totalHeight - 30f)
                                val yOffset = (totalHeight - 30f) - fractionalHeight

                                // Draw bar gradient brush shadow
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = if (i == 1) listOf(AmberAlert, CorporateNavy) else listOf(
                                            SteelBlue,
                                            CorporateNavy
                                        )
                                    ),
                                    topLeft = Offset(xOffset, yOffset),
                                    size = Size(barWidth, fractionalHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }

                        // Labels row under custom plotting canvas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val labelsStr = listOf("IT Unit", "Mftg Dept", "Logistics", "HR Dept", "Admin")
                            labelsStr.forEach { label ->
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GRAPHICAL LIFE LIFE-CYCLE PIPELINE FOR ACCESS CONTROL
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = CorporateNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Visitor Admission Lifecycle Stages",
                                fontWeight = FontWeight.Bold,
                                color = CorporateNavy,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text(
                            "Live state pipeline of pre-registered and checked-in plant visitors",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        // 4 Horizontal State Pipeline Cards
                        val pendingCount = visitorsList.filter { it.status == "PENDING" }.size
                        val activeCount = visitorsList.filter { it.status == "CHECKED_IN" }.size
                        val completedCount = visitorsList.filter { it.status == "CHECKED_OUT" }.size
                        val rejectedCount = visitorsList.filter { it.status == "REJECTED" }.size

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val steps = listOf(
                                Triple("PENDING", pendingCount, AmberAlert.copy(alpha = 0.15f)),
                                Triple("CHECKED IN", activeCount, SteelBlue.copy(alpha = 0.15f)),
                                Triple("COMPLETED", completedCount, MintGreen.copy(alpha = 0.15f)),
                                Triple("REJECTED", rejectedCount, ErrorRed.copy(alpha = 0.12f))
                            )

                            steps.forEach { (title, count, bgCol) ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(bgCol)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CorporateNavy
                                    )
                                    Text(
                                        text = title,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextNavy,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cargo Gate Pass Industrial Clearance Pipeline Block Diagrams
                        Divider(color = SoftSlate.copy(alpha = 0.1f), modifier = Modifier.padding(bottom = 12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = SteelBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Cargo Gate-Pass Logistics Clearance Stage Diagram",
                                fontWeight = FontWeight.Bold,
                                color = CorporateNavy,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text(
                            "Step-by-step gate clearances mapped dynamically through five verification e-checkpoints",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                        )

                        val empStage = gatePassList.filter { it.currentStage == "EMPLOYEE" }.size
                        val deptStage = gatePassList.filter { it.currentStage == "DEPT_HEAD" }.size
                        val secStage = gatePassList.filter { it.currentStage == "SECURITY" }.size
                        val dispatchStage = gatePassList.filter { it.currentStage == "DISPATCH" }.size
                        val appStage = gatePassList.filter { it.currentStage == "APPROVED" }.size

                        val pipeline = listOf(
                            Triple("Employee Draft", empStage, "1"),
                            Triple("Dept Head", deptStage, "2"),
                            Triple("Security Gate", secStage, "3"),
                            Triple("Dispatch Yard", dispatchStage, "4"),
                            Triple("Gate Released", appStage, "✔")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pipeline.forEachIndexed { index, (label, count, indicator) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CoolWhite)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular stage badge
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (count > 0) SteelBlue else CorporateNavy.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = indicator,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextNavy,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Dynamic Stage Count Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (count > 0) SteelBlue.copy(alpha = 0.2f) else SoftSlate.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$count Passes",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (count > 0) SteelBlue else TextMuted
                                        )
                                    }
                                }

                                if (index < pipeline.size - 1) {
                                    // Visual downward connection flow vector arrow
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 18.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = "flow connection",
                                            tint = SteelBlue.copy(alpha = 0.4f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            "audits" -> {
                // Employee Roles and Profiles Verification Panel ("admin role also check" & "employee" stages)
                if (user?.role == "Department Head" || user?.role == "Admin") {
                    Text(
                        text = "Employee Access & Role Security Audit",
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Department Head authority to audit user profiles or check system admin roles.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val employeesList by viewModel.employees.collectAsStateWithLifecycle()
                    employeesList.forEach { emp ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CleanWhite),
                            border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when(emp.role) {
                                                "Admin" -> Icons.Default.AdminPanelSettings
                                                "Department Head" -> Icons.Default.ManageAccounts
                                                "Security Guard" -> Icons.Default.PrivacyTip
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = "Role icon",
                                            tint = if (emp.role == "Admin") AmberAlert else SteelBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = emp.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = TextNavy
                                            )
                                            Text(
                                                text = "ID: ${emp.employeeId} | Dept: ${emp.department}",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    // Role badge marker
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (emp.role == "Admin") AmberAlert.copy(alpha = 0.12f) else SoftSlate.copy(alpha = 0.08f)
                                        )
                                    ) {
                                        Text(
                                            text = emp.role,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (emp.role == "Admin") AmberAlert else TextNavy,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        // Verify/Approve profile
                                        Button(
                                            onClick = {
                                                Toast.makeText(context, "Profile ${emp.name} checked & verified!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "verify_icon",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Verify", fontSize = 9.sp, color = Color.White)
                                        }

                                        // Reject/Block profile
                                        OutlinedButton(
                                            onClick = {
                                                Toast.makeText(context, "Access for ${emp.name} has been rejected/suspended!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                            border = BorderStroke(1.dp, ErrorRed),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("Reject", fontSize = 9.sp)
                                        }
                                    }

                                    // Cycle/Switch active roles on-the-fly to test of stages
                                    Text(
                                        text = "Toggle Role 🔄",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SteelBlue,
                                        modifier = Modifier
                                            .clickable {
                                                val nextRole = when(emp.role) {
                                                    "Employee" -> "Security Guard"
                                                    "Security Guard" -> "Department Head"
                                                    "Department Head" -> "Admin"
                                                    else -> "Employee"
                                                }
                                                viewModel.updateEmployee(emp.copy(role = nextRole))
                                                Toast.makeText(context, "Shifted ${emp.name} to $nextRole role", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = SoftSlate.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Plant Audit log registries
                Text(
                    text = "Local System Transaction Logs",
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (logs.isEmpty()) {
                    Text("No local transactions index found.", style = MaterialTheme.typography.bodySmall)
                } else {
                    logs.forEach { logItem ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CleanWhite),
                            border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = logItem.action,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = SteelBlue
                                    )
                                    Text(
                                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(logItem.timestamp)),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = logItem.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    color = TextNavy,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Operator: ${logItem.username} (${logItem.userId})",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 4.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            "export" -> {
                // Simulate corporate excel/pdf print exports
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Compile Excel/PDF Summary Documents",
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Download encrypted logs to local external folders.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val reportsList = listOf(
                            "Visitor Access Incident report (.PDF)",
                            "Logistics Gate Outward pass logs (.CSV)",
                            "Weekly Room Reservation audits (.XLSX)",
                            "Factory Compliance checklist (.PDF)"
                        )

                        reportsList.forEach { documentName ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CoolWhite),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "download",
                                            tint = CorporateNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            documentName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextNavy
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Export", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "db_config" -> {
                if (user?.role != "Admin") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "authorized",
                                    tint = MintGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Admin Access Bypass Enabled",
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreen,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Granted write, read, collection data & sensor permissions for Android 5-13 standard devices.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextNavy,
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                if (true) {
                    // Enterprise relational engine connection profile
                Text(
                    text = "Enterprise Data Gateway Integration",
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy,
                    fontSize = 14.sp
                )
                Text(
                    text = "Connect local gate operations with industrial backend servers, firebase proxies, or MS SQL catalog databases.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // 2. Selector for Connection Mode
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Select Connection Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val modesRow1 = listOf(
                                    "OFFLINE" to "Local Room",
                                    "WEB_API" to "ASP.NET API"
                                )
                                modesRow1.forEach { (modeKey, modeTitle) ->
                                    val modeSelected = tempServerType == modeKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (modeSelected) SteelBlue else SoftSlate.copy(alpha = 0.12f))
                                            .clickable { tempServerType = modeKey }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            modeTitle,
                                            color = if (modeSelected) Color.White else TextNavy,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val modesRow2 = listOf(
                                    "MS_SQL" to "Direct MSSQL Host",
                                    "CONN_STRING" to "Direct SSMS Conn String"
                                )
                                modesRow2.forEach { (modeKey, modeTitle) ->
                                    val modeSelected = tempServerType == modeKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (modeSelected) SteelBlue else SoftSlate.copy(alpha = 0.12f))
                                            .clickable { tempServerType = modeKey }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            modeTitle,
                                            color = if (modeSelected) Color.White else TextNavy,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2.5 Firebase Protection Suite Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = CorporateNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Firebase Shield Protections",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextNavy
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MintGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("ACTIVE_SHIELD", color = MintGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            "Protect local SQLITE caches and restrict credential authorization rules with integrated security policies.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // App Check Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text("Firebase App Check Shield", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextNavy)
                                Text("Attests device integrity and shields endpoints against clones.", fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = appCheckEnabled,
                                onCheckedChange = { 
                                    appCheckEnabled = it
                                    viewModel.toggleFirebaseProtection(it, authSecurityEnabled, dbSyncEnabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SteelBlue,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.White
                                )
                            )
                        }
                        Divider(color = SoftSlate.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                        // Credential Protection Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text("AuthShield Identity Biometrics", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextNavy)
                                Text("Mandates enterprise credentials and prevents rollbacks.", fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = authSecurityEnabled,
                                onCheckedChange = { 
                                    authSecurityEnabled = it
                                    viewModel.toggleFirebaseProtection(appCheckEnabled, it, dbSyncEnabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SteelBlue,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.White
                                )
                            )
                        }
                        Divider(color = SoftSlate.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                        // Cloud Backup sync Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text("Firestore Vault Synchronization", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextNavy)
                                Text("Safely backs up gate passes inside encrypted firestore schemas.", fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = dbSyncEnabled,
                                onCheckedChange = { 
                                    dbSyncEnabled = it
                                    viewModel.toggleFirebaseProtection(appCheckEnabled, authSecurityEnabled, it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SteelBlue,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.White
                                )
                            )
                        }
                    }
                }

                // 2. Active configuration form variables
                Card(
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Dns,
                                contentDescription = null,
                                tint = SteelBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Connection Settings Form",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextNavy
                             )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (tempServerType == "OFFLINE") {
                            Text(
                                "Currently relying heavily on Local SQlite cache mappings via Room. Fully offline support enabled. No network overhead.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            )
                        } else if (tempServerType == "CONN_STRING") {
                            Text(
                                text = "Input manual SSMS Database Connection String:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextNavy,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = tempConnectionString,
                                onValueChange = { tempConnectionString = it },
                                placeholder = { Text("Server=tcp:192.168.1.100,1433;Database=GatePassManagementDB;User ID=sa;Password=secret;Encrypt=False;", fontSize = 11.sp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                modifier = Modifier.fillMaxWidth().testTag("custom_conn_string_input"),
                                visualTransformation = if (connStringVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { connStringVisible = !connStringVisible }) {
                                        Icon(
                                            imageVector = if (connStringVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle connection string visibility",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                minLines = 3,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = SteelBlue,
                                    unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "This string connects local checks directly to SQL Server Management Studio (SSMS) target catalogs via ADO.NET pipelines.",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        } else {
                            // URL or Server Target
                            val labelText = if (tempServerType == "WEB_API") "REST Web API Base URL" else "Server Host IP / Hostname"
                            val placeholderText = if (tempServerType == "WEB_API") "http://192.168.1.100/gatepass/api/" else "192.168.1.100"
                            
                            OutlinedTextField(
                                value = tempServerUrl,
                                onValueChange = { tempServerUrl = it },
                                label = { Text(labelText, fontSize = 11.sp) },
                                placeholder = { Text(placeholderText, fontSize = 11.sp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                modifier = Modifier.fillMaxWidth().testTag("server_url_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = SteelBlue,
                                    unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (tempServerType == "MS_SQL") {
                                // Port & Database Name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = tempDbPort,
                                        onValueChange = { tempDbPort = it },
                                        label = { Text("Port", fontSize = 11.sp) },
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedBorderColor = SteelBlue,
                                            unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                        )
                                    )
                                    OutlinedTextField(
                                        value = tempDbName,
                                        onValueChange = { tempDbName = it },
                                        label = { Text("Database Name", fontSize = 11.sp) },
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                        modifier = Modifier.weight(2f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                             focusedBorderColor = SteelBlue,
                                            unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Username & Password
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = tempDbUsername,
                                        onValueChange = { tempDbUsername = it },
                                        label = { Text("Username", fontSize = 11.sp) },
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedBorderColor = SteelBlue,
                                            unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                        )
                                    )
                                    OutlinedTextField(
                                        value = tempDbPassword,
                                        onValueChange = { tempDbPassword = it },
                                        label = { Text("Password", fontSize = 11.sp) },
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password visibility",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedBorderColor = SteelBlue,
                                            unfocusedBorderColor = SoftSlate.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                if (tempServerType == "CONN_STRING" || tempServerType == "MS_SQL") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        border = BorderStroke(1.dp, SoftSlate.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("panel_provisioning_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Database Provisioning Automation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CorporateNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempAutoCreateDb = !tempAutoCreateDb }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = tempAutoCreateDb,
                                    onCheckedChange = { tempAutoCreateDb = it },
                                    colors = CheckboxDefaults.colors(checkedColor = SteelBlue),
                                    modifier = Modifier.size(32.dp).testTag("panel_auto_create_db_cb")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Auto-Create SQL Database", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextNavy)
                                    Text("Generates catalog database if unavailable", fontSize = 9.sp, color = TextMuted)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempAutoCreateTables = !tempAutoCreateTables }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = tempAutoCreateTables,
                                    onCheckedChange = { tempAutoCreateTables = it },
                                    colors = CheckboxDefaults.colors(checkedColor = SteelBlue),
                                    modifier = Modifier.size(32.dp).testTag("panel_auto_create_tables_cb")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Auto-Create Schema Tables", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextNavy)
                                    Text("Generates missing table entities & relations", fontSize = 9.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }

                // 3. Test Connection Live Status Card
                if (viewModel.connectionTestStatus != "NONE") {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (viewModel.connectionTestStatus) {
                                "SUCCESS" -> MintGreen.copy(alpha = 0.08f)
                                "ERROR" -> ErrorRed.copy(alpha = 0.08f)
                                else -> CorporateNavy.copy(alpha = 0.05f)
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            when (viewModel.connectionTestStatus) {
                                "SUCCESS" -> MintGreen.copy(alpha = 0.25f)
                                "ERROR" -> ErrorRed.copy(alpha = 0.25f)
                                else -> CorporateNavy.copy(alpha = 0.15f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (viewModel.connectionTestStatus == "LOADING") {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CorporateNavy
                                )
                            } else {
                                Icon(
                                    imageVector = if (viewModel.connectionTestStatus == "SUCCESS") Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (viewModel.connectionTestStatus == "SUCCESS") MintGreen else ErrorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (viewModel.connectionTestStatus == "LOADING") "Testing Handshake..." 
                                           else if (viewModel.connectionTestStatus == "SUCCESS") "Handshake Succeeded!" 
                                           else "Handshake Failed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (viewModel.connectionTestStatus == "SUCCESS") MintGreen else if (viewModel.connectionTestStatus == "ERROR") ErrorRed else TextNavy
                                )
                                Text(
                                    text = viewModel.connectionTestMsg,
                                    fontSize = 11.sp,
                                    color = TextNavy,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Action triggers: Test connection and Save configuration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Test connection button
                    OutlinedButton(
                        onClick = {
                            viewModel.testConnection(
                                serverType = tempServerType,
                                serverUrl = tempServerUrl,
                                databaseName = tempDbName,
                                port = tempDbPort,
                                username = tempDbUsername,
                                pass = tempDbPassword,
                                connString = tempConnectionString
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        enabled = viewModel.connectionTestStatus != "LOADING",
                        modifier = Modifier.weight(1f).height(44.dp).testTag("test_conn_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CorporateNavy
                        ),
                        border = BorderStroke(1.dp, CorporateNavy)
                    ) {
                        Icon(
                            Icons.Default.Power,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Save settings button (runs validation BEFORE updating run-time config)
                    Button(
                        onClick = {
                            viewModel.testConnection(
                                serverType = tempServerType,
                                serverUrl = tempServerUrl,
                                databaseName = tempDbName,
                                port = tempDbPort,
                                username = tempDbUsername,
                                pass = tempDbPassword,
                                connString = tempConnectionString,
                                onComplete = { success ->
                                    if (success) {
                                        if (tempServerType == "CONN_STRING") {
                                            viewModel.updateConnectionString(
                                                connString = tempConnectionString,
                                                autoCreateDb = tempAutoCreateDb,
                                                autoCreateTables = tempAutoCreateTables
                                            )
                                        } else {
                                            viewModel.updateConnectionSettings(
                                                serverType = tempServerType,
                                                serverUrl = tempServerUrl,
                                                databaseName = tempDbName,
                                                port = tempDbPort,
                                                user = tempDbUsername,
                                                pass = tempDbPassword,
                                                autoCreateDb = tempAutoCreateDb,
                                                autoCreateTables = tempAutoCreateTables
                                            )
                                        }
                                        Toast.makeText(context, "Handshake Verified! Configuration Saved Successfully.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Handshake Failed! Settings NOT saved. Resolve issues first.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        enabled = viewModel.connectionTestStatus != "LOADING",
                        modifier = Modifier.weight(1.5f).height(44.dp).testTag("save_config_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CorporateNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Validate & Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
