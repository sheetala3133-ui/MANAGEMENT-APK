package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey val employeeId: String, // e.g. EMP101
    val name: String,
    val role: String, // Employee, Security Guard, Receptionist, Department Head, Admin
    val department: String,
    val mobile: String,
    val pin: String = "1234",
    val passwordHash: String, // simple plain password for simulation
    val biometricEnabled: Boolean = true,
    val otpCode: String = ""
)

@Entity(tableName = "departments")
data class Department(
    @PrimaryKey val name: String
)

@Entity(tableName = "visitors")
data class Visitor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val mobile: String,
    val company: String,
    val purpose: String,
    val department: String,
    val meetingDate: String, // yyyy-MM-dd
    val meetingTime: String, // HH:mm
    val photoUri: String? = null,
    val passNumber: String, // VP-100293
    val qrCode: String,     // QR-VP-100293
    val status: String,    // PENDING, CHECKED_IN, CHECKED_OUT, REJECTED
    val checkInTime: Long? = null,
    val checkOutTime: Long? = null,
    val guardNote: String? = null
)

@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val agenda: String,
    val meetingDate: String, // yyyy-MM-dd
    val startTime: String,   // HH:mm
    val endTime: String,     // HH:mm
    val department: String,
    val participants: String, // Comma-separated list of emails or names
    val meetingRoom: String,
    val organizerId: String,
    val organizerName: String,
    val status: String = "SCHEDULED" // SCHEDULED, CANCELLED, RESCHEDULED
)

@Entity(tableName = "gate_passes")
data class GatePass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gatePassNo: String, // GP-99823
    val date: String,       // yyyy-MM-dd
    val customerName: String,
    val vehicleNumber: String,
    val driverName: String,
    val materialDetails: String,
    val quantity: String,
    val creatorId: String,
    val creatorName: String,
    val approvedBy: String? = null,
    val currentStage: String, // EMPLOYEE -> DEPT_HEAD -> SECURITY -> DISPATCH -> APPROVED / REJECTED
    val statusText: String,   // e.g. "Pending Dept Head Approval", "Arrived at Gate", "Approved"
    val qrCode: String,       // QR-GP-99823
    val barcode: String,      // BC-GP-99823
    val checkInTime: Long? = null,
    val checkOutTime: Long? = null,
    val guardNote: String? = null
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipientId: String, // employee ID or "ALL"
    val title: String,
    val body: String,
    val category: String, // MEETING, VISITOR, GATEPASS, GENERAL
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String,
    val username: String
)
