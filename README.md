# MyContactsAppDemo
MyContacts App is a Java-based, console-driven application implemented use-case wise to demonstrate object-oriented design, design patterns, and core Java concepts through a contact management system.

## Features

This application consolidates multiple use cases (UC1 to UC10) into a single, cohesive user experience:

1.  **User Management**: Registration and Authentication (UC1, UC2).
2.  **Profile**: Manage personal details, passwords, and preferences (UC3).
3.  **Contacts**:
    -   Create and Manage Person & Organization contacts (UC4).
    -   View details with Decorator enhancements (UpperCase, Masked Email) (UC5).
    -   Edit contacts with Undo/Redo capabilities (Command Pattern) (UC6).
    -   Delete contacts (Soft & Hard Delete) (UC7).
4.  **Groups**: Create and manage contact groups with bulk operations (Composite Pattern) (UC8).
5.  **Search**: Advanced search using Specification Pattern (Name, Phone, Email, Tag, etc.) (UC9).
6.  **Filter & Sort**: Advanced filtering and sorting options (Strategy Pattern) (UC10).
7.  **Admin Features**: User oversight and global search capabilities.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 8 or higher.
-   A terminal or command prompt.

### Compilation

Navigate to the source directory and compile the Java files:

```bash
cd src/main/java
javac com/apps/mycontactsapp/MyContactsAppMain.java
```

### Running the Application

Execute the main class to start the application:

```bash
java com.apps.mycontactsapp.MyContactsAppMain
```

### Navigation

-   Follow the on-screen prompts to navigate through the menus.
-   Enter the number corresponding to your choice.
-   Input is validated to ensure robustness.

## Architecture

The project follows a modular structure:
-   **Model**: Domain entities (User, Contact, etc.).
-   **Service**: Business logic and operations.
-   **Repository**: Data abstraction layer.
-   **Patterns Used**: Singleton, Factory, Builder, Strategy, Observer, Decorator, Command, Composite, Specification.