package service;

import model.EligibilityResult;
import java.util.List;

public interface NotificationService {
    void notifyEvaluationComplete(String applicantName, List<EligibilityResult> results);
    void sendRejectionSummary(String applicantName, List<EligibilityResult> rejectedResults);
}
