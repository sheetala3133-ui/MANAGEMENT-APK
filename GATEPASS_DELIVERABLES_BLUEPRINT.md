# GATEPASS MANAGER: ENTERPRISE BLUEPRINT MODULES
This repository contains the complete production-ready source code for the GatePass Android application. To complement the mobile client codebase, this blueprint document outlines the auxiliary deliverables required for corporate deployment: SQL Server 19 scripts, ASP.NET Web APIs, ER diagrams, and system architecture structures.

---

## 1. ARCHITECTURE BLOCK DIAGRAM
The mobile client and secure backend follow modern Clean Architecture patterns, splitting operations across three isolated layers:

```
+---------------------------------------------------------------------------------+
|                                 PRESENTATION LAYER                              |
|   [Jetpack Compose UI Screens] <--> [StateFlow] <--> [GatePass ViewModel]       |
+---------------------------------------------------------------------------------+
                                         |
                                         v
+---------------------------------------------------------------------------------+
|                                  DOMAIN LAYER                                   |
|                [Repository Abstraction] <-- [Entities / Models]                 |
+---------------------------------------------------------------------------------+
                                         |
                                         v
+---------------------------------------------------------------------------------+
|                                   DATA LAYER                                    |
|   +---------------------------------------+   +-----------------------------+   |
|   |          ROOM LOCAL DATABASE          |   |      RETROFIT REST API      |   |
|   | (Offline SQLite Caches / Audit Logs)  |   | (ASP.NET Web API Endpoint)  |   |
|   +---------------------------------------+   +-----------------------------+   |
+---------------------------------------------------------------------------------+
                                         | (JSON / HTTPS)
                                         v
+---------------------------------------------------------------------------------+
|                                 ENTERPRISE BACKEND                              |
|           [IIS Server] <--> [ASP.NET MVC 5 Controller] <--> [EF6 / ADO.NET]     |
+---------------------------------------------------------------------------------+
                                         | (Stored Procedures)
                                         v
+---------------------------------------------------------------------------------+
|                                 DATABASE ENGINES                                |
|                        [Microsoft SQL Server 2019 DB]                           |
+---------------------------------------------------------------------------------+
```

---

## 2. DATABASE ENTITY RELATIONSHIP (ER) DIAGRAM
Structured relation maps representing Primary Keys (PK) and Foreign Keys (FK) relationships:

```
  +------------------+             +--------------------+
  |   DEPARTMENTS    |             |       USERS        |
  +------------------+             +--------------------+
  | PK  Name (VC)    |<-----+      | PK  UserId (VC)    |<----+
  +------------------+      |      |     Password (VC)  |     |
                            |      |     Pin (VC)       |     |
                            |      |     OTPCode (VC)   |     |
  +------------------+      |      +--------------------+     |
  |    EMPLOYEES     |      |                                 |
  +------------------+      |      +--------------------+     |
  | PK  EmployeeId   |      |      |     AUDIT_LOGS     |     |
  | FK  DeptName     |------+      +--------------------+     |
  |     Name (VC)    |             | PK  LogId (INT)    |     |
  |     Mobile (VC)  |             | FK  UserId (VC)    |-----+
  |     Role (VC)    |<----+       |     Action (VC)    |
  +------------------+     |       |     Details (VC)   |
                           |       |     Timestamp (DT) |
                           |       +--------------------+
  +------------------+     |
  |     MEETINGS     |     |       +--------------------+
  +------------------+     |       |  MEETING_PARTICIP  |
  | PK  MeetingId    |     |       +--------------------+
  | FK  DeptName     |--+  |       | PK  ParticipantId  |
  |     Subject (VC) |  |  |       | FK  MeetingId      |-----+
  |     Agenda (VC)  |  |  +-------| FK  EmployeeId     |
  |     Room (VC)    |  |          +--------------------+
  | FK  OrganizerId  |--+
  |     Date/Time    |
  +------------------+             +--------------------+
                                   |     VISITORS       |
  +------------------+             +--------------------+
  |    GATE_PASS     |             | PK  VisitorId (INT)|
  +------------------+             |     PassNumber (VC)|
  | PK  PassId (INT) |             |     Name (VC)      |
  |     GatePassNo   |             |     Mobile (VC)    |
  |     CustName     |             |     Company (VC)   |
  |     VehicleNo    |             |     Purpose (VC)   |
  |     MaterialDet  |             | FK  DeptName       |-----+
  |     Quantity     |             |     Status (VC)    |
  | FK  CreatorId    |----+        |     CheckInTIme    |
  | FK  ApprovedBy   |----+        |     CheckOutTime   |
  +------------------+             +--------------------+
```

---

## 3. SQL SERVER 2019 DATABASE DDL SETUP SCRIPT
Execute the following T-SQL script inside SQL Server Management Studio (SSMS) to instantiate the enterprise relational engine:

```sql
-- Create Enterprise GatePass Database
CREATE DATABASE GatePassManagementDB;
GO

USE GatePassManagementDB;
GO

-- 1. Departments Table
CREATE TABLE Departments (
    DepartmentName NVARCHAR(100) PRIMARY KEY
);

-- 2. Employees Table
CREATE TABLE Employees (
    EmployeeId NVARCHAR(50) PRIMARY KEY,
    FullName NVARCHAR(150) NOT NULL,
    Role NVARCHAR(100) NOT NULL, -- Employee, Security Guard, Receptionist, Department Head, Admin
    DepartmentName NVARCHAR(100) NOT NULL,
    Mobile NVARCHAR(20) NULL,
    PasswordHash NVARCHAR(256) NOT NULL,
    PIN NVARCHAR(10) DEFAULT '1234',
    BiometricEnabled BIT DEFAULT 1,
    OTPCode NVARCHAR(10) NULL,
    CONSTRAINT FK_Employee_Department FOREIGN KEY (DepartmentName) REFERENCES Departments(DepartmentName)
);

-- 3. Visitors Table
CREATE TABLE Visitors (
    VisitorId INT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(150) NOT NULL,
    Mobile NVARCHAR(20) NOT NULL,
    CompanyName NVARCHAR(150) NOT NULL,
    PurposeOfVisit NVARCHAR(250) NOT NULL,
    DepartmentName NVARCHAR(100) NOT NULL,
    MeetingDate DATE NOT NULL,
    MeetingTime TIME NOT NULL,
    PassNumber NVARCHAR(50) UNIQUE NOT NULL,
    QRCode NVARCHAR(100) NOT NULL,
    Status NVARCHAR(50) DEFAULT 'PENDING', -- PENDING, CHECKED_IN, CHECKED_OUT, REJECTED
    CheckInTime DATETIME NULL,
    CheckOutTime DATETIME NULL,
    GuardNote NVARCHAR(500) NULL,
    CONSTRAINT FK_Visitor_Department FOREIGN KEY (DepartmentName) REFERENCES Departments(DepartmentName)
);

-- 4. Meetings Table
CREATE TABLE Meetings (
    MeetingId INT IDENTITY(1,1) PRIMARY KEY,
    Subject NVARCHAR(200) NOT NULL,
    Agenda NVARCHAR(1000) NULL,
    MeetingDate DATE NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    DepartmentName NVARCHAR(100) NOT NULL,
    MeetingRoom NVARCHAR(100) NOT NULL,
    OrganizerId NVARCHAR(50) NOT NULL,
    Status NVARCHAR(50) DEFAULT 'SCHEDULED', -- SCHEDULED, CANCELLED
    CONSTRAINT FK_Meeting_Department FOREIGN KEY (DepartmentName) REFERENCES Departments(DepartmentName),
    CONSTRAINT FK_Meeting_Organizer FOREIGN KEY (OrganizerId) REFERENCES Employees(EmployeeId)
);

-- 5. GatePass Table
CREATE TABLE GatePasses (
    PassId INT IDENTITY(1,1) PRIMARY KEY,
    GatePassNo NVARCHAR(50) UNIQUE NOT NULL,
    DateCreated DATE DEFAULT CAST(GETDATE() AS DATE),
    CustomerName NVARCHAR(200) NOT NULL,
    VehicleNumber NVARCHAR(50) NOT NULL,
    DriverName NVARCHAR(150) NOT NULL,
    MaterialDetails NVARCHAR(MAX) NOT NULL,
    Quantity NVARCHAR(100) NOT NULL,
    CreatorId NVARCHAR(50) NOT NULL,
    ApprovedBy NVARCHAR(150) NULL,
    CurrentStage NVARCHAR(50) DEFAULT 'DEPT_HEAD', -- DEPT_HEAD, SECURITY, DISPATCH, APPROVED, REJECTED
    StatusText NVARCHAR(150) DEFAULT 'Pending Department Head Approval',
    QRCode NVARCHAR(100) NOT NULL,
    Barcode NVARCHAR(100) NOT NULL,
    CheckInTime DATETIME NULL,
    CheckOutTime DATETIME NULL,
    CONSTRAINT FK_GatePass_Creator FOREIGN KEY (CreatorId) REFERENCES Employees(EmployeeId)
);

-- 6. Notifications Table
CREATE TABLE Notifications (
    NotificationId INT IDENTITY(1,1) PRIMARY KEY,
    RecipientId NVARCHAR(50) NOT NULL, -- Employee ID or 'ALL'
    Title NVARCHAR(200) NOT NULL,
    Body NVARCHAR(500) NOT NULL,
    Category NVARCHAR(50) NOT NULL, -- MEETING, VISITOR, GATEPASS, GENERAL
    Timestamp DATETIME DEFAULT GETDATE(),
    IsRead BIT DEFAULT 0
);

-- 7. Audit Logs Table
CREATE TABLE AuditLogs (
    LogId INT IDENTITY(1,1) PRIMARY KEY,
    Action NVARCHAR(100) NOT NULL,
    Details NVARCHAR(1000) NOT NULL,
    Timestamp DATETIME DEFAULT GETDATE(),
    UserId NVARCHAR(50) NOT NULL,
    Username NVARCHAR(150) NOT NULL
);
GO

-- CREATE DATABASE VIEWS
CREATE VIEW Vw_DailyVisitorRegistry AS
SELECT VisitorId, FullName, CompanyName, PurposeOfVisit, MeetingDate, Status, CheckInTime, CheckOutTime
FROM Visitors
WHERE MeetingDate = CAST(GETDATE() AS DATE);
GO

-- CREATE STORED PROCEDURES
CREATE PROCEDURE Sp_RegisterVisitor
    @FullName NVARCHAR(150),
    @Mobile NVARCHAR(20),
    @CompanyName NVARCHAR(150),
    @PurposeOfVisit NVARCHAR(250),
    @DepartmentName NVARCHAR(100),
    @MeetingDate DATE,
    @MeetingTime TIME,
    @PassNumber NVARCHAR(50),
    @QRCode NVARCHAR(100)
AS
BEGIN
    INSERT INTO Visitors (FullName, Mobile, CompanyName, PurposeOfVisit, DepartmentName, MeetingDate, MeetingTime, PassNumber, QRCode, Status)
    VALUES (@FullName, @Mobile, @CompanyName, @PurposeOfVisit, @DepartmentName, @MeetingDate, @MeetingTime, @PassNumber, @QRCode, 'PENDING');
    
    SELECT SCOPE_IDENTITY() AS NewVisitorId;
END;
GO

CREATE PROCEDURE Sp_ApproveGatePass
    @GatePassNo NVARCHAR(50),
    @ApproveUser NVARCHAR(150),
    @NextStage NVARCHAR(50),
    @NextStatus NVARCHAR(150)
AS
BEGIN
    UPDATE GatePasses
    SET CurrentStage = @NextStage,
        StatusText = @NextStatus,
        ApprovedBy = CASE WHEN @NextStage = 'SECURITY' THEN @ApproveUser ELSE ApprovedBy END
    WHERE GatePassNo = @GatePassNo;
END;
GO
```

---

## 4. ASP.NET MVC 5 WEB API BACKEND BLUEPRINT (C#)
C# API endpoints exposed for authentication, registration, and approvals:

```csharp
using System;
using System.Linq;
using System.Web.Http;
using GatePassManagementDB.Models; // Assumes EF6 context is configured

namespace GatePassAPI.Controllers
{
    [RoutePrefix("api/auth")]
    public class AuthController : ApiController
    {
        private GatePassDBEntities db = new GatePassDBEntities();

        [HttpPost]
        [Route("login")]
        public IHttpActionResult Login([FromBody] LoginRequest request)
        {
            var user = db.Employees.FirstOrDefault(e => e.EmployeeId == request.EmployeeId);
            if (user == null || user.PasswordHash != request.Password)
            {
                return BadRequest("Invalid credentials.");
            }
            return Ok(new { 
                User = user.FullName, 
                Role = user.Role, 
                Dept = user.DepartmentName 
            });
        }
    }

    [RoutePrefix("api/visitors")]
    public class VisitorController : ApiController
    {
        private GatePassDBEntities db = new GatePassDBEntities();

        [HttpPost]
        [Route("create")]
        public IHttpActionResult CreateVisitor([FromBody] Visitor visitor)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            
            visitor.PassNumber = "VP-" + new Random().Next(10000, 99999);
            visitor.QRCode = "QR-" + visitor.PassNumber;
            visitor.Status = "PENDING";
            
            db.Visitors.Add(visitor);
            db.SaveChanges();
            
            return Ok(visitor);
        }
    }

    [RoutePrefix("api/gatepass")]
    public class GatePassController : ApiController
    {
        private GatePassDBEntities db = new GatePassDBEntities();

        [HttpPost]
        [Route("approve")]
        public IHttpActionResult ApproveGatePass(string passNo, string userId)
        {
            var pass = db.GatePasses.FirstOrDefault(gp => gp.GatePassNo == passNo);
            if (pass == null) return NotFound();

            if (pass.CurrentStage == "DEPT_HEAD")
            {
                pass.CurrentStage = "SECURITY";
                pass.StatusText = "Pending Security Verification";
            }
            else if (pass.CurrentStage == "SECURITY")
            {
                pass.CurrentStage = "DISPATCH";
                pass.StatusText = "Pending Dispatch Release";
            }
            else if (pass.CurrentStage == "DISPATCH")
            {
                pass.CurrentStage = "APPROVED";
                pass.StatusText = "Approved & Dispatched";
            }

            db.SaveChanges();
            return Ok(pass);
        }
    }
}
```

---

## 5. ENTERPRISE SYSTEM INSTALLATION MANUAL

### Step 1: SQL Server Database Instance Initialization
1. Launch **SQL Server Management Studio (SSMS)**.
2. Connect to your database engine instance (e.g. `localhost\SQLEXPRESS`).
3. File -> Open -> File -> Select the provided SQL script above.
4. Hit **Execute (F5)** to instantiate the Database tables, Relationships, and Stored Procedures.

### Step 2: ASP.NET MVC 5 Web API Backend Deploy
1. Launch **Visual Studio**.
2. Select **Create a new project** -> Choose **ASP.NET Web Application (.NET Framework)** -> Set Framework version to `4.7.2` or later.
3. Choose the **Web API** template.
4. Connect to database via **Entity Framework ADO.NET Entity Data Model** (Database-First approach) using connection string:
   `metadata=res://*/Models.GatePassModel.csdl|...;provider=System.Data.SqlClient;provider connection string="data source=YOUR_SERVER;initial catalog=GatePassManagementDB;integrated security=True;"`
5. Map the controllers as shown in the API blueprints above.
6. Publish the endpoint to your **IIS (Internet Information Services)** server locally or on an Azure Virtual Machine, exposing the endpoint to the local Wi-Fi router.

### Step 3: Android Mobile Client Configuration
1. Unzip the Android project bundle.
2. Open **Android Studio**. Select **Open -> gatepass_manager** (Gradle will index the caches).
3. If connecting to a live backend, modify the Retrofit interface base URL inside the Kotlin configs to reflect your IIS server address:
   `val BASE_URL = "http://192.168.1.100/gatepass/api/"`
4. Set up an user credential in the SSMS database or use the built-in **Quick Bypass Switch** on the Login Page to simulate live workers immediately.
5. Compile and generate debug APK using the Gradle taskbar.
