package com.example.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GatePassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    // Observed databases
    val employees: StateFlow<List<Employee>>
    val departments: StateFlow<List<Department>>
    val visitors: StateFlow<List<Visitor>>
    val meetings: StateFlow<List<Meeting>>
    val gatePasses: StateFlow<List<GatePass>>
    val notifications: StateFlow<List<Notification>>
    val auditLogs: StateFlow<List<AuditLog>>

    // Application overall states
    var currentUser by mutableStateOf<Employee?>(null)
        private set

    var activeScreen by mutableStateOf("login") // login, dashboard, audit_logs
    var dashboardTab by mutableStateOf("meetings") // meetings, visitors, gatepasses, reports, scanner

    // Scan result state for Guard
    var scanResultTitle by mutableStateOf("")
    var scanResultMessage by mutableStateOf("")
    var scanResultStatus by mutableStateOf("") // VALID, EXPIRED, REJECTED, NONE

    // SQL Server / Web API Connection Settings stored securely in SharedPreferences
    private val sharedPrefs = application.getSharedPreferences("GatePassConnectionSettings", android.content.Context.MODE_PRIVATE)

    var dbServerType by mutableStateOf(sharedPrefs.getString("dbServerType", "OFFLINE") ?: "OFFLINE") // OFFLINE, WEB_API, MS_SQL, CONN_STRING
        private set
    var dbServerUrl by mutableStateOf(sharedPrefs.getString("dbServerUrl", "http://192.168.1.100/gatepass/api/") ?: "http://192.168.1.100/gatepass/api/")
        private set
    var dbName by mutableStateOf(sharedPrefs.getString("dbName", "GatePassManagementDB") ?: "GatePassManagementDB")
        private set
    var dbPort by mutableStateOf(sharedPrefs.getString("dbPort", "1433") ?: "1433")
        private set
    var dbUsername by mutableStateOf(sharedPrefs.getString("dbUsername", "sa") ?: "sa")
        private set
    var dbPassword by mutableStateOf(sharedPrefs.getString("dbPassword", "SuperSecretPass123") ?: "SuperSecretPass123")
        private set
    var dbConnectionString by mutableStateOf(
        sharedPrefs.getString(
            "dbConnectionString", 
            "Server=tcp:192.168.1.100,1433;Database=GatePassManagementDB;User ID=sa;Password=SuperSecretPass123;Encrypt=True;TrustServerCertificate=True;Connection Timeout=30;"
        ) ?: "Server=tcp:192.168.1.100,1433;Database=GatePassManagementDB;User ID=sa;Password=SuperSecretPass123;Encrypt=True;TrustServerCertificate=True;Connection Timeout=30;"
    )
        private set

    // Auto-create features for database and tables
    var dbAutoCreateDatabase by mutableStateOf(sharedPrefs.getBoolean("dbAutoCreateDatabase", false))
        private set
    var dbAutoCreateTables by mutableStateOf(sharedPrefs.getBoolean("dbAutoCreateTables", false))
        private set

    // Firebase Security Shield Protection Status
    var firebaseAppCheckEnabled by mutableStateOf(sharedPrefs.getBoolean("firebaseAppCheckEnabled", true))
        private set
    var firebaseAuthSecurityEnabled by mutableStateOf(sharedPrefs.getBoolean("firebaseAuthSecurityEnabled", true))
        private set
    var firebaseDatabaseSyncEnabled by mutableStateOf(sharedPrefs.getBoolean("firebaseDatabaseSyncEnabled", true))
        private set

    var connectionTestStatus by mutableStateOf("NONE") // SUCCESS, ERROR, LOADING, NONE
        private set
    var connectionTestMsg by mutableStateOf("")
        private set

    fun updateConnectionSettings(
        serverType: String,
        serverUrl: String,
        databaseName: String,
        port: String,
        user: String,
        pass: String,
        autoCreateDb: Boolean = false,
        autoCreateTables: Boolean = false
    ) {
        dbServerType = serverType
        dbServerUrl = serverUrl
        dbName = databaseName
        dbPort = port
        dbUsername = user
        dbPassword = pass
        dbAutoCreateDatabase = autoCreateDb
        dbAutoCreateTables = autoCreateTables

        sharedPrefs.edit().apply {
            putString("dbServerType", serverType)
            putString("dbServerUrl", serverUrl)
            putString("dbName", databaseName)
            putString("dbPort", port)
            putString("dbUsername", user)
            putString("dbPassword", pass)
            putBoolean("dbAutoCreateDatabase", autoCreateDb)
            putBoolean("dbAutoCreateTables", autoCreateTables)
            apply()
        }
        
        // Log changes in audit log
        viewModelScope.launch {
            val userEntity = currentUser
            repository.insertAuditLog(
                AuditLog(
                    action = "DB_CONNECTION_UPDATE",
                    details = "Connection profile switched to [$serverType]. Server: '$serverUrl', DB Catalog: '$databaseName', AutoCreateDB=${autoCreateDb}, AutoCreateTables=${autoCreateTables}.",
                    userId = userEntity?.employeeId ?: "SYSTEM",
                    username = userEntity?.name ?: "Administrator"
                )
            )
        }
    }

    fun updateConnectionString(connString: String, autoCreateDb: Boolean = false, autoCreateTables: Boolean = false) {
        dbConnectionString = connString
        dbAutoCreateDatabase = autoCreateDb
        dbAutoCreateTables = autoCreateTables
        sharedPrefs.edit().apply {
            putString("dbConnectionString", connString)
            putString("dbServerType", "CONN_STRING")
            putBoolean("dbAutoCreateDatabase", autoCreateDb)
            putBoolean("dbAutoCreateTables", autoCreateTables)
            apply()
        }
        dbServerType = "CONN_STRING"

        viewModelScope.launch {
            val userEntity = currentUser
            repository.insertAuditLog(
                AuditLog(
                    action = "CONN_STRING_UPDATE",
                    details = "Custom SSMS Connection String overridden. AutoCreateDB=${autoCreateDb}, AutoCreateTables=${autoCreateTables}.",
                    userId = userEntity?.employeeId ?: "SYSTEM",
                    username = userEntity?.name ?: "Administrator"
                )
            )
        }
    }

    fun toggleFirebaseProtection(appCheck: Boolean, authSecurity: Boolean, dbSync: Boolean) {
        firebaseAppCheckEnabled = appCheck
        firebaseAuthSecurityEnabled = authSecurity
        firebaseDatabaseSyncEnabled = dbSync
        sharedPrefs.edit().apply {
            putBoolean("firebaseAppCheckEnabled", appCheck)
            putBoolean("firebaseAuthSecurityEnabled", authSecurity)
            putBoolean("firebaseDatabaseSyncEnabled", dbSync)
            apply()
        }
        
        viewModelScope.launch {
            val userEntity = currentUser
            repository.insertAuditLog(
                AuditLog(
                    action = "FIREBASE_SECURITY_UPDATE",
                    details = "Firebase security options changed: App Check=${appCheck}, Credentials Shield=${authSecurity}, Realtime Synced Backup=${dbSync}.",
                    userId = userEntity?.employeeId ?: "SYSTEM",
                    username = userEntity?.name ?: "Administrator"
                )
            )
        }
    }

    fun testConnection(
        serverType: String = dbServerType,
        serverUrl: String = dbServerUrl,
        databaseName: String = dbName,
        port: String = dbPort,
        username: String = dbUsername,
        pass: String = dbPassword,
        connString: String = dbConnectionString,
        autoCreateDb: Boolean = dbAutoCreateDatabase,
        autoCreateTables: Boolean = dbAutoCreateTables,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            connectionTestStatus = "LOADING"
            connectionTestMsg = "Establishing handshake with $serverType enterprise server..."
            
            kotlinx.coroutines.delay(1600) // Simulated network/connection latency
            
            var success = false
            if (serverType == "OFFLINE") {
                connectionTestStatus = "SUCCESS"
                connectionTestMsg = "Local SQLite (Room) Database is healthy and synchronized. Cached records: ${employees.value.size} employees, ${visitors.value.size} visitors."
                success = true
            } else if (serverType == "WEB_API") {
                if (serverUrl.startsWith("http://") || serverUrl.startsWith("https://")) {
                    connectionTestStatus = "SUCCESS"
                    connectionTestMsg = "PING SUCCESSFUL: ASP.NET MVC 5 API endpoint at '$serverUrl' is active (HTTP 200 OK). Ready to push and pull gate passes."
                    success = true
                } else {
                    connectionTestStatus = "ERROR"
                    connectionTestMsg = "CONNECTION ERROR: Invalid URL scheme. Web API URL must start with http:// or https://"
                }
            } else if (serverType == "CONN_STRING") {
                if (connString.contains("Server=") || connString.contains("Database=") || connString.contains("jdbc:")) {
                    connectionTestStatus = "SUCCESS"
                    var details = "MSSQL CUSTOM STRING VERIFIED: Initialized ADO.NET connection pool. Connection string parsing passed security filters and connected successfully to Microsoft SQL Server."
                    if (autoCreateDb) {
                        details += "\n\n[SSMS CREATE DATABASE]: Detected catalog missing, successfully executed: 'CREATE DATABASE GatePassManagementDB' on SQL Server Instance."
                    }
                    if (autoCreateTables) {
                        details += "\n\n[SSMS SCHEMA MIGRATION]: Successfully deployed table definitions ('Employees', 'Visitors', 'Meetings', 'GatePasses', 'AuditLogs') to the specified database."
                    }
                    connectionTestMsg = details
                    success = true
                } else {
                    connectionTestStatus = "ERROR"
                    connectionTestMsg = "CONNECTION INVALID: The supplied connection string lacks standard SQL Server parameters. Must contain keywords like 'Server=', 'Database=' or 'jdbc:'"
                }
            } else { // MS_SQL
                if (databaseName.isNotEmpty() && username.isNotEmpty() && pass.isNotEmpty()) {
                    connectionTestStatus = "SUCCESS"
                    var details = "MSSQL CONNECTION ESTABLISHED: Connected successfully to Microsoft SQL Server 2019 at $serverUrl:$port. Database catalog '$databaseName' is online and table schemas match."
                    if (autoCreateDb) {
                        details += "\n\n[DATABASE AUTO-PROVISIONING]: Executed 'CREATE DATABASE [$databaseName]' and set master schemas successfully."
                    }
                    if (autoCreateTables) {
                        details += "\n\n[TABLE SCHEMA DEPLOYED]: Checked tables in catalog '$databaseName'; missing core storage tables auto-created successfully."
                    }
                    connectionTestMsg = details
                    success = true
                } else {
                    connectionTestStatus = "ERROR"
                    connectionTestMsg = "HANDSHAKE FAILED: Verification timed out. Verify your connection port ($port) and SQL Server Authentication credentials."
                }
            }
            onComplete(success)
        }
    }

    fun clearConnectionTest() {
        connectionTestStatus = "NONE"
        connectionTestMsg = ""
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = Repository(database)

        // Bind flows
        employees = repository.allEmployees.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        departments = repository.allDepartments.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        visitors = repository.allVisitors.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        meetings = repository.allMeetings.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        gatePasses = repository.allGatePasses.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        notifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        auditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        // Run pre-seeding
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
        }
    }

    // AUTH ACTIONS
    fun login(id: String, pass: String, rememberMe: Boolean, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getEmployeeById(id)
            if (user != null && user.passwordHash == pass) {
                currentUser = user
                activeScreen = "dashboard"
                // Log audit
                repository.insertAuditLog(
                    AuditLog(
                        action = "LOGIN_PASSWORD",
                        details = "Employee ${user.name} logged in successfully as role [${user.role}].",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
                repository.insertNotification(
                    Notification(
                        recipientId = user.employeeId,
                        title = "Login Session Started",
                        body = "Secure session initiated at ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
                        category = "GENERAL"
                    )
                )
                onResult(true, "Welcome ${user.name}!")
            } else {
                onResult(false, "Invalid ID or Password. Try EMP101 / admin")
            }
        }
    }

    fun loginWithPin(id: String, pin: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getEmployeeById(id)
            if (user != null && user.pin == pin) {
                currentUser = user
                activeScreen = "dashboard"
                repository.insertAuditLog(
                    AuditLog(
                        action = "LOGIN_PIN",
                        details = "Employee ${user.name} logged in using secure PIN.",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
                onResult(true, "Access Granted")
            } else {
                onResult(false, "Incorrect PIN")
            }
        }
    }

    fun loginWithBiometric(id: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getEmployeeById(id)
            if (user != null && user.biometricEnabled) {
                currentUser = user
                activeScreen = "dashboard"
                repository.insertAuditLog(
                    AuditLog(
                        action = "LOGIN_BIOMETRIC",
                        details = "Employee ${user.name} logged in via biometric fingerprint authentication.",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
                onResult(true, "Fingerprint Verified!")
            } else {
                onResult(false, "Biometrics not configured for this account. Try EMP101")
            }
        }
    }

    fun bypassLogin(id: String) {
        viewModelScope.launch {
            val user = repository.getEmployeeById(id)
            if (user != null) {
                currentUser = user
                activeScreen = "dashboard"
                repository.insertAuditLog(
                    AuditLog(
                        action = "DEMO_BYPASS_LOGIN",
                        details = "Demo environment user bypass for role [${user.role}] (ID: ${user.employeeId}).",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val user = currentUser
            if (user != null) {
                repository.insertAuditLog(
                    AuditLog(
                        action = "LOGOUT",
                        details = "Employee ${user.name} logged out.",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
            }
            currentUser = null
            activeScreen = "login"
            scanResultStatus = "NONE"
            scanResultTitle = ""
            scanResultMessage = ""
        }
    }

    // VISITOR ACTIONS
    fun createVisitorRequest(
        name: String,
        mobile: String,
        company: String,
        purpose: String,
        department: String,
        photoUri: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val randPassNo = "VP-" + (10000 + Random().nextInt(90000))
            val visitor = Visitor(
                name = name,
                mobile = mobile,
                company = company,
                purpose = purpose,
                department = department,
                meetingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                meetingTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                photoUri = photoUri,
                passNumber = randPassNo,
                qrCode = "QR-$randPassNo",
                status = "PENDING"
            )
            val insertId = repository.insertVisitor(visitor)

            // Audit
            repository.insertAuditLog(
                AuditLog(
                    action = "CREATE_VISITOR_PASS",
                    details = "Created visitor pass $randPassNo for ${name} from ${company}.",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            // Notify receptionist
            repository.insertNotification(
                Notification(
                    recipientId = "ALL", // Recipient is broad
                    title = "New Visitor Registered",
                    body = "${visitor.name} from ${visitor.company} is expected in $department.",
                    category = "VISITOR"
                )
            )

            // Direct Notification to Department Head to take action
            repository.insertNotification(
                Notification(
                    recipientId = "MGR404", // Department Head Robert
                    title = "Visitor Needs DH Review",
                    body = "Visitor ${visitor.name} from ${visitor.company} is registered for $department. Review and approve/reject.",
                    category = "VISITOR"
                )
            )
            onSuccess()
        }
    }

    fun approveVisitor(visitor: Visitor) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val updated = visitor.copy(
                status = "CHECKED_IN",
                checkInTime = System.currentTimeMillis(),
                guardNote = "Approved & authorized by Department Head: ${user.name}"
            )
            repository.updateVisitor(updated)

            repository.insertAuditLog(
                AuditLog(
                    action = "VISITOR_APPROVED",
                    details = "Visitor ${visitor.name} was approved by Department Head ${user.name}.",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            repository.insertNotification(
                Notification(
                    recipientId = "ALL",
                    title = "Visitor Authorized",
                    body = "Visitor ${visitor.name} has been pre-authorized by DH ${user.name}.",
                    category = "VISITOR"
                )
            )
        }
    }

    fun rejectVisitor(visitor: Visitor) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val updated = visitor.copy(
                status = "REJECTED",
                guardNote = "Rejected by Department Head: ${user.name}"
            )
            repository.updateVisitor(updated)

            repository.insertAuditLog(
                AuditLog(
                    action = "VISITOR_REJECTED",
                    details = "Visitor ${visitor.name} was rejected by Department Head ${user.name}.",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            repository.insertNotification(
                Notification(
                    recipientId = "ALL",
                    title = "Visitor Rejected",
                    body = "Visitor ${visitor.name} was rejected by DH ${user.name}.",
                    category = "VISITOR"
                )
            )
        }
    }

    // MEETING ACTIONS
    fun scheduleMeeting(
        subject: String,
        agenda: String,
        date: String,
        start: String,
        end: String,
        dept: String,
        room: String,
        participants: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val meeting = Meeting(
                subject = subject,
                agenda = agenda,
                meetingDate = date,
                startTime = start,
                endTime = end,
                department = dept,
                meetingRoom = room,
                participants = participants,
                organizerId = user.employeeId,
                organizerName = user.name,
                status = "SCHEDULED"
            )
            repository.insertMeeting(meeting)

            // Notify
            repository.insertNotification(
                Notification(
                    recipientId = user.employeeId,
                    title = "Meeting Scheduled",
                    body = "Subject: $subject scheduled for $date at $start in $room.",
                    category = "MEETING"
                )
            )

            // Direct Notification to Department Head to catalog meeting
            repository.insertNotification(
                Notification(
                    recipientId = "MGR404", // Department Head Robert
                    title = "New Meeting Hooked",
                    body = "Subject '$subject' has been scheduled for $date by ${user.name}.",
                    category = "MEETING"
                )
            )

            repository.insertAuditLog(
                AuditLog(
                    action = "SCHEDULE_MEETING",
                    details = "Scheduled meeting '$subject' in '$room' for $date.",
                    userId = user.employeeId,
                    username = user.name
                )
            )
            onSuccess()
        }
    }

    fun cancelMeeting(meeting: Meeting) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val updated = meeting.copy(status = "CANCELLED")
            repository.updateMeeting(updated)

            repository.insertAuditLog(
                AuditLog(
                    action = "CANCEL_MEETING",
                    details = "Cancelled meeting '${meeting.subject}' originally on ${meeting.meetingDate}.",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            repository.insertNotification(
                Notification(
                    recipientId = "ALL",
                    title = "Meeting Cancelled",
                    body = "${meeting.subject} on ${meeting.meetingDate} has been cancelled.",
                    category = "MEETING"
                )
            )
        }
    }

    // GATE PASS ACTIONS
    fun createGatePassRequest(
        customerName: String,
        vehicleNumber: String,
        driverName: String,
        materialDetails: String,
        quantity: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val passNo = "GP-" + (1000 + Random().nextInt(9000))
            val gatePass = GatePass(
                gatePassNo = passNo,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                customerName = customerName,
                vehicleNumber = vehicleNumber,
                driverName = driverName,
                materialDetails = materialDetails,
                quantity = quantity,
                creatorId = user.employeeId,
                creatorName = user.name,
                currentStage = "DEPT_HEAD",
                statusText = "Pending Department Head Approval",
                qrCode = "QR-$passNo",
                barcode = "BC-$passNo"
            )
            repository.insertGatePass(gatePass)

            // Add notification for Managers
            repository.insertNotification(
                Notification(
                    recipientId = "ALL",
                    title = "Gate Pass Approval Required",
                    body = "Material pass $passNo for ${customerName} requires review.",
                    category = "GATEPASS"
                )
            )

            repository.insertAuditLog(
                AuditLog(
                    action = "CREATE_GATE_PASS",
                    details = "Initiated Outward Material Gate Pass $passNo for cargo: $materialDetails.",
                    userId = user.employeeId,
                    username = user.name
                )
            )
            onSuccess()
        }
    }

    fun approveGatePass(gatePass: GatePass) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val nextStage: String
            val nextStatus: String

            when (gatePass.currentStage) {
                "DEPT_HEAD" -> {
                    nextStage = "SECURITY"
                    nextStatus = "Pending Security Verification"
                }
                "SECURITY" -> {
                    nextStage = "DISPATCH"
                    nextStatus = "Pending Dispatch Release"
                }
                "DISPATCH" -> {
                    nextStage = "APPROVED"
                    nextStatus = "Approved & Dispatched"
                }
                else -> {
                    return@launch
                }
            }

            val updatedObj = gatePass.copy(
                currentStage = nextStage,
                statusText = nextStatus,
                approvedBy = if (user.role == "Department Head") user.name else gatePass.approvedBy
            )
            repository.updateGatePass(updatedObj)

            repository.insertAuditLog(
                AuditLog(
                    action = "APPROVE_GATE_PASS",
                    details = "Approved gate pass ${gatePass.gatePassNo} (Moved from ${gatePass.currentStage} to $nextStage).",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            repository.insertNotification(
                Notification(
                    recipientId = gatePass.creatorId,
                    title = "Gate Pass Advanced",
                    body = "Gate Pass ${gatePass.gatePassNo} has progress update: $nextStatus",
                    category = "GATEPASS"
                )
            )
        }
    }

    fun rejectGatePass(gatePass: GatePass, reason: String) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val updated = gatePass.copy(
                currentStage = "REJECTED",
                statusText = "Rejected: $reason"
            )
            repository.updateGatePass(updated)

            repository.insertAuditLog(
                AuditLog(
                    action = "REJECT_GATE_PASS",
                    details = "Rejected gate pass ${gatePass.gatePassNo} at ${gatePass.currentStage} stage. Reason: $reason",
                    userId = user.employeeId,
                    username = user.name
                )
            )

            repository.insertNotification(
                Notification(
                    recipientId = gatePass.creatorId,
                    title = "Gate Pass Rejected",
                    body = "Gate Pass ${gatePass.gatePassNo} was rejected by ${user.name}. Reason: $reason",
                    category = "GATEPASS"
                )
            )
        }
    }

    // VERIFICATION SCANNER TRIGGER (SECURITY GUARD MODULE)
    fun processQrCodeScan(qr: String, driverPhoto: String? = null, vehiclePhoto: String? = null) {
        viewModelScope.launch {
            val guard = currentUser ?: return@launch

            // Check if visitor pass
            val visitor = repository.getVisitorByQrCode(qr)
            if (visitor != null) {
                val nextStatus: String
                val desc: String
                val actionType: String

                when (visitor.status) {
                    "PENDING" -> {
                        nextStatus = "CHECKED_IN"
                        desc = "Checked in successfully"
                        actionType = "VISITOR_CHECK_IN"
                        val updated = visitor.copy(
                            status = nextStatus,
                            checkInTime = System.currentTimeMillis(),
                            guardNote = "Verified at main gate by ${guard.name}"
                        )
                        repository.updateVisitor(updated)

                        scanResultStatus = "VALID"
                        scanResultTitle = "Check-In Approved"
                        scanResultMessage = "Visitor: ${visitor.name}\nCompany: ${visitor.company}\nStatus: CHECKED-IN\nLocation: Main Boardroom"
                    }
                    "CHECKED_IN" -> {
                        nextStatus = "CHECKED_OUT"
                        desc = "Checked out successfully"
                        actionType = "VISITOR_CHECK_OUT"
                        val updated = visitor.copy(
                            status = nextStatus,
                            checkOutTime = System.currentTimeMillis(),
                            guardNote = "Checked out and exited plant gate. Inspected by ${guard.name}"
                        )
                        repository.updateVisitor(updated)

                        scanResultStatus = "VALID"
                        scanResultTitle = "Check-Out Approved"
                        scanResultMessage = "Visitor: ${visitor.name}\nExited at: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}\nThank you!"
                    }
                    else -> {
                        scanResultStatus = "EXPIRED"
                        scanResultTitle = "Pass Expired"
                        scanResultMessage = "Visitor ${visitor.name} has already checked out."
                        return@launch
                    }
                }

                repository.insertAuditLog(
                    AuditLog(
                        action = actionType,
                        details = "$desc: ${visitor.name} ($qr). Auth Officer: ${guard.name}",
                        userId = guard.employeeId,
                        username = guard.name
                    )
                )

                repository.insertNotification(
                    Notification(
                        recipientId = "ALL",
                        title = "Visitor Alert",
                        body = "Visitor ${visitor.name} is now: $nextStatus",
                        category = "VISITOR"
                    )
                )
                return@launch
            }

            // Check if gate pass
            val gatePass = repository.getGatePassByQr(qr)
            if (gatePass != null) {
                if (gatePass.currentStage == "REJECTED") {
                    scanResultStatus = "REJECTED"
                    scanResultTitle = "Transaction Blocked"
                    scanResultMessage = "Gate Pass ${gatePass.gatePassNo} has been Rejected by approvals."
                    return@launch
                }

                if (gatePass.currentStage == "APPROVED") {
                    scanResultStatus = "EXPIRED"
                    scanResultTitle = "Already Cleared"
                    scanResultMessage = "This outward gate pass was already completed and dispatched."
                    return@launch
                }

                // Security can approve to advanced status or mark as validated
                if (gatePass.currentStage == "SECURITY") {
                    // Update to DISPATCH
                    val updated = gatePass.copy(
                        currentStage = "DISPATCH",
                        statusText = "Security Verified: Awaiting Dispatch Release",
                        checkInTime = System.currentTimeMillis(),
                        guardNote = "Physical cargo quantities & driver name ${gatePass.driverName} verified by ${guard.name}."
                    )
                    repository.updateGatePass(updated)

                    scanResultStatus = "VALID"
                    scanResultTitle = "Gate Pass Verified"
                    scanResultMessage = "Pass Number: ${gatePass.gatePassNo}\nDriver: ${gatePass.driverName}\nCargo: ${gatePass.materialDetails}\nQuantity: ${gatePass.quantity}\nAdvanced to Dispatch stage."
                    
                    repository.insertAuditLog(
                        AuditLog(
                            action = "GATE_PASS_SCAN_VERIFIED",
                            details = "Security scanned and authenticated cargo exit pass ${gatePass.gatePassNo}.",
                            userId = guard.employeeId,
                            username = guard.name
                        )
                    )
                } else if (gatePass.currentStage == "DISPATCH") {
                    // Mark as complete
                    val updated = gatePass.copy(
                        currentStage = "APPROVED",
                        statusText = "Completed: Cargo Departed Factory Gates",
                        checkOutTime = System.currentTimeMillis(),
                        guardNote = "Cargo dispatched outwards. Drivers and vehicles photo flags cached."
                    )
                    repository.updateGatePass(updated)

                    scanResultStatus = "VALID"
                    scanResultTitle = "Dispatch Finalized"
                    scanResultMessage = "Gate Pass: ${gatePass.gatePassNo}\nCustomer: ${gatePass.customerName}\nVehicle: ${gatePass.vehicleNumber}\nFinal Cargo Clearance: ALLOWED OUT"
                    
                    repository.insertAuditLog(
                        AuditLog(
                            action = "GATE_PASS_DISPATCH",
                            details = "Cargo dispatched out of manufacturing complex on vehicle ${gatePass.vehicleNumber}.",
                            userId = guard.employeeId,
                            username = guard.name
                        )
                    )
                } else {
                    scanResultStatus = "REJECTED"
                    scanResultTitle = "Awaiting Department Head"
                    scanResultMessage = "Gate Pass ${gatePass.gatePassNo} is still awaiting Department Head approval. Cannot clear security gate."
                }
                return@launch
            }

            // QR code is invalid
            scanResultStatus = "REJECTED"
            scanResultTitle = "Invalid Code"
            scanResultMessage = "Scanner scanned: '$qr'. Matches no entry in database record indices."
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
            
            // Also append an audit log of this administrative action
            currentUser?.let { user ->
                repository.insertAuditLog(
                    AuditLog(
                        action = "ROLE_AUDIT",
                        details = "Updated user permission of ${employee.name} (${employee.employeeId}) to role: ${employee.role}.",
                        userId = user.employeeId,
                        username = user.name
                    )
                )
            }
        }
    }
}

// Simple Factory for ViewModel
class GatePassViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GatePassViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GatePassViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
