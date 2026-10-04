package service;

import model.Applicant;
import model.CounterfactualSuggestion;
import model.LenderPolicy;
import loan.Loan;

public class CounterfactualEngine {

    public static CounterfactualSuggestion generateSuggestions(Applicant applicant, Loan loan, LenderPolicy policy, 
                                                                 double calculatedEmi, boolean emiRatioExceeded, 
                                                                 boolean scoreDeficit, boolean incomeDeficit, boolean amountExceeded) {
        
        CounterfactualSuggestion suggestion = new CounterfactualSuggestion();
        
        double monthlyIncome = applicant.getMonthlyIncome();
        double existingEmi = applicant.getExistingEmi();
        double maxAllowedTotalEmi = monthlyIncome * policy.getMaxEmiToIncomeRatio();
        double maxAllowedNewEmi = maxAllowedTotalEmi - existingEmi;

        // 1. Credit Score Suggestion
        if (scoreDeficit) {
            int scoreGap = policy.getMinCreditScore() - applicant.getCreditScore();
            suggestion.setRequiredCreditScoreBoost(scoreGap);
            suggestion.addAdvice(String.format("Improve Credit Score by %d points (Minimum required by %s is %d, your current score is %d).",
                    scoreGap, policy.getLenderName(), policy.getMinCreditScore(), applicant.getCreditScore()));
        }

        // 2. Income Deficit Suggestion
        if (incomeDeficit) {
            double incomeGap = policy.getMinMonthlyIncome() - monthlyIncome;
            suggestion.setRequiredIncomeDeficit(incomeGap);
            suggestion.addAdvice(String.format("Increase Monthly Income by ₹%.2f (Minimum required is ₹%.2f, current: ₹%.2f).",
                    incomeGap, policy.getMinMonthlyIncome(), monthlyIncome));
        }

        // 3. Existing EMI / EMI Ratio / Loan Amount Counterfactual Math
        if (emiRatioExceeded || amountExceeded) {
            if (maxAllowedNewEmi <= 0) {
                double emiReductionNeeded = existingEmi - maxAllowedTotalEmi;
                suggestion.setMaxAllowedExistingEmi(maxAllowedTotalEmi);
                suggestion.addAdvice(String.format("Your existing EMI (₹%.2f) exceeds %s's maximum allowed total EMI limit (₹%.2f, which is %.0f%% of your income). Reduce existing debt by at least ₹%.2f to become eligible for new loans.",
                        existingEmi, policy.getLenderName(), maxAllowedTotalEmi, policy.getMaxEmiToIncomeRatio() * 100, emiReductionNeeded));
            } else {
                // Calculate max eligible principal for current requested tenure
                double maxLoanForCurrentTenure = EMIComputer.computeMaxPrincipal(
                        maxAllowedNewEmi, policy.getInterestRate(), loan.getTenureMonths()
                );
                // Cap at lender's max loan limit
                if (maxLoanForCurrentTenure > policy.getMaxLoanAmount()) {
                    maxLoanForCurrentTenure = policy.getMaxLoanAmount();
                }

                suggestion.setMaxEligibleLoanAmount(maxLoanForCurrentTenure);
                if (maxLoanForCurrentTenure > 0 && maxLoanForCurrentTenure < loan.getAmount()) {
                    suggestion.addAdvice(String.format("Reduce requested loan amount from ₹%.2f to approximately ₹%.2f (for %d months tenure at %.2f%% p.a.).",
                            loan.getAmount(), maxLoanForCurrentTenure, loan.getTenureMonths(), policy.getInterestRate()));
                }

                // Check if increasing tenure helps
                int maxPolicyTenure = policy.getMaxTenureMonths();
                if (loan.getTenureMonths() < maxPolicyTenure) {
                    double emiWithMaxTenure = EMIComputer.computeEMI(loan.getAmount(), policy.getInterestRate(), maxPolicyTenure);
                    double totalEmiWithMaxTenure = existingEmi + emiWithMaxTenure;
                    double emiRatioWithMaxTenure = totalEmiWithMaxTenure / monthlyIncome;

                    if (emiRatioWithMaxTenure <= policy.getMaxEmiToIncomeRatio()) {
                        suggestion.setSuggestedTenureMonths(maxPolicyTenure);
                        suggestion.addAdvice(String.format("Increase loan tenure from %d months to %d months (this reduces monthly EMI to ₹%.2f, fitting your income ratio).",
                                loan.getTenureMonths(), maxPolicyTenure, emiWithMaxTenure));
                    }
                }
            }
        }

        return suggestion;
    }
}
