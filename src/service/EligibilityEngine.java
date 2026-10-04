package service;

import exception.PolicyLoadException;
import model.Applicant;
import model.EligibilityResult;
import model.LenderPolicy;
import loan.Loan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class EligibilityEngine {

    private final List<LenderPolicy> lenderPolicies;

    public EligibilityEngine(String policyFilePath) throws PolicyLoadException {
        this.lenderPolicies = PolicyLoader.loadPolicies(policyFilePath);
    }

    public List<LenderPolicy> getLenderPolicies() {
        return Collections.unmodifiableList(lenderPolicies);
    }

    public List<EligibilityResult> evaluateAllLenders(Applicant applicant, Loan loan) {
        List<EligibilityResult> results = new ArrayList<>();
        int threadCount = Math.max(1, lenderPolicies.size());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<EligibilityResult>> futures = new ArrayList<>();

        // Launch multithreaded evaluation tasks
        for (LenderPolicy policy : lenderPolicies) {
            LenderEvaluationTask task = new LenderEvaluationTask(applicant, loan, policy);
            futures.add(executor.submit(task));
        }

        // Collect results safely from threads
        for (Future<EligibilityResult> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error evaluating lender task: " + e.getMessage());
            }
        }

        executor.shutdown();

        // Unit 5: Sort results (Eligible lenders sorted by EMI ascending, then Ineligible lenders)
        Collections.sort(results, new Comparator<EligibilityResult>() {
            @Override
            public int compare(EligibilityResult r1, EligibilityResult r2) {
                if (r1.isEligible() && !r2.isEligible()) return -1;
                if (!r1.isEligible() && r2.isEligible()) return 1;
                if (r1.isEligible() && r2.isEligible()) {
                    return Double.compare(r1.getEmi(), r2.getEmi());
                }
                return r1.getLenderName().compareTo(r2.getLenderName());
            }
        });

        return results;
    }

    public EligibilityResult getBestRecommendation(List<EligibilityResult> results) {
        for (EligibilityResult res : results) {
            if (res.isEligible()) {
                return res; // First eligible lender is the lowest EMI option due to sorting
            }
        }
        return null; // No eligible lender found
    }
}
