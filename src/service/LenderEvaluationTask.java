package service;

import model.Applicant;
import model.EligibilityResult;
import model.LenderPolicy;
import model.CounterfactualSuggestion;
import loan.Loan;

import java.util.concurrent.Callable;

public class LenderEvaluationTask implements Callable<EligibilityResult> {

    private final Applicant applicant;
    private final Loan loan;
    private final LenderPolicy policy;

    public LenderEvaluationTask(Applicant applicant, Loan loan, LenderPolicy policy) {
        this.applicant = applicant;
        this.loan = loan;
        this.policy = policy;
    }

    @Override
    public EligibilityResult call() throws Exception {
        // Evaluate Risk-Based Dynamic Pricing
        RiskAssessmentEngine.RiskProfile risk = RiskAssessmentEngine.evaluateRisk(applicant, loan);
        double adjustedRate = Math.max(5.0, policy.getInterestRate() + risk.getInterestAdjustment());

        System.out.printf("[Thread: %s] Evaluating %s | Base Rate: %.2f%% | Adjusted Rate: %.2f%% (%s)\n", 
                Thread.currentThread().getName(), policy.getLenderName(), policy.getInterestRate(), adjustedRate, risk.getRiskGrade());

        // Simulate micro latency to demonstrate multithreaded evaluation
        Thread.sleep(40);

        EligibilityResult result = new EligibilityResult(policy.getLenderName(), adjustedRate);
        result.setRequiredDocuments(loan.getRequiredDocuments());

        boolean ageDeficit = false;
        boolean scoreDeficit = false;
        boolean incomeDeficit = false;
        boolean emiRatioExceeded = false;
        boolean amountExceeded = false;
        boolean tenureExceeded = false;

        // 1. Age Check
        if (applicant.getAge() < policy.getMinAge() || applicant.getAge() > policy.getMaxAge()) {
            ageDeficit = true;
            result.addRejectionReason(String.format("Applicant age (%d) outside lender limits (%d - %d years)", 
                    applicant.getAge(), policy.getMinAge(), policy.getMaxAge()));
        }

        // 2. Credit Score Check
        if (applicant.getCreditScore() < policy.getMinCreditScore()) {
            scoreDeficit = true;
            result.addRejectionReason(String.format("Credit score (%d) below lender minimum requirement (%d)", 
                    applicant.getCreditScore(), policy.getMinCreditScore()));
        }

        // 3. Monthly Income Check
        if (applicant.getMonthlyIncome() < policy.getMinMonthlyIncome()) {
            incomeDeficit = true;
            result.addRejectionReason(String.format("Monthly income (₹%.2f) below lender minimum requirement (₹%.2f)", 
                    applicant.getMonthlyIncome(), policy.getMinMonthlyIncome()));
        }

        // 4. Maximum Loan Amount Check
        if (loan.getAmount() > policy.getMaxLoanAmount()) {
            amountExceeded = true;
            result.addRejectionReason(String.format("Requested amount (₹%.2f) exceeds lender limit (₹%.2f)", 
                    loan.getAmount(), policy.getMaxLoanAmount()));
        }

        // 5. Maximum Tenure Check
        if (loan.getTenureMonths() > policy.getMaxTenureMonths()) {
            tenureExceeded = true;
            result.addRejectionReason(String.format("Requested tenure (%d months) exceeds lender max allowed (%d months)", 
                    loan.getTenureMonths(), policy.getMaxTenureMonths()));
        }

        // 6. EMI & EMI-to-Income Ratio Check with Risk-Adjusted Interest Rate
        double newEmi = loan.calculateEMI(adjustedRate);
        result.setEmi(newEmi);

        double totalEmi = applicant.getExistingEmi() + newEmi;
        double ratio = totalEmi / applicant.getMonthlyIncome();
        result.setEmiToIncomeRatio(ratio);

        if (ratio > policy.getMaxEmiToIncomeRatio()) {
            emiRatioExceeded = true;
            result.addRejectionReason(String.format("Total EMI ratio (%.1f%%) exceeds lender maximum cap (%.0f%%)", 
                    ratio * 100, policy.getMaxEmiToIncomeRatio() * 100));
        }

        // Generate counterfactual suggestions if rejected
        if (!result.isEligible()) {
            CounterfactualSuggestion suggestion = CounterfactualEngine.generateSuggestions(
                    applicant, loan, policy, newEmi, emiRatioExceeded, scoreDeficit, incomeDeficit, amountExceeded
            );
            result.setCounterfactualSuggestion(suggestion);
        }

        return result;
    }
}
