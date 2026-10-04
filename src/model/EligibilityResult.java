package model;

import java.util.ArrayList;
import java.util.List;

public class EligibilityResult {
    private String lenderName;
    private boolean eligible;
    private double emi;
    private double interestRate;
    private double emiToIncomeRatio;
    private List<String> rejectionReasons;
    private CounterfactualSuggestion counterfactualSuggestion;
    private List<String> requiredDocuments;

    public EligibilityResult(String lenderName, double interestRate) {
        this.lenderName = lenderName;
        this.interestRate = interestRate;
        this.eligible = true;
        this.rejectionReasons = new ArrayList<>();
        this.requiredDocuments = new ArrayList<>();
        this.counterfactualSuggestion = new CounterfactualSuggestion();
    }

    public String getLenderName() { return lenderName; }
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public double getEmi() { return emi; }
    public void setEmi(double emi) { this.emi = emi; }

    public double getInterestRate() { return interestRate; }

    public double getEmiToIncomeRatio() { return emiToIncomeRatio; }
    public void setEmiToIncomeRatio(double emiToIncomeRatio) { this.emiToIncomeRatio = emiToIncomeRatio; }

    public List<String> getRejectionReasons() { return rejectionReasons; }
    public void addRejectionReason(String reason) {
        this.rejectionReasons.add(reason);
        this.eligible = false;
    }

    public CounterfactualSuggestion getCounterfactualSuggestion() { return counterfactualSuggestion; }
    public void setCounterfactualSuggestion(CounterfactualSuggestion counterfactualSuggestion) {
        this.counterfactualSuggestion = counterfactualSuggestion;
    }

    public List<String> getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(List<String> requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    @Override
    public String toString() {
        if (eligible) {
            return String.format("✅ %s: ELIGIBLE | EMI: ₹%.2f (Interest: %.2f%%, Ratio: %.1f%%)",
                    lenderName, emi, interestRate, emiToIncomeRatio * 100);
        } else {
            return String.format("❌ %s: REJECTED | Reasons: %s", lenderName, String.join("; ", rejectionReasons));
        }
    }
}
