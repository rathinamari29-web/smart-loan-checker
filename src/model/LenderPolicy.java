package model;

public class LenderPolicy {
    private String lenderName;
    private int minAge;
    private int maxAge;
    private int minCreditScore;
    private double minMonthlyIncome;
    private double maxEmiToIncomeRatio;
    private double interestRate; // Annual %
    private int maxTenureMonths;
    private double maxLoanAmount;

    public LenderPolicy(String lenderName, int minAge, int maxAge, int minCreditScore, 
                        double minMonthlyIncome, double maxEmiToIncomeRatio, 
                        double interestRate, int maxTenureMonths, double maxLoanAmount) {
        this.lenderName = lenderName;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.minCreditScore = minCreditScore;
        this.minMonthlyIncome = minMonthlyIncome;
        this.maxEmiToIncomeRatio = maxEmiToIncomeRatio;
        this.interestRate = interestRate;
        this.maxTenureMonths = maxTenureMonths;
        this.maxLoanAmount = maxLoanAmount;
    }

    public String getLenderName() { return lenderName; }
    public int getMinAge() { return minAge; }
    public int getMaxAge() { return maxAge; }
    public int getMinCreditScore() { return minCreditScore; }
    public double getMinMonthlyIncome() { return minMonthlyIncome; }
    public double getMaxEmiToIncomeRatio() { return maxEmiToIncomeRatio; }
    public double getInterestRate() { return interestRate; }
    public int getMaxTenureMonths() { return maxTenureMonths; }
    public double getMaxLoanAmount() { return maxLoanAmount; }

    @Override
    public String toString() {
        return String.format("%s [Interest: %.2f%%, MinScore: %d, MinInc: ₹%.0f, MaxEmiRatio: %.0f%%]",
                lenderName, interestRate, minCreditScore, minMonthlyIncome, maxEmiToIncomeRatio * 100);
    }
}
