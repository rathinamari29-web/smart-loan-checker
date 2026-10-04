package loan;

public interface LoanCalculator {
    double calculateEMI(double annualInterestRate);
    boolean validateLoanSpecifics() throws exception.InvalidLoanException;
}
