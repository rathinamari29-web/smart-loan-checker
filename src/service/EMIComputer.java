package service;

public class EMIComputer {

    public static double computeEMI(double principal, double annualInterestRate, int tenureMonths) {
        if (principal <= 0 || tenureMonths <= 0) return 0.0;
        if (annualInterestRate <= 0) {
            return principal / tenureMonths;
        }

        double r = (annualInterestRate / 12) / 100;
        double emi = (principal * r * Math.pow(1 + r, tenureMonths)) / (Math.pow(1 + r, tenureMonths) - 1);
        return emi;
    }

    public static double computeMaxPrincipal(double maxAllowedEmi, double annualInterestRate, int tenureMonths) {
        if (maxAllowedEmi <= 0 || tenureMonths <= 0) return 0.0;
        if (annualInterestRate <= 0) {
            return maxAllowedEmi * tenureMonths;
        }

        double r = (annualInterestRate / 12) / 100;
        double maxPrincipal = (maxAllowedEmi * (Math.pow(1 + r, tenureMonths) - 1)) / (r * Math.pow(1 + r, tenureMonths));
        return maxPrincipal;
    }
}
