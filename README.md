# 🏦 Smart Loan Eligibility Checker

A multi-interface Java application designed to evaluate loan applicants across multiple banking policies using parallel multithreading, real-time risk assessment, SQLite database persistence, and explainable loan suggestions.

---

## 📌 Project Overview
The **Smart Loan Eligibility Checker** allows applicants to calculate loan eligibility for various loan products (Personal, Home, Car, and Education loans). The system reads bank rules dynamically from a policy file (`lenders.txt`), evaluates rules concurrently across multiple threads, stores application records in an SQLite database, and generates downloadable digital pre-approval certificates.

It offers three distinct user interfaces:
1. **Swing Desktop GUI**: Rich graphical interface built with Java Swing.
2. **Console CLI**: Interactive terminal interface for command-line users.
3. **Mobile Progressive Web App (PWA)**: Web UI optimized for mobile phones and local Wi-Fi networks.

---

## ✨ Key Features
- **⚡ Multithreaded Evaluation**: Evaluates applicant credentials across multiple bank policies concurrently using Java `ExecutorService`.
- **📊 Real-time Risk Assessment**: Dynamically computes FOIR (Fixed-Income-to-Obligation Ratio), DTI (Debt-to-Income), and risk-adjusted interest rates.
- **💡 Smart Suggestions Engine**: Provides actionable counterfactual suggestions for ineligible applicants (e.g., required debt reduction or income adjustments).
- **📄 Digital Pre-Approval Certificates**: Generates official pre-approval certificates with unique verification hash codes.
- **💾 SQLite Persistence**: Automatically logs application records, statuses, and recommended bank offers to `loan_history.db`.
- **🌐 Mobile PWA Support**: Mobile web UI that can be served over local Wi-Fi and installed to mobile home screens.

---

## 🛠️ Tech Stack & Architecture
- **Language**: Java 17+
- **GUI Framework**: Java Swing (FlatLaf / Modern UI)
- **Database**: SQLite (`sqlite-jdbc.jar`)
- **Concurrency**: `java.util.concurrent` (`ExecutorService`, `Future`, `Callable`)
- **Frontend / Mobile**: HTML5, Vanilla CSS3, JavaScript (ServiceWorker, PWA)

---

## 🚀 How to Run

### 1. Run Swing Desktop GUI
```powershell
.\run_gui.ps1
```

### 2. Run Console CLI
```powershell
.\run_console.ps1
```

### 3. Launch Mobile App in Browser
```powershell
.\run_mobile.ps1
```

### 4. Host Mobile App on Local Wi-Fi Network
```powershell
.\host_mobile.ps1
```

---

## 📂 Project Structure
```text
├── src/
│   ├── db/              # SQLite Database Operations
│   ├── exception/       # Custom Exception Classes
│   ├── loan/            # Loan Product Classes (Personal, Home, Car, Education)
│   ├── model/           # Data Models (Applicant, LenderPolicy, EligibilityResult)
│   ├── service/         # Business Engines (EligibilityEngine, RiskAssessment, Counterfactuals)
│   └── ui/              # User Interfaces (MainGUI, MainConsole)
├── mobile/              # Mobile PWA Frontend (HTML, CSS, JS, Manifest, SW)
├── lib/                 # Dependencies (sqlite-jdbc.jar)
├── lenders.txt          # Dynamic Bank Policy Configuration File
├── compile.ps1          # Compilation Script
├── run_gui.ps1          # GUI Launcher
├── run_console.ps1      # CLI Launcher
├── run_mobile.ps1       # Desktop Mobile Browser Launcher
└── host_mobile.ps1      # Local Wi-Fi Network Server Launcher
```
