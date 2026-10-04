package loan;

import exception.InvalidLoanException;
import java.util.Arrays;
import java.util.List;

public class EducationLoan extends Loan {

    public EducationLoan(double amount, int tenureMonths) throws InvalidLoanException {
        super(amount, tenureMonths);
    }

    @Override
    public String getLoanType() {
        return "Education Loan";
    }

    @Override
    public boolean validateLoanSpecifics() throws InvalidLoanException {
        if (amount > 15000000) {
            throw new InvalidLoanException("Education Loan maximum limit is ₹1.5 Crore.");
        }
        return true;
    }

    @Override
    public List<String> getRequiredDocuments() {
        return Arrays.asList(
            "Admission Offer Letter from University / College",
            "Fee Structure Breakdown Document",
            "Academic Marksheets & Certificates (10th, 12th, Degree)",
            "Co-Applicant / Parent Income & Identity Proofs",
            "Collateral Documents (if loan > ₹7.5 Lakhs)"
        );
    }
}
