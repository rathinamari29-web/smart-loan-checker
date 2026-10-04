package service;

import model.Applicant;
import model.EligibilityResult;
import model.LenderPolicy;
import loan.Loan;

public interface EligibilityRule {
    EligibilityResult evaluate(Applicant applicant, Loan loan, LenderPolicy policy);
}
