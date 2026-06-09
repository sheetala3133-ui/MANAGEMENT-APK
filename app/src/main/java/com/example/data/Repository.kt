package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val db: AppDatabase) {

    val allEmployees: Flow<List<Employee>> = db.employeeDao().getAllEmployees()
    val allDepartments: Flow<List<Department>> = db.departmentDao().getAllDepartments()
    val allVisitors: Flow<List<Visitor>> = db.visitorDao().getAllVisitors()
    val allMeetings: Flow<List<Meeting>> = db.meetingDao().getAllMeetings()
    val allGatePasses: Flow<List<GatePass>> = db.gatePassDao().getAllGatePasses()
    val allNotifications: Flow<List<Notification>> = db.notificationDao().getAllNotifications()
    val allAuditLogs: Flow<List<AuditLog>> = db.auditLogDao().getAllLogs()

    suspend fun getEmployeeById(id: String): Employee? = db.employeeDao().getEmployeeById(id)
    suspend fun insertEmployee(employee: Employee) = db.employeeDao().insertEmployee(employee)
    suspend fun updateEmployee(employee: Employee) = db.employeeDao().updateEmployee(employee)

    suspend fun insertDepartment(department: Department) = db.departmentDao().insertDepartment(department)

    suspend fun getVisitorByQrCode(qr: String): Visitor? = db.visitorDao().getVisitorByQrCode(qr)
    suspend fun getVisitorById(id: Int): Visitor? = db.visitorDao().getVisitorById(id)
    suspend fun insertVisitor(visitor: Visitor): Long = db.visitorDao().insertVisitor(visitor)
    suspend fun updateVisitor(visitor: Visitor) = db.visitorDao().updateVisitor(visitor)

    fun getMeetingsByDate(date: String): Flow<List<Meeting>> = db.meetingDao().getMeetingsByDate(date)
    suspend fun insertMeeting(meeting: Meeting): Long = db.meetingDao().insertMeeting(meeting)
    suspend fun updateMeeting(meeting: Meeting) = db.meetingDao().updateMeeting(meeting)

    suspend fun getGatePassByQr(qr: String): GatePass? = db.gatePassDao().getGatePassByQr(qr)
    suspend fun insertGatePass(gatePass: GatePass): Long = db.gatePassDao().insertGatePass(gatePass)
    suspend fun updateGatePass(gatePass: GatePass) = db.gatePassDao().updateGatePass(gatePass)

    fun getNotificationsForUser(userId: String): Flow<List<Notification>> = db.notificationDao().getNotificationsForUser(userId)
    suspend fun insertNotification(notification: Notification) = db.notificationDao().insertNotification(notification)
    suspend fun markNotificationAsRead(id: Int) = db.notificationDao().markAsRead(id)

    suspend fun insertAuditLog(log: AuditLog) = db.auditLogDao().insertLog(log)

    suspend fun seedDatabaseIfNeeded() {
        val count = db.employeeDao().getEmployeeById("EMP101")
        if (count != null) {
            // Already seeded
            return
        }

        // 1. Seed Departments
        val depts = listOf(
            "Information Technology",
            "Human Resources",
            "Production & Manufacturing",
            "Logistics & Dispatch",
            "Quality Assurance",
            "Finance"
        )
        depts.forEach { db.departmentDao().insertDepartment(Department(it)) }

        // 2. Seed Employees
        val employees = listOf(
            Employee(
                employeeId = "EMP101",
                name = "John Doe-Employee",
                role = "Employee",
                department = "Information Technology",
                mobile = "+1-555-0101",
                pin = "1234",
                passwordHash = "admin",
                biometricEnabled = true
            ),
            Employee(
                employeeId = "SEC202",
                name = "Officer Marcus-Guard",
                role = "Security Guard",
                department = "Logistics & Dispatch",
                mobile = "+1-555-0202",
                pin = "1234",
                passwordHash = "admin",
                biometricEnabled = true
            ),
            Employee(
                employeeId = "REC303",
                name = "Alice Smith-Receptionist",
                role = "Receptionist",
                department = "Human Resources",
                mobile = "+1-555-0303",
                pin = "1234",
                passwordHash = "admin",
                biometricEnabled = true
            ),
            Employee(
                employeeId = "MGR404",
                name = "Robert Manager-Dept Head",
                role = "Department Head",
                department = "Information Technology",
                mobile = "+1-555-0404",
                pin = "1234",
                passwordHash = "admin",
                biometricEnabled = true
            ),
            Employee(
                employeeId = "ADM001",
                name = "Devin Admin-Admin",
                role = "Admin",
                department = "Finance",
                mobile = "+1-555-0505",
                pin = "1234",
                passwordHash = "admin",
                biometricEnabled = true
            )
        )
        employees.forEach { db.employeeDao().insertEmployee(it) }

        // Get today's date for realistic displays
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFmt.format(Date())

        // 3. Seed Visitors
        val visitors = listOf(
            Visitor(
                name = "Sarah Connor",
                mobile = "+1-555-0011",
                company = "Cyberdyne Systems",
                purpose = "Database Core Audit",
                department = "Information Technology",
                meetingDate = todayStr,
                meetingTime = "09:30",
                passNumber = "VP-77215",
                qrCode = "QR-VP-77215",
                status = "PENDING"
            ),
            Visitor(
                name = "Tony Stark",
                mobile = "+1-555-3000",
                company = "Stark Industries",
                purpose = "Solenoid Energy Review",
                department = "Production & Manufacturing",
                meetingDate = todayStr,
                meetingTime = "14:00",
                passNumber = "VP-90112",
                qrCode = "QR-VP-90112",
                status = "CHECKED_IN",
                checkInTime = System.currentTimeMillis() - 3600000
            ),
            Visitor(
                name = "Bruce Wayne",
                mobile = "+1-555-0007",
                company = "Wayne Enterprises",
                purpose = "CSR Project Meeting",
                department = "Human Resources",
                meetingDate = todayStr,
                meetingTime = "16:15",
                passNumber = "VP-89211",
                qrCode = "QR-VP-89211",
                status = "CHECKED_OUT",
                checkInTime = System.currentTimeMillis() - 7200000,
                checkOutTime = System.currentTimeMillis() - 1800000
            )
        )
        visitors.forEach { db.visitorDao().insertVisitor(it) }

        // 4. Seed Meetings
        val meetings = listOf(
            Meeting(
                subject = "Production Operations Sync",
                agenda = "Review robotic arm safety configurations & floor plan layout modifications",
                meetingDate = todayStr,
                startTime = "09:00",
                endTime = "10:30",
                department = "Production & Manufacturing",
                participants = "Robert Manager, Tony Stark, Chief Engineer",
                meetingRoom = "Conference Room Alpha",
                organizerId = "MGR404",
                organizerName = "Robert Manager"
            ),
            Meeting(
                subject = "IT Security Upgrade Planning",
                agenda = "Discuss multi-factor gate biometric access and SQLite local encrypted database logs project.",
                meetingDate = todayStr,
                startTime = "11:00",
                endTime = "12:00",
                department = "Information Technology",
                participants = "John Doe, Sarah Connor, IT Admin",
                meetingRoom = "IT Lab Room 4",
                organizerId = "EMP101",
                organizerName = "John Doe-Employee"
            ),
            Meeting(
                subject = "Logistics Safety Standards Review",
                agenda = "Quarterly analysis of entry logs, dispatch trucks gate controls, and vehicle driver audits.",
                meetingDate = todayStr,
                startTime = "15:00",
                endTime = "16:00",
                department = "Logistics & Dispatch",
                participants = "Officer Marcus, Lead Operator",
                meetingRoom = "Main Admin Block Boardroom",
                organizerId = "SEC202",
                organizerName = "Officer Marcus-Guard"
            )
        )
        meetings.forEach { db.meetingDao().insertMeeting(it) }

        // 5. Seed GatePasses
        val gatePasses = listOf(
            GatePass(
                gatePassNo = "GP-103",
                date = todayStr,
                customerName = "Tesla Logistics Corporation",
                vehicleNumber = "TX-88-M3",
                driverName = "Logan White",
                materialDetails = "Aluminum Shell Castings & Coils",
                quantity = "12 Tons (18 Bundles)",
                creatorId = "EMP101",
                creatorName = "John Doe-Employee",
                currentStage = "DEPT_HEAD",
                statusText = "Pending Department Head Approval",
                qrCode = "QR-GP-103",
                barcode = "BC-GP-103"
            ),
            GatePass(
                gatePassNo = "GP-104",
                date = todayStr,
                customerName = "Freight Logistics Ltd",
                vehicleNumber = "CA-909-RT",
                driverName = "David Banner",
                materialDetails = "Re-usable Wooden Pallets",
                quantity = "200 Pieces",
                creatorId = "EMP101",
                creatorName = "John Doe-Employee",
                currentStage = "SECURITY",
                statusText = "Pending Security Verification",
                qrCode = "QR-GP-104",
                barcode = "BC-GP-104"
            ),
            GatePass(
                gatePassNo = "GP-105",
                date = todayStr,
                customerName = "Toyota Supply Base",
                vehicleNumber = "MI-567-TR",
                driverName = "Frank Parker",
                materialDetails = "Electronic Engine ECUs",
                quantity = "50 Units",
                creatorId = "EMP101",
                creatorName = "John Doe-Employee",
                currentStage = "DISPATCH",
                statusText = "Approved: Awaiting Final Dispatch",
                qrCode = "QR-GP-105",
                barcode = "BC-GP-105"
            )
        )
        gatePasses.forEach { db.gatePassDao().insertGatePass(it) }

        // 6. Seed Notifications & Audit
        db.notificationDao().insertNotification(
            Notification(
                recipientId = "EMP101",
                title = "Welcome to GatePass Manager",
                body = "System database initialized. All features ready for validation.",
                category = "GENERAL"
            )
        )
        db.notificationDao().insertNotification(
            Notification(
                recipientId = "MGR404",
                title = "Gate Pass Approval Required",
                body = "Gate Pass GP-103 requires department head review.",
                category = "GATEPASS"
            )
        )

        db.auditLogDao().insertLog(
            AuditLog(
                action = "SYSTEM_INITIALIZATION",
                details = "Local sandbox database pre-populated with default corporate assets, user profiles (5 roles), mock records & charts.",
                userId = "SYSTEM",
                username = "System Setup"
            )
        )
    }
}
