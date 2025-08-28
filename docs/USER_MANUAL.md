# Blood Bank Management System - User Manual

## Table of Contents

1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [User Roles and Permissions](#user-roles-and-permissions)
4. [Authentication](#authentication)
5. [Blood Inventory Management](#blood-inventory-management)
6. [User Management](#user-management)
7. [Donation Management](#donation-management)
8. [Request Management](#request-management)
9. [Desktop Client Usage](#desktop-client-usage)
10. [Troubleshooting](#troubleshooting)
11. [Keyboard Shortcuts](#keyboard-shortcuts)
12. [Best Practices](#best-practices)

## Introduction

The Blood Bank Management System is a comprehensive solution designed to manage blood bank operations efficiently. The system provides both a web-based API and a desktop client application for managing blood inventory, donors, donations, and blood requests.

### Key Features

- **Multi-User Support**: Role-based access control for different user types
- **Blood Inventory Tracking**: Real-time monitoring of blood units and expiry dates
- **Donor Management**: Comprehensive donor information and health screening data
- **Request Processing**: Efficient handling of blood requests from hospitals
- **Security**: JWT-based authentication and role-based permissions
- **Desktop Client**: User-friendly Swing-based interface for daily operations

## Getting Started

### System Requirements

- **Operating System**: Windows 10+, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **Java Runtime**: Java 17 or later
- **Memory**: Minimum 4GB RAM, recommended 8GB+
- **Storage**: 2GB available disk space
- **Network**: Internet connection for API communication

### Installation

#### Desktop Client Installation

1. **Download the Application**
   - Obtain the latest JAR file from your system administrator
   - Ensure you have Java 17+ installed on your system

2. **Run the Application**
   - **Windows**: Double-click the JAR file or run `run-swing-client.bat`
   - **macOS/Linux**: Use `./run-swing-client.sh` or `java -jar blood-bank-management-*.jar`

3. **First Launch**
   - The application will start with a login screen
   - Use the default credentials provided by your administrator
   - Change your password on first login

### Initial Setup

#### First-Time Configuration

1. **Login with Default Credentials**
   ```
   Username: admin
   Password: admin123
   ```

2. **Change Default Password**
   - Navigate to User Profile → Change Password
   - Enter current password and new password
   - Ensure new password meets security requirements

3. **Configure System Settings**
   - Set your timezone and preferred language
   - Configure notification preferences
   - Set default blood unit measurements

## User Roles and Permissions

### Role Overview

The system supports multiple user roles with different levels of access:

#### Administrator (ADMIN)
- **Full System Access**: Can perform all operations
- **User Management**: Create, modify, and deactivate users
- **System Configuration**: Modify system settings and parameters
- **Reports and Analytics**: Access to all system reports
- **Audit Logs**: View system activity and user actions

#### Doctor (DOCTOR)
- **Patient Management**: Create and manage blood requests
- **Medical Decisions**: Approve or reject donation requests
- **Patient History**: Access patient transfusion records
- **Emergency Requests**: Handle urgent blood requirements

#### Nurse (NURSE)
- **Donor Screening**: Conduct health assessments
- **Blood Collection**: Record donation details
- **Inventory Updates**: Update blood unit status
- **Patient Care**: Monitor transfusion processes

#### Technician (TECHNICIAN)
- **Laboratory Work**: Process blood samples
- **Quality Control**: Verify blood unit quality
- **Inventory Management**: Add and update blood units
- **Equipment Maintenance**: Monitor lab equipment status

#### Donor (DONOR)
- **Personal Information**: View and update personal details
- **Donation History**: Access personal donation records
- **Appointment Scheduling**: Book donation appointments
- **Health Updates**: Report health status changes

#### Recipient (RECIPIENT)
- **Request Status**: Check blood request status
- **Medical History**: View transfusion history
- **Appointment Management**: Schedule follow-up appointments

### Permission Matrix

| Feature | Admin | Doctor | Nurse | Technician | Donor | Recipient |
|---------|-------|--------|-------|------------|-------|-----------|
| User Management | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Blood Inventory | ✓ | ✓ | ✓ | ✓ | ✗ | ✗ |
| Donation Records | ✓ | ✓ | ✓ | ✓ | ✓ | ✗ |
| Blood Requests | ✓ | ✓ | ✓ | ✗ | ✗ | ✓ |
| Reports | ✓ | ✓ | ✓ | ✗ | ✗ | ✗ |
| System Settings | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |

## Authentication

### Login Process

1. **Launch Application**
   - Start the desktop client or access the web API
   - Enter your username and password
   - Click "Login" or press Enter

2. **Authentication**
   - System validates credentials against database
   - JWT token generated upon successful authentication
   - Token stored securely for session management

3. **Session Management**
   - Token valid for 24 hours
   - Automatic logout on token expiry
   - Option to extend session if needed

### Password Security

#### Password Requirements
- **Minimum Length**: 8 characters
- **Complexity**: Must include uppercase, lowercase, numbers, and special characters
- **History**: Cannot reuse last 5 passwords
- **Expiry**: Passwords expire every 90 days

#### Password Change Process
1. Navigate to User Profile → Change Password
2. Enter current password for verification
3. Enter new password (twice for confirmation)
4. Submit and confirm change

### Multi-Factor Authentication (Future Enhancement)
- SMS verification codes
- Email confirmation links
- Hardware token support
- Biometric authentication

## Blood Inventory Management

### Inventory Overview

The blood inventory section displays all available blood units with key information:

#### Display Fields
- **Blood Type**: ABO and Rh factor
- **Quantity**: Available amount in ml
- **Status**: Available, Reserved, In Use, Expired, Quarantined
- **Expiry Date**: When the blood unit expires
- **Collection Date**: When the blood was collected
- **Donor ID**: Reference to the donor
- **Batch Number**: Unique identifier for tracking

### Adding Blood Units

#### Manual Entry
1. Navigate to Inventory → Add Blood Unit
2. Fill in required fields:
   - Blood type (required)
   - Quantity in ml (required)
   - Unit of measure (ml, units)
   - Expiry date (required)
   - Collection date (required)
   - Donor ID (required)
   - Batch number (required)
   - Notes (optional)
3. Click "Add" to save

#### Bulk Import
1. Prepare CSV file with required columns
2. Navigate to Inventory → Bulk Import
3. Select file and validate data
4. Confirm import and review results

### Updating Inventory

#### Status Changes
1. Select blood unit from inventory list
2. Click "Edit" or double-click the row
3. Modify status, quantity, or notes
4. Save changes

#### Quantity Adjustments
- **Add Units**: Increase available quantity
- **Remove Units**: Decrease quantity (requires reason)
- **Reserve Units**: Mark as allocated to request
- **Discard Units**: Remove expired or contaminated units

### Expiry Management

#### Expiry Monitoring
- **7-Day Warning**: Units expiring within 7 days highlighted
- **3-Day Alert**: Critical warning for units expiring soon
- **Expired Units**: Automatic status change to "EXPIRED"

#### Expiry Actions
1. **Extend Shelf Life**: If possible and safe
2. **Transfer to Research**: If suitable for medical research
3. **Safe Disposal**: Following medical waste protocols
4. **Documentation**: Record disposal details for audit

### Inventory Reports

#### Available Reports
- **Current Inventory**: Real-time stock levels
- **Expiry Report**: Units expiring soon
- **Blood Type Distribution**: Stock by blood type
- **Utilization Report**: Usage patterns and trends
- **Donor Contribution**: Donor-specific inventory

#### Report Generation
1. Select report type from dropdown
2. Set date range and filters
3. Click "Generate Report"
4. Export to PDF, Excel, or CSV

## User Management

### User Overview

The user management section allows administrators to manage all system users:

#### User Information Display
- **Basic Details**: Name, username, email, phone
- **Role**: User's system role and permissions
- **Blood Type**: User's blood type (if applicable)
- **Status**: Active, Inactive, or Suspended
- **Last Login**: Most recent system access
- **Created Date**: Account creation timestamp

### Creating New Users

#### Registration Process
1. Navigate to Users → Add New User
2. Fill in required information:
   - Username (unique, required)
   - Email (unique, required)
   - Password (meets security requirements)
   - First and Last Name (required)
   - Phone Number (optional)
   - Role (required)
   - Blood Type (if applicable)
3. Click "Create User"

#### User Types
- **Internal Staff**: Doctors, nurses, technicians
- **External Users**: Donors, recipients
- **System Users**: Administrators, support staff

### User Modifications

#### Profile Updates
1. Select user from user list
2. Click "Edit" to modify information
3. Update relevant fields
4. Save changes

#### Role Changes
- **Promotion**: Upgrade user role (requires approval)
- **Demotion**: Reduce user permissions
- **Role Assignment**: Assign multiple roles if needed

#### Account Management
- **Activation**: Enable disabled accounts
- **Deactivation**: Temporarily disable accounts
- **Suspension**: Immediate account suspension
- **Deletion**: Permanent account removal (admin only)

### User Search and Filtering

#### Search Options
- **Username Search**: Find users by username
- **Email Search**: Locate users by email address
- **Role Filter**: Filter users by role
- **Status Filter**: Show active, inactive, or suspended users
- **Blood Type Filter**: Find users by blood type

#### Advanced Filters
- **Date Range**: Users created within specific period
- **Last Login**: Users who haven't logged in recently
- **Location**: Users by geographic region
- **Department**: Users by organizational unit

## Donation Management

### Donation Overview

The donation management section tracks all blood donations:

#### Donation Information
- **Donor Details**: Personal and contact information
- **Donation Date**: When blood was collected
- **Blood Type**: ABO and Rh factor
- **Quantity**: Amount collected in ml
- **Health Screening**: Medical assessment results
- **Status**: Pending, Approved, Rejected, Completed

### Recording Donations

#### New Donation Entry
1. Navigate to Donations → New Donation
2. Enter donor information:
   - Donor ID or search for existing donor
   - Blood type verification
   - Collection date and time
   - Quantity collected
   - Unit of measure

#### Health Screening Data
- **Hemoglobin Level**: Blood iron content (g/dL)
- **Blood Pressure**: Systolic/Diastolic (mmHg)
- **Pulse Rate**: Heart rate (beats per minute)
- **Temperature**: Body temperature (°C/°F)
- **Health Questions**: Medical history review
- **Physical Examination**: Basic health assessment

### Donation Processing

#### Approval Workflow
1. **Initial Screening**: Basic health check
2. **Laboratory Testing**: Blood sample analysis
3. **Quality Assessment**: Blood unit evaluation
4. **Final Approval**: Medical staff review
5. **Inventory Addition**: Add to available stock

#### Rejection Handling
- **Health Issues**: Failed health screening
- **Quality Problems**: Blood unit quality issues
- **Documentation**: Incomplete donor information
- **Regulatory**: Compliance violations

### Donation Tracking

#### Status Updates
- **Pending**: Awaiting processing
- **In Progress**: Under laboratory review
- **Approved**: Ready for inventory
- **Rejected**: Failed quality standards
- **Completed**: Added to inventory

#### Follow-up Actions
- **Donor Notification**: Results communication
- **Appointment Scheduling**: Next donation planning
- **Health Monitoring**: Post-donation follow-up
- **Record Maintenance**: Documentation updates

## Request Management

### Request Overview

The request management section handles blood requests from hospitals:

#### Request Information
- **Requester**: Hospital and doctor details
- **Patient**: Patient identification and medical info
- **Blood Requirements**: Type, quantity, urgency
- **Timeline**: Required delivery date
- **Priority**: Normal, High, or Urgent
- **Status**: Pending, Approved, Fulfilled, Cancelled

### Creating Blood Requests

#### New Request Entry
1. Navigate to Requests → New Request
2. Enter request details:
   - Hospital name and contact information
   - Patient name and identification
   - Blood type and quantity required
   - Required delivery date
   - Priority level
   - Medical reason for request
   - Doctor's name and contact

#### Request Validation
- **Blood Type Compatibility**: Verify patient requirements
- **Quantity Verification**: Check available inventory
- **Timeline Assessment**: Evaluate delivery feasibility
- **Priority Justification**: Document urgency reasons

### Request Processing

#### Approval Workflow
1. **Request Review**: Medical staff assessment
2. **Inventory Check**: Verify blood availability
3. **Resource Allocation**: Reserve required units
4. **Approval Decision**: Grant or deny request
5. **Notification**: Inform requester of decision

#### Fulfillment Process
1. **Blood Unit Selection**: Choose appropriate units
2. **Quality Verification**: Final quality check
3. **Packaging**: Prepare for transport
4. **Delivery Coordination**: Arrange transportation
5. **Documentation**: Record fulfillment details

### Request Tracking

#### Status Monitoring
- **Pending**: Awaiting review and approval
- **Under Review**: Medical staff assessment
- **Approved**: Request approved, units allocated
- **In Transit**: Blood units being delivered
- **Fulfilled**: Request completed successfully
- **Cancelled**: Request cancelled or denied

#### Communication
- **Status Updates**: Regular progress notifications
- **Issue Resolution**: Address problems promptly
- **Delivery Confirmation**: Verify successful delivery
- **Feedback Collection**: Gather requester satisfaction

## Desktop Client Usage

### Interface Overview

The desktop client provides a tabbed interface for easy navigation:

#### Main Tabs
- **Login**: User authentication
- **Dashboard**: System overview and quick actions
- **Inventory**: Blood inventory management
- **Users**: User management and administration
- **Donations**: Donation tracking and processing
- **Requests**: Blood request management
- **Reports**: System reports and analytics

### Navigation and Controls

#### Menu Bar
- **File**: Application settings and exit
- **Edit**: Copy, paste, and search functions
- **View**: Interface customization options
- **Help**: User manual and support information

#### Toolbar
- **Quick Actions**: Common operations buttons
- **Search**: Global search functionality
- **Refresh**: Update data from server
- **Export**: Data export options

### Data Entry and Editing

#### Table Operations
- **Sorting**: Click column headers to sort
- **Filtering**: Use filter rows for data search
- **Selection**: Click rows to select items
- **Editing**: Double-click cells for inline editing

#### Form Operations
- **Input Validation**: Real-time field validation
- **Auto-completion**: Smart field suggestions
- **Required Fields**: Clear indication of mandatory data
- **Error Messages**: Helpful validation feedback

### Keyboard Shortcuts

#### Navigation Shortcuts
- **Tab**: Move between fields
- **Shift+Tab**: Move backward between fields
- **Enter**: Submit forms or confirm actions
- **Escape**: Cancel operations or close dialogs

#### Data Management
- **Ctrl+C**: Copy selected data
- **Ctrl+V**: Paste data
- **Ctrl+F**: Find/search functionality
- **Ctrl+R**: Refresh data
- **F5**: Refresh current view

#### Application Control
- **Ctrl+Q**: Quick exit
- **Ctrl+S**: Save current data
- **Ctrl+Z**: Undo last action
- **F1**: Help documentation

## Troubleshooting

### Common Issues

#### Login Problems
**Issue**: Cannot log in with correct credentials
**Solutions**:
1. Check username and password spelling
2. Verify account is active and not locked
3. Clear application cache and restart
4. Contact system administrator

**Issue**: "Session expired" error
**Solutions**:
1. Re-login with credentials
2. Check system clock synchronization
3. Clear browser cookies (web version)
4. Restart desktop client

#### Data Loading Issues
**Issue**: Tables not loading data
**Solutions**:
1. Check internet connection
2. Verify server is running
3. Refresh data manually
4. Check user permissions

**Issue**: Slow data loading
**Solutions**:
1. Check network connection speed
2. Reduce data filter complexity
3. Contact IT support for server issues
4. Use pagination for large datasets

#### Application Crashes
**Issue**: Application stops responding
**Solutions**:
1. Force close and restart
2. Check system memory usage
3. Update Java runtime
4. Reinstall application

**Issue**: Error messages during operation
**Solutions**:
1. Note error message details
2. Check system logs
3. Contact support with error details
4. Try alternative operation method

### Performance Optimization

#### System Recommendations
- **Memory**: Ensure adequate RAM (8GB+ recommended)
- **Storage**: Use SSD for better performance
- **Network**: Stable internet connection
- **Java**: Latest stable Java 17+ version

#### Application Settings
- **Data Refresh**: Adjust auto-refresh intervals
- **Cache Size**: Optimize local data caching
- **Connection Timeout**: Set appropriate timeout values
- **Batch Operations**: Use bulk operations for large datasets

### Error Reporting

#### When to Report Issues
- **System Crashes**: Application stops working
- **Data Loss**: Information not saved or corrupted
- **Security Issues**: Unauthorized access attempts
- **Performance Problems**: Slow response times
- **Feature Requests**: Missing functionality

#### How to Report Issues
1. **Document the Problem**:
   - Describe what happened
   - Note error messages
   - Record steps to reproduce
   - Include system information

2. **Contact Support**:
   - Email: support@bloodbank.com
   - Phone: Support hotline
   - Online: Support portal
   - In-app: Help → Report Issue

3. **Provide Details**:
   - User account information
   - Time and date of issue
   - Screenshots if possible
   - System logs if available

## Best Practices

### Data Entry Guidelines

#### Accuracy and Completeness
- **Verify Information**: Double-check all entered data
- **Required Fields**: Complete all mandatory information
- **Data Validation**: Use system validation features
- **Consistency**: Follow established naming conventions

#### Data Quality
- **Regular Updates**: Keep information current
- **Error Correction**: Fix data errors promptly
- **Duplicate Prevention**: Avoid creating duplicate records
- **Audit Trail**: Maintain change history

### Security Best Practices

#### Password Management
- **Strong Passwords**: Use complex, unique passwords
- **Regular Changes**: Update passwords periodically
- **No Sharing**: Never share login credentials
- **Secure Storage**: Don't write down passwords

#### Access Control
- **Role-Based Access**: Use appropriate user roles
- **Least Privilege**: Grant minimum necessary permissions
- **Regular Review**: Periodically review user access
- **Immediate Revocation**: Remove access for terminated users

### Operational Efficiency

#### Workflow Optimization
- **Standard Procedures**: Follow established processes
- **Batch Operations**: Group similar tasks together
- **Keyboard Shortcuts**: Learn and use shortcuts
- **Templates**: Use pre-filled forms when possible

#### Time Management
- **Priority Setting**: Handle urgent requests first
- **Regular Updates**: Update status information promptly
- **Communication**: Keep stakeholders informed
- **Documentation**: Record actions and decisions

### Compliance and Regulations

#### Medical Standards
- **Blood Safety**: Follow blood safety protocols
- **Quality Control**: Maintain quality standards
- **Documentation**: Complete all required records
- **Training**: Stay current with medical procedures

#### Regulatory Requirements
- **Data Privacy**: Protect patient confidentiality
- **Audit Trails**: Maintain complete audit records
- **Reporting**: Submit required reports on time
- **Inspections**: Prepare for regulatory inspections

## Conclusion

This user manual provides comprehensive guidance for using the Blood Bank Management System. Regular training and practice will help users become proficient with all system features.

### Additional Resources

- **Online Help**: Built-in help system
- **Training Materials**: Video tutorials and guides
- **Support Team**: Technical support contacts
- **User Community**: User forums and discussions

### Feedback and Improvement

The system is continuously improved based on user feedback. Please share your suggestions and report any issues to help make the system better for everyone.

---

**Version**: 1.0  
**Last Updated**: January 2024  
**Contact**: support@bloodbank.com
