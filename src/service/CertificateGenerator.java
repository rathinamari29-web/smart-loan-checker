package service;

import model.Applicant;
import model.EligibilityResult;
import loan.Loan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CertificateGenerator {

    public static String generatePreApprovalCertificate(Applicant applicant, Loan loan, EligibilityResult bestResult) throws IOException {
        String certId = "CERT-SL-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String fileName = "Digital_PreApproval_Certificate_" + applicant.getName().replaceAll("\\s+", "_") + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==================================================================================");
            writer.println("               OFFICIAL DIGITAL LOAN PRE-APPROVAL CERTIFICATE                     ");
            writer.println("==================================================================================");
            writer.println(" Verification Code : " + certId);
            writer.println(" Issued Date        : " + dateStr);
            writer.println(" Status             : PRE-APPROVED BY LENDER");
            writer.println("----------------------------------------------------------------------------------");
            writer.println(" APPLICANT DETAILS:");
            writer.println(" • Name             : " + applicant.getName());
            writer.println(" • Age              : " + applicant.getAge() + " years");
            writer.println(" • Credit Score     : " + applicant.getCreditScore());
            writer.println(" • Monthly Income   : ₹" + String.format("%.2f", applicant.getMonthlyIncome()));
            writer.println("----------------------------------------------------------------------------------");
            writer.println(" APPROVED LOAN SPECIFICATIONS:");
            writer.println(" • Sanctioning Bank : " + bestResult.getLenderName().toUpperCase());
            writer.println(" • Loan Facility    : " + loan.getLoanType());
            writer.println(" • Approved Principal: ₹" + String.format("%.2f", loan.getAmount()));
            writer.println(" • Approved Tenure  : " + loan.getTenureMonths() + " Months");
            writer.println(" • Final Interest Rate: " + String.format("%.2f%% p.a. (Risk-Adjusted)", bestResult.getInterestRate()));
            writer.println(" • Monthly EMI      : ₹" + String.format("%.2f", bestResult.getEmi()));
            writer.println("----------------------------------------------------------------------------------");
            writer.println(" REQUIRED DOCUMENTS FOR FINAL DISBURSEMENT:");
            int idx = 1;
            for (String doc : loan.getRequiredDocuments()) {
                writer.println("   " + (idx++) + ". " + doc);
            }
            writer.println("----------------------------------------------------------------------------------");
            writer.println(" SECURITY STAMP & QR VERIFICATION CODE: [ " + certId.hashCode() + "-VERIFIED-SL ]");
            writer.println("==================================================================================");
        }

        return fileName;
    }
}
