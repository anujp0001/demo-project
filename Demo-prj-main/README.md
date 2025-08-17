# Complete Testing Guide for Ticketing System

Here's a comprehensive step-by-step testing guide with curl commands to verify all functionality:

## **Phase 1: Setup & User Registration**

### **Step 1: Register First User (Regular User)**
```bash
curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "FirstUser",
    "password": "password123",
    "email": "firstuser@example.com"
}'
```

**Expected Response:**
```json
{
    "message": "User registered successfully",
    "token": null,
    "role": null
}
```

### **Step 2: Create Initial Admin User (Add to your DataInitializer)**
Make sure your DataInitializer creates an admin user:
```java
// This should run automatically when you start the application
// Admin user: username="admin", password="admin123"
```

### **Step 3: Login as Admin**
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "admin",
    "password": "admin123"
}'
```

**Expected Response:**
```json
{
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "role": "ADMIN"
}
```

**Save the admin token as ADMIN_TOKEN for future use.**

***

## **Phase 2: Admin Operations Testing**

### **Step 4: Create Support Agent (Admin Only)**
```bash
curl --location 'http://localhost:8080/api/admin/support-agents' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "SupportAgent1",
    "password": "agent123",
    "email": "agent1@example.com"
}'
```

**Expected Response:**
```json
{
    "message": "Support Agent created successfully"
}
```

### **Step 5: Create Another Admin (Admin Only)**
```bash
curl --location 'http://localhost:8080/api/admin/admins' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "Admin2",
    "password": "admin456",
    "email": "admin2@example.com"
}'
```

### **Step 6: Get All Users (Admin Only)**
```bash
curl --location 'http://localhost:8080/api/admin/users' \
--header 'Authorization: Bearer '
```

**Expected Response:**
```json
[
    {
        "id": 1,
        "username": "admin",
        "email": "admin@company.com",
        "role": "ADMIN",
        "enabled": true
    },
    {
        "id": 2,
        "username": "FirstUser",
        "email": "firstuser@example.com",
        "role": "USER",
        "enabled": true
    }
    // ... more users
]
```

***

## **Phase 3: User Authentication & Basic Operations**

### **Step 7: Login as Regular User**
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "FirstUser",
    "password": "password123"
}'
```

**Save the user token as USER_TOKEN.**

### **Step 8: Create First Ticket (User)**
```bash
curl --location 'http://localhost:8080/api/tickets' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "subject": "Login Issue",
    "description": "Cannot access my account dashboard",
    "priority": "HIGH"
}'
```

**Expected Response:**
```json
{
    "id": 1,
    "subject": "Login Issue",
    "description": "Cannot access my account dashboard",
    "status": "OPEN",
    "priority": "HIGH",
    "createdByUsername": "FirstUser",
    "assignedToUsername": null,
    "createdAt": "2025-08-16T22:30:00",
    "updatedAt": "2025-08-16T22:30:00"
}
```

### **Step 9: Create Second Ticket (User)**
```bash
curl --location 'http://localhost:8080/api/tickets' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "subject": "Feature Request",
    "description": "Need ability to export reports",
    "priority": "LOW"
}'
```

### **Step 10: Get User's Tickets**
```bash
curl --location 'http://localhost:8080/api/tickets' \
--header 'Authorization: Bearer '
```

**Expected Response:**
```json
[
    {
        "id": 1,
        "subject": "Login Issue",
        "status": "OPEN",
        // ... other fields
    },
    {
        "id": 2,
        "subject": "Feature Request",
        "status": "OPEN",
        // ... other fields
    }
]
```

### **Step 11: Get Specific Ticket**
```bash
curl --location 'http://localhost:8080/api/tickets/1' \
--header 'Authorization: Bearer '
```

***

## **Phase 4: Comment System Testing**

### **Step 12: Add Comment to Ticket**
```bash
curl --location 'http://localhost:8080/api/tickets/1/comments' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "content": "I tried clearing cache but the issue persists"
}'
```

**Expected Response:**
```json
{
    "id": 1,
    "content": "I tried clearing cache but the issue persists",
    "authorUsername": "FirstUser",
    "createdAt": "2025-08-16T22:35:00"
}
```

### **Step 13: Get Comments for Ticket**
```bash
curl --location 'http://localhost:8080/api/tickets/1/comments' \
--header 'Authorization: Bearer '
```

### **Step 14: Update Ticket (User can update their own)**
```bash
curl --location 'http://localhost:8080/api/tickets/1' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "subject": "Login Issue - URGENT",
    "description": "Cannot access my account dashboard - blocking my work",
    "priority": "URGENT"
}'
```

***

## **Phase 5: Support Agent Operations**

### **Step 15: Login as Support Agent**
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "SupportAgent1",
    "password": "agent123"
}'
```

**Save the support agent token as AGENT_TOKEN.**

### **Step 16: Assign Ticket to Support Agent (Admin Only)**
```bash
curl --location 'http://localhost:8080/api/tickets/1/assign' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "supportAgentId": 3
}'
```

**Note: Replace 3 with actual support agent ID from Step 6.**

### **Step 17: Support Agent Views Assigned Tickets**
```bash
curl --location 'http://localhost:8080/api/tickets' \
--header 'Authorization: Bearer '
```

### **Step 18: Support Agent Changes Ticket Status**
```bash
curl --location 'http://localhost:8080/api/tickets/1/status?status=IN_PROGRESS' \
--header 'Authorization: Bearer ' \
--method PUT
```

### **Step 19: Support Agent Adds Comment**
```bash
curl --location 'http://localhost:8080/api/tickets/1/comments' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "content": "I am investigating this issue. Please provide your browser version."
}'
```

### **Step 20: Support Agent Resolves Ticket**
```bash
curl --location 'http://localhost:8080/api/tickets/1/status?status=RESOLVED' \
--header 'Authorization: Bearer ' \
--method PUT
```

***

## **Phase 6: Admin Advanced Operations**

### **Step 21: Admin Views All Tickets**
```bash
curl --location 'http://localhost:8080/api/admin/tickets' \
--header 'Authorization: Bearer '
```

### **Step 22: Admin Force Reassigns Ticket**
```bash
curl --location 'http://localhost:8080/api/admin/tickets/2/reassign' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
    "supportAgentId": 3
}'
```

### **Step 23: Admin Force Closes Ticket**
```bash
curl --location 'http://localhost:8080/api/admin/tickets/1/force-close' \
--header 'Authorization: Bearer ' \
--method PUT
```

### **Step 24: Get Support Agents List**
```bash
curl --location 'http://localhost:8080/api/admin/support-agents' \
--header 'Authorization: Bearer '
```

### **Step 25: Update User Role**
```bash
curl --location 'http://localhost:8080/api/admin/users/2/role?role=SUPPORT_AGENT' \
--header 'Authorization: Bearer ' \
--method PUT
```

***

## **Phase 7: Access Control Testing**

### **Step 26: User Tries to Access Admin Endpoint (Should Fail)**
```bash
curl --location 'http://localhost:8080/api/admin/tickets' \
--header 'Authorization: Bearer '
```

**Expected Response:**
```json
{
    "error": "Access denied"
}
```
**Status Code: 403 Forbidden**

### **Step 27: User Tries to Access Another User's Ticket (Should Fail)**
First, create a second user and their ticket, then try to access it with first user's token.

### **Step 28: Support Agent Tries to Access Unassigned Ticket (Should Fail)**
```bash
curl --location 'http://localhost:8080/api/tickets/2' \
--header 'Authorization: Bearer '
```

### **Step 29: User Tries to Change Ticket Status (Should Fail)**
```bash
curl --location 'http://localhost:8080/api/tickets/1/status?status=CLOSED' \
--header 'Authorization: Bearer ' \
--method PUT
```

**Expected Response:**
```json
{
    "error": "Access denied"
}
```

***

## **Phase 8: Filtering and Search Testing**

### **Step 30: Get Tickets by Status**
```bash
curl --location 'http://localhost:8080/api/tickets?status=OPEN' \
--header 'Authorization: Bearer '
```

### **Step 31: Admin Gets All Tickets by Status**
```bash
curl --location 'http://localhost:8080/api/admin/tickets?status=IN_PROGRESS' \
--header 'Authorization: Bearer '
```

***

## **Phase 9: Logout Testing**

### **Step 32: User Logout**
```bash
curl --location 'http://localhost:8080/api/auth/logout' \
--header 'Authorization: Bearer ' \
--method POST
```

**Expected Response:**
```json
{
    "message": "Logout successful"
}
```

### **Step 33: Try to Use Token After Logout (Should Fail)**
```bash
curl --location 'http://localhost:8080/api/tickets' \
--header 'Authorization: Bearer '
```

**Expected Response:**
```json
{
    "error": "Token has been invalidated"
}
```
**Status Code: 401 Unauthorized**

***

## **Phase 10: Error Testing**

### **Step 34: Invalid Login Credentials**
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "FirstUser",
    "password": "wrongpassword"
}'
```

**Expected Response:**
```json
{
    "message": "Invalid credentials",
    "token": null,
    "role": null
}
```

### **Step 35: Access Protected Endpoint Without Token**
```bash
curl --location 'http://localhost:8080/api/tickets'
```

**Expected Response:**
```json
{
    "error": "Access denied"
}
```

### **Step 36: Register Duplicate Username**
```bash
curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "FirstUser",
    "password": "newpassword",
    "email": "newemail@example.com"
}'
```

**Expected Response:**
```json
{
    "message": "Username already exists",
    "token": null,
    "role": null
}
```

***

## **Verification Checklist**

After running all tests, verify:

- ✅ **User Registration**: Users can register with default USER role
- ✅ **Authentication**: Login returns JWT token
- ✅ **Ticket Creation**: Users can create tickets
- ✅ **Ownership**: Users can only see their own tickets
- ✅ **Comments**: Users can add comments to their tickets
- ✅ **Support Agent**: Can view assigned tickets and change status
- ✅ **Admin Operations**: Admins can view all tickets and manage users
- ✅ **Access Control**: Users cannot access admin endpoints
- ✅ **Status Management**: Proper ticket lifecycle (Open → In Progress → Resolved → Closed)
- ✅ **Assignment**: Admins can assign tickets to support agents
- ✅ **Logout**: Tokens are invalidated after logout
- ✅ **Error Handling**: Proper error responses for invalid operations

## **Quick Test Script**
