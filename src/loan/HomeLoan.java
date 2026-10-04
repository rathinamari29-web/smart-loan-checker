package loan;

import exception.InvalidLoanException;
import java.util.Arrays;
import java.util.List;

public class HomeLoan extends Loan {

    public HomeLoan(double amount, int tenureMonths) throws InvalidLoanException {
        super(amount, tenureMonths);
    }

    @Override
    public String getLoanType() {
        return "Home Loan";
    }

    @Override
    public boolean validateLoanSpecifics() throws InvalidLoanException {
        if (amount < 200000) {
            throw new InvalidLoanException("Home loan minimum amount is ₹2,00,000.");
        }
        return true;
    }

    @Override
    public List<String> getRequiredDocuments() {
        return Arrays.asList(
            "PAN Card & Aadhaar Card",
            "Property Sale Agreement / Allotment Letter",
            "Property Title Deeds & Approved Building Plan",
            "Bank Statements for the last 6 months",
            "ITR with Computation of Income for 3 years",
            "NOC from Builder / Society"
        );
    }
}
