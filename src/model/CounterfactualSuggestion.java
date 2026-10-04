package model;

import java.util.ArrayList;
import java.util.List;

public class CounterfactualSuggestion {
    private double maxEligibleLoanAmount;
    private int suggestedTenureMonths;
    private double maxAllowedExistingEmi;
    private int requiredCreditScoreBoost;
    private double requiredIncomeDeficit;
    private List<String> actionableAdvice;

    public CounterfactualSuggestion() {
        this.actionableAdvice = new ArrayList<>();
    }

    public double getMaxEligibleLoanAmount() { return maxEligibleLoanAmount; }
    public void setMaxEligibleLoanAmount(double maxEligibleLoanAmount) { this.maxEligibleLoanAmount = maxEligibleLoanAmount; }

    public int getSuggestedTenureMonths() { return suggestedTenureMonths; }
    public void setSuggestedTenureMonths(int suggestedTenureMonths) { this.suggestedTenureMonths = suggestedTenureMonths; }

    public double getMaxAllowedExistingEmi() { return maxAllowedExistingEmi; }
    public void setMaxAllowedExistingEmi(double maxAllowedExistingEmi) { this.maxAllowedExistingEmi = maxAllowedExistingEmi; }

    public int getRequiredCreditScoreBoost() { return requiredCreditScoreBoost; }
    public void setRequiredCreditScoreBoost(int requiredCreditScoreBoost) { this.requiredCreditScoreBoost = requiredCreditScoreBoost; }

    public double getRequiredIncomeDeficit() { return requiredIncomeDeficit; }
    public void setRequiredIncomeDeficit(double requiredIncomeDeficit) { this.requiredIncomeDeficit = requiredIncomeDeficit; }

    public List<String> getActionableAdvice() { return actionableAdvice; }
    public void addAdvice(String advice) { this.actionableAdvice.add(advice); }
}
