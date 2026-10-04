package loan;

import exception.InvalidLoanException;
import java.util.List;

public abstract class Loan implements LoanCalculator {
    protected double amount;
    protected int tenureMonths;

    public Loan(double amount, int tenureMonths) throws InvalidLoanException {
        if (amount <= 0) {
            throw new InvalidLoanException("Loan amount must be greater than zero.");
        }
        if (tenureMonths <= 0 || tenureMonths > 360) {
            throw new InvalidLoanException("Tenure must be between 1 and 360 months.");
        }
        this.amount = amount;
        this.tenureMonths = tenureMonths;
    }

    public double getAmount() { return amount; }
    public int getTenureMonths() { return tenureMonths; }

    @Override
    public double calculateEMI(double annualInterestRate) {
        if (annualInterestRate <= 0) {
            return amount / tenureMonths; // Zero-interest fallback
        }
        double r = (annualInterestRate / 12) / 100;
        double emi = (amount * r * Math.pow(1 + r, tenureMonths)) / (Math.pow(1 + r, tenureMonths) - 1);
        return emi;
    }

    public abstract String getLoanType();
    public abstract List<String> getRequiredDocuments();
}
