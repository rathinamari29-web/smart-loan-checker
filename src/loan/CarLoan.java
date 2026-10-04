package loan;

import exception.InvalidLoanException;
import java.util.Arrays;
import java.util.List;

public class CarLoan extends Loan {

    public CarLoan(double amount, int tenureMonths) throws InvalidLoanException {
        super(amount, tenureMonths);
    }

    @Override
    public String getLoanType() {
        return "Car Loan";
    }

    @Override
    public boolean validateLoanSpecifics() throws InvalidLoanException {
        if (tenureMonths > 84) {
            throw new InvalidLoanException("Car Loan maximum tenure is 84 months (7 years).");
        }
        return true;
    }

    @Override
    public List<String> getRequiredDocuments() {
        return Arrays.asList(
            "PAN Card & Driving License / Passport",
            "Vehicle Proforma Invoice / Quotation",
            "Salary Slips (3 months) / Business Income Proof",
            "Bank Statements (6 months)",
            "Proof of Residence"
        );
    }
}
