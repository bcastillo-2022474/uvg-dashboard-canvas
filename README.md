# Canvas Dashboard

A Canvas Dashboard Redesign built with Spring Boot and modern web technologies.

## Prerequisites

You need:
1. **Java 21** - Download from [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21) and verify with `java --version`
2. **Maven** - Build tool (see installation below)

## Maven Installation

### Windows

**Option A: Using Chocolatey (Recommended)**
```bash
# Install Chocolatey first (if you don't have it)
# Run PowerShell as Administrator and execute:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven
```

**Option B: Manual Installation**
1. Download Maven from [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Add `C:\Program Files\Apache\maven\bin` to your PATH environment variable
4. Verify: `mvn --version`

### macOS

**Using Homebrew (Recommended)**
```bash
# Install Homebrew first (if you don't have it)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Maven
brew install maven
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install maven
```

### Linux (CentOS/RHEL/Fedora)
```bash
# Fedora
sudo dnf install maven

# CentOS/RHEL
sudo yum install maven
```

### Verify Maven Installation
```bash
mvn --version
```

## Running the Application

1. **Clone the project**
```bash
git clone <your-repository-url>
cd canvas-dashboard
```

2. **Run the application**
```bash
mvn spring-boot:run
```

3. **Access the application**
   - Open your browser
   - Go to: `http://localhost:8080`

## Alternative: Using Maven Wrapper (No Maven Installation Required)

If you don't want to install Maven globally, you can use the included Maven wrapper:

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**macOS/Linux:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

## Troubleshooting

**"mvn: command not found"**
- Maven is not installed or not in your PATH
- Try the Maven wrapper instead: `./mvnw` (macOS/Linux) or `mvnw.cmd` (Windows)

**"java: command not found"**
- Install Java 21 and ensure it's in your PATH

**Port 8080 already in use**
- Stop other applications using port 8080, or change the port in `src/main/resources/application.properties`

**Permission denied (macOS/Linux)**
- Run: `chmod +x mvnw`

## Project Structure

```
canvas-dashboard/
├── src/main/java/          # Java source code
├── src/main/resources/     # Configuration files
├── src/test/               # Test files
├── mvnw / mvnw.cmd         # Maven wrapper
├── pom.xml                 # Maven configuration
└── README.md               # This file
```

## Development Stack

- **Spring Boot 3.5.5** - Main framework
- **Spring Security** - Authentication
- **Thymeleaf** - Template engine
- **HTMX** - Modern web interactions
- **Maven** - Build tool

Happy coding!