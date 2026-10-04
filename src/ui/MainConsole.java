package ui;

import db.DatabaseManager;
import exception.*;
import loan.*;
import model.*;
import service.EligibilityEngine;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainConsole {

    private static EligibilityEngine engine;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SMART LOAN ELIGIBILITY CHECKER (CONSOLE CLI)   ");
        System.out.println("==================================================");

        // Initialize SQLite DB
        DatabaseManager.initializeDatabase();

        // Load Policies
        try {
            engine = new EligibilityEngine("lenders.txt");
            System.out.println("✅ Loaded " + engine.getLenderPolicies().size() + " bank policies from lenders.txt");
        } catch (PolicyLoadException e) {
            System.err.println("❌ Failed to load lender policies: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n---------------- MAIN MENU ----------------");
            System.out.println("1. Check Loan Eligibility");
            System.out.println("2. View Loaded Bank Policies");
            System.out.println("3. View SQLite Application History");
            System.out.println("4. Clear SQLite History");
            System.out.println("5. Exit");
            System.out.print("Select an option (1-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runEligibilityCheck(scanner);
                    break;
                case "2":
                    viewBankPolicies();
                    break;
                case "3":
                    viewDbHistory();
                    break;
                case "4":
                    clearDbHistory();
                    break;
                case "5":
                    running = false;
                    System.out.println("Thank you for using Smart Loan Eligibility Checker!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5.");
            }
        }

        scanner.close();
    }

    private static void runEligibilityCheck(Scanner scanner) {
        try {
            System.out.println("\n--- Enter Applicant Details ---");
            System.out.print("Applicant Name: ");
            String name = scanner.nextLine();

            System.out.print("Age (years): ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Monthly Income (₹): ");
            double income = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Credit Score (300-900): ");
            int creditScore = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Existing Monthly EMI (₹): ");
            double existingEmi = Double.parseDouble(scanner.nextLine().trim());

            Applicant applicant = new Applicant(name, age, income, creditScore, existingEmi);

            System.out.println("\n--- Select Loan Type ---");
            System.out.println("1. Personal Loan");
            System.out.println("2. Home Loan");
            System.out.println("3. Car Loan");
            System.out.println("4. Education Loan");
            System.out.print("Choice (1-4): ");
            String loanTypeChoice = scanner.nextLine().trim();

            System.out.print("Requested Loan Amount (₹): ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Requested Tenure (Months): ");
            int tenure = Integer.parseInt(scanner.nextLine().trim());

            Loan loan;
            switch (loanTypeChoice) {
                case "1": loan = new PersonalLoan(amount, tenure); break;
                case "2": loan = new HomeLoan(amount, tenure); break;
                case "3": loan = new CarLoan(amount, tenure); break;
                case "4": loan = new EducationLoan(amount, tenure); break;
                default:
                    System.out.println("Invalid loan type choice.");
                    return;
            }

            loan.validateLoanSpecifics();

            System.out.println("\n⚡ Launching Multithreaded Bank Eligibility Evaluation...");
            List<EligibilityResult> results = engine.evaluateAllLenders(applicant, loan);

            System.out.println("\n==================================================");
            System.out.println("               EVALUATION RESULTS                 ");
            System.out.println("==================================================");

            EligibilityResult best = engine.getBestRecommendation(results);

            if (best != null) {
                System.out.println("\n🏆 RECOMMENDED LENDER: " + best.getLenderName());
                System.out.printf("   Monthly EMI: ₹%.2f | Interest Rate: %.2f%%\n", best.getEmi(), best.getInterestRate());
            } else {
                System.out.println("\n⚠️ NO LENDER IS CURRENTLY ELIGIBLE FOR THIS APPLICANT.");
            }

            System.out.println("\n--- Detailed Lender Breakdowns ---");
            for (EligibilityResult res : results) {
                System.out.println(res);
                if (!res.isEligible()) {
                    List<String> advice = res.getCounterfactualSuggestion().getActionableAdvice();
                    if (!advice.isEmpty()) {
                        System.out.println("   💡 Smart Suggestions to become eligible for " + res.getLenderName() + ":");
                        for (String adv : advice) {
                            System.out.println("      - " + adv);
                        }
                    }
                }
            }

            System.out.println("\n--- Required Documents (" + loan.getLoanType() + ") ---");
            for (String doc : loan.getRequiredDocuments()) {
                System.out.println("  📄 " + doc);
            }

            // Save to SQLite DB
            String status = (best != null) ? "APPROVED" : "REJECTED";
            String recLender = (best != null) ? best.getLenderName() : "None";
            double bestEmi = (best != null) ? best.getEmi() : 0.0;

            boolean saved = DatabaseManager.saveApplicationRecord(
                    applicant.getName(), applicant.getAge(), applicant.getMonthlyIncome(),
                    applicant.getCreditScore(), applicant.getExistingEmi(), loan.getLoanType(),
                    loan.getAmount(), loan.getTenureMonths(), status, recLender, bestEmi
            );

            if (saved) {
                System.out.println("\n💾 Application record saved to SQLite database (loan_history.db).");
            }

        } catch (NumberFormatException nfe) {
            System.out.println("❌ Invalid numerical input. Please enter valid numbers.");
        } catch (InvalidApplicantException | InvalidLoanException | InvalidCreditScoreException e) {
            System.out.println("❌ Validation Error: " + e.getMessage());
        }
    }

    private static void viewBankPolicies() {
        System.out.println("\n--- Loaded Bank Policies (from lenders.txt) ---");
        for (LenderPolicy p : engine.getLenderPolicies()) {
            System.out.println(" • " + p);
        }
    }

    private static void viewDbHistory() {
        List<Map<String, Object>> history = DatabaseManager.getApplicationHistory();
        System.out.println("\n--- SQLite Application History (" + history.size() + " records) ---");
        if (history.isEmpty()) {
            System.out.println("No application history found.");
            return;
        }

        for (Map<String, Object> r : history) {
            System.out.printf("[%d] %s | %s | Loan: ₹%.0f (%d m) | Result: %s | Rec: %s (EMI: ₹%.2f) | Date: %s\n",
                    r.get("id"), r.get("applicant_name"), r.get("loan_type"),
                    (Double) r.get("loan_amount"), (Integer) r.get("tenure"),
                    r.get("result"), r.get("recommended_lender"),
                    (Double) r.get("emi"), r.get("application_date"));
        }
    }

    private static void clearDbHistory() {
        boolean cleared = DatabaseManager.clearHistory();
        if (cleared) {
            System.out.println("✅ SQLite application history cleared successfully.");
        }
    }
}
