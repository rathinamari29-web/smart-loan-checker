package service;

import exception.PolicyLoadException;
import model.LenderPolicy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolicyLoader {

    public static List<LenderPolicy> loadPolicies(String filePath) throws PolicyLoadException {
        List<LenderPolicy> policies = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new PolicyLoadException("Lender policy file not found at: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length < 9) {
                    System.err.println("Warning: Skipping malformed line " + lineNumber + " in " + filePath);
                    continue;
                }

                try {
                    String lenderName = parts[0].trim();
                    int minAge = Integer.parseInt(parts[1].trim());
                    int maxAge = Integer.parseInt(parts[2].trim());
                    int minCreditScore = Integer.parseInt(parts[3].trim());
                    double minMonthlyIncome = Double.parseDouble(parts[4].trim());
                    double maxEmiToIncomeRatio = Double.parseDouble(parts[5].trim());
                    double interestRate = Double.parseDouble(parts[6].trim());
                    int maxTenureMonths = Integer.parseInt(parts[7].trim());
                    double maxLoanAmount = Double.parseDouble(parts[8].trim());

                    LenderPolicy policy = new LenderPolicy(
                            lenderName, minAge, maxAge, minCreditScore, 
                            minMonthlyIncome, maxEmiToIncomeRatio, 
                            interestRate, maxTenureMonths, maxLoanAmount
                    );
                    policies.add(policy);
                } catch (NumberFormatException nfe) {
                    System.err.println("Warning: Invalid number format on line " + lineNumber + ": " + nfe.getMessage());
                }
            }
        } catch (IOException e) {
            throw new PolicyLoadException("Error reading lender policy file: " + e.getMessage(), e);
        }

        if (policies.isEmpty()) {
            throw new PolicyLoadException("No valid lender policies loaded from " + filePath);
        }

        return policies;
    }
}
