package service;

import model.Applicant;
import loan.Loan;

public class RiskAssessmentEngine {

    public static class RiskProfile {
        private final double riskScore; // 0 to 100 (0 = Lowest Risk, 100 = Highest Risk)
        private final String riskGrade; // A+, A, B, C, D, F
        private final double interestAdjustment; // e.g. -0.50% or +0.75%
        private final String riskSummary;

        public RiskProfile(double riskScore, String riskGrade, double interestAdjustment, String riskSummary) {
            this.riskScore = riskScore;
            this.riskGrade = riskGrade;
            this.interestAdjustment = interestAdjustment;
            this.riskSummary = riskSummary;
        }

        public double getRiskScore() { return riskScore; }
        public String getRiskGrade() { return riskGrade; }
        public double getInterestAdjustment() { return interestAdjustment; }
        public String getRiskSummary() { return riskSummary; }
    }

    public static RiskProfile evaluateRisk(Applicant applicant, Loan loan) {
        double scoreComponent = 0;
        int cs = applicant.getCreditScore();

        // 1. Credit Score Risk Component (Max 45 points)
        if (cs >= 800) scoreComponent = 0;
        else if (cs >= 750) scoreComponent = 8;
        else if (cs >= 700) scoreComponent = 18;
        else if (cs >= 650) scoreComponent = 30;
        else scoreComponent = 45;

        // 2. Existing Debt-to-Income Risk Component (Max 35 points)
        double dti = applicant.getExistingEmi() / applicant.getMonthlyIncome();
        double dtiComponent = Math.min(35, dti * 70);

        // 3. Age Buffer Risk Component (Max 20 points)
        double ageComponent = 0;
        int endAge = applicant.getAge() + (loan.getTenureMonths() / 12);
        if (endAge > 60) {
            ageComponent = Math.min(20, (endAge - 60) * 2.5);
        }

        double totalRiskScore = Math.min(100, scoreComponent + dtiComponent + ageComponent);

        // Determine Grade & Rate Adjustment
        String grade;
        double adj;
        if (totalRiskScore <= 15) {
            grade = "A+ (Excellent)";
            adj = -0.50; // 0.5% Interest Discount for Prime Applicants
        } else if (totalRiskScore <= 30) {
            grade = "A (Low Risk)";
            adj = -0.25;
        } else if (totalRiskScore <= 50) {
            grade = "B (Moderate Risk)";
            adj = 0.00;
        } else if (totalRiskScore <= 70) {
            grade = "C (Elevated Risk)";
            adj = 0.50; // 0.5% Risk Surcharge
        } else if (totalRiskScore <= 85) {
            grade = "D (High Risk)";
            adj = 1.00; // 1.0% Risk Surcharge
        } else {
            grade = "F (Severe Risk)";
            adj = 1.50;
        }

        String summary = String.format("Debt Stress: %.1f%% | Risk Index: %.0f/100 | Rate Adjustment: %+.2f%%",
                dti * 100, totalRiskScore, adj);

        return new RiskProfile(totalRiskScore, grade, adj, summary);
    }
}
