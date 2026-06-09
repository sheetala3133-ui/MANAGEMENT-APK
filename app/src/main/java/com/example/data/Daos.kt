package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE employeeId = :empId LIMIT 1")
    suspend fun getEmployeeById(empId: String): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Update
    suspend fun updateEmployee(employee: Employee)
}

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments")
    fun getAllDepartments(): Flow<List<Department>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: Department)
}

@Dao
interface VisitorDao {
    @Query("SELECT * FROM visitors ORDER BY id DESC")
    fun getAllVisitors(): Flow<List<Visitor>>

    @Query("SELECT * FROM visitors WHERE qrCode = :qrCode LIMIT 1")
    suspend fun getVisitorByQrCode(qrCode: String): Visitor?

    @Query("SELECT * FROM visitors WHERE id = :id LIMIT 1")
    suspend fun getVisitorById(id: Int): Visitor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: Visitor): Long

    @Update
    suspend fun updateVisitor(visitor: Visitor)
}

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY meetingDate ASC, startTime ASC")
    fun getAllMeetings(): Flow<List<Meeting>>

    @Query("SELECT * FROM meetings WHERE meetingDate = :date ORDER BY startTime ASC")
    fun getMeetingsByDate(date: String): Flow<List<Meeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting): Long

    @Update
    suspend fun updateMeeting(meeting: Meeting)
}

@Dao
interface GatePassDao {
    @Query("SELECT * FROM gate_passes ORDER BY id DESC")
    fun getAllGatePasses(): Flow<List<GatePass>>

    @Query("SELECT * FROM gate_passes WHERE qrCode = :qrCode LIMIT 1")
    suspend fun getGatePassByQr(qrCode: String): GatePass?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatePass(gatePass: GatePass): Long

    @Update
    suspend fun updateGatePass(gatePass: GatePass)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE recipientId = :recipientId OR recipientId = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForUser(recipientId: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)
}
