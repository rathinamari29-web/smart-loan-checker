package model;

import exception.InvalidApplicantException;
import exception.InvalidCreditScoreException;

public class Applicant {
    private String name;
    private int age;
    private double monthlyIncome;
    private int creditScore;
    private double existingEmi;

    public Applicant(String name, int age, double monthlyIncome, int creditScore, double existingEmi) 
            throws InvalidApplicantException, InvalidCreditScoreException {
        
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidApplicantException("Applicant name cannot be empty.");
        }
        if (age < 18 || age > 100) {
            throw new InvalidApplicantException("Age must be between 18 and 100.");
        }
        if (monthlyIncome <= 0) {
            throw new InvalidApplicantException("Monthly income must be greater than zero.");
        }
        if (creditScore < 300 || creditScore > 900) {
            throw new InvalidCreditScoreException("Credit score must be between 300 and 900.");
        }
        if (existingEmi < 0) {
            throw new InvalidApplicantException("Existing EMI cannot be negative.");
        }

        this.name = name.trim();
        this.age = age;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
        this.existingEmi = existingEmi;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public double getExistingEmi() {
        return existingEmi;
    }

    @Override
    public String toString() {
        return String.format("Applicant[Name: %s, Age: %d, Income: ₹%.2f, CreditScore: %d, ExistingEMI: ₹%.2f]",
                name, age, monthlyIncome, creditScore, existingEmi);
    }
}
