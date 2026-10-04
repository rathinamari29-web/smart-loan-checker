package loan;

import exception.InvalidLoanException;
import java.util.Arrays;
import java.util.List;

public class PersonalLoan extends Loan {

    public PersonalLoan(double amount, int tenureMonths) throws InvalidLoanException {
        super(amount, tenureMonths);
    }

    @Override
    public String getLoanType() {
        return "Personal Loan";
    }

    @Override
    public boolean validateLoanSpecifics() throws InvalidLoanException {
        if (tenureMonths > 84) { // Personal loans capped at 7 years (84 months)
            throw new InvalidLoanException("Personal Loan maximum tenure is 84 months (7 years).");
        }
        return true;
    }

    @Override
    public List<String> getRequiredDocuments() {
        return Arrays.asList(
            "PAN Card & Aadhaar Card (Identity & Address Proof)",
            "Salary Slips for the last 3 months",
            "Bank Account Statements for the last 6 months",
            "Form 16 / ITR for the last 2 years",
            "Employment ID Card / Offer Letter"
        );
    }
}
