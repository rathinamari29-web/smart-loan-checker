package ui;

import db.DatabaseManager;
import exception.*;
import loan.*;
import model.*;
import service.CertificateGenerator;
import service.EMIComputer;
import service.EligibilityEngine;
import service.RiskAssessmentEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class MainGUI extends JFrame {

    private EligibilityEngine engine;

    // Form inputs
    private JTextField txtName;
    private JTextField txtAge;
    private JTextField txtIncome;
    private JTextField txtCreditScore;
    private JTextField txtExistingEmi;
    private JComboBox<String> cbLoanType;
    private JTextField txtAmount;
    private JTextField txtTenure;

    // Displays
    private JTabbedPane tabbedPane;
    private JPanel panelRecommendation;
    private JPanel panelBankCards;
    private JTextArea txtDocuments;
    private JTable tableHistory;
    private DefaultTableModel historyTableModel;

    // Standalone EMI Calculator Tab Controls
    private JTextField txtCalcAmount;
    private JTextField txtCalcRate;
    private JTextField txtCalcTenure;
    private JLabel lblCalcEmi;
    private JLabel lblCalcTotalInterest;
    private JLabel lblCalcTotalPayment;
    private DefaultTableModel matrixTableModel;

    private List<EligibilityResult> currentResults;
    private Loan currentLoan;
    private Applicant currentApplicant;

    public MainGUI() {
        super("Smart Loan Eligibility & Risk-Based Evaluation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 800);
        setLocationRelativeTo(null);

        // Initialize SQLite DB
        DatabaseManager.initializeDatabase();

        // Load Policies
        try {
            engine = new EligibilityEngine("lenders.txt");
        } catch (PolicyLoadException e) {
            JOptionPane.showMessageDialog(this, "Failed to load lenders.txt: " + e.getMessage(),
                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
        }

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Smart Loan Eligibility & Recommendation System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Multithreaded Evaluation • Risk-Based Pricing • Interactive EMI Calculator • Pre-Approval Generator");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(220, 221, 225));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Main Split Pane: Left Form Input, Right Results & Tabs
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(380);

        // Left Form Panel
        JPanel formPanel = createFormPanel();
        mainSplit.setLeftComponent(formPanel);

        // Right Tabbed Results Panel
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Eligibility Results & Recommendations
        JPanel resultsPanel = createResultsPanel();
        tabbedPane.addTab("Eligibility Results", resultsPanel);

        // Tab 2: Standalone Interactive EMI Calculator
        JPanel emiCalcPanel = createEmiCalculatorPanel();
        tabbedPane.addTab("🧮 Standalone EMI Calculator", emiCalcPanel);

        // Tab 3: Required Documents
        JPanel docsPanel = createDocsPanel();
        tabbedPane.addTab("Required Documents", docsPanel);

        // Tab 4: SQLite History
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Application History (SQLite)", historyPanel);

        mainSplit.setRightComponent(tabbedPane);
        add(mainSplit, BorderLayout.CENTER);

        // Initial history load
        refreshHistoryTable();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Applicant & Loan Details ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(25, 42, 86))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Applicant Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Applicant Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtName = new JTextField("Rahul Sharma", 15);
        panel.add(txtName, gbc);
        row++;

        // Age
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Age (Years):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAge = new JTextField("28", 15);
        panel.add(txtAge, gbc);
        row++;

        // Monthly Income
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Monthly Income (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtIncome = new JTextField("65000", 15);
        panel.add(txtIncome, gbc);
        row++;

        // Credit Score
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Credit Score (300-900):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCreditScore = new JTextField("720", 15);
        panel.add(txtCreditScore, gbc);
        row++;

        // Existing EMI
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Existing EMI (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtExistingEmi = new JTextField("5000", 15);
        panel.add(txtExistingEmi, gbc);
        row++;

        // Separator
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JSeparator(JSeparator.HORIZONTAL), gbc);
        gbc.gridwidth = 1;
        row++;

        // Loan Type
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Loan Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cbLoanType = new JComboBox<>(new String[]{"Personal Loan", "Home Loan", "Car Loan", "Education Loan"});
        panel.add(cbLoanType, gbc);
        row++;

        // Loan Amount
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Loan Amount (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAmount = new JTextField("500000", 15);
        panel.add(txtAmount, gbc);
        row++;

        // Tenure (Months)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Tenure (Months):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTenure = new JTextField("60", 15);
        panel.add(txtTenure, gbc);
        row++;

        // Buttons Panel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);

        JButton btnEvaluate = new JButton("⚡ Evaluate Multi-Lender Eligibility");
        btnEvaluate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEvaluate.setBackground(new Color(9, 132, 227));
        btnEvaluate.setForeground(Color.WHITE);
        btnEvaluate.setFocusPainted(false);
        btnEvaluate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEvaluate.addActionListener(e -> performEvaluation());

        panel.add(btnEvaluate, gbc);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Recommendation Card Header
        panelRecommendation = new JPanel(new BorderLayout());
        panelRecommendation.setBackground(new Color(245, 246, 250));
        panelRecommendation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblPlaceholder = new JLabel("Fill the form and click 'Evaluate Multi-Lender Eligibility' to process.");
        lblPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        panelRecommendation.add(lblPlaceholder, BorderLayout.CENTER);

        panel.add(panelRecommendation, BorderLayout.NORTH);

        // Bank Breakdown Scroll Pane
        panelBankCards = new JPanel();
        panelBankCards.setLayout(new BoxLayout(panelBankCards, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelBankCards);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar: Export & Pre-Approval Buttons
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnCert = new JButton("🎓 Download Pre-Approval Certificate");
        btnCert.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCert.setBackground(new Color(39, 174, 96));
        btnCert.setForeground(Color.WHITE);
        btnCert.addActionListener(e -> generatePreApprovalCertificate());

        JButton btnExport = new JButton("📄 Export Assessment Report");
        btnExport.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExport.addActionListener(e -> exportReport());

        bottomBar.add(btnCert);
        bottomBar.add(btnExport);

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEmiCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Form & Card Controls
        JPanel topCard = new JPanel(new GridLayout(4, 2, 10, 10));
        topCard.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Standalone Interactive EMI Calculator ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(25, 42, 86)));
        topCard.setBackground(Color.WHITE);
        topCard.setBorder(BorderFactory.createCompoundBorder(topCard.getBorder(), new EmptyBorder(10, 15, 10, 15)));

        topCard.add(new JLabel("Principal Loan Amount (₹):"));
        txtCalcAmount = new JTextField("1000000");
        topCard.add(txtCalcAmount);

        topCard.add(new JLabel("Annual Interest Rate (% p.a.):"));
        txtCalcRate = new JTextField("8.5");
        topCard.add(txtCalcRate);

        topCard.add(new JLabel("Tenure (Months):"));
        txtCalcTenure = new JTextField("240");
        topCard.add(txtCalcTenure);

        JButton btnCalculate = new JButton("🧮 Compute EMI & Breakdown");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCalculate.setBackground(new Color(41, 128, 185));
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.addActionListener(e -> computeEmiCalculator());

        topCard.add(new JLabel(""));
        topCard.add(btnCalculate);

        panel.add(topCard, BorderLayout.NORTH);

        // Middle Display Cards
        JPanel displayPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        displayPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel card1 = createSummaryCard("Monthly EMI", lblCalcEmi = new JLabel("₹8,678.23"), new Color(41, 128, 185));
        JPanel card2 = createSummaryCard("Total Interest Payable", lblCalcTotalInterest = new JLabel("₹10,82,776"), new Color(230, 126, 34));
        JPanel card3 = createSummaryCard("Total Amount Payable", lblCalcTotalPayment = new JLabel("₹20,82,776"), new Color(39, 174, 96));

        displayPanel.add(card1);
        displayPanel.add(card2);
        displayPanel.add(card3);

        // Bottom Tenure Sensitivity Matrix Table
        JPanel matrixPanel = new JPanel(new BorderLayout());
        matrixPanel.setBorder(BorderFactory.createTitledBorder(" Tenure vs EMI Sensitivity Analysis Matrix "));

        String[] cols = {"Tenure (Years)", "Tenure (Months)", "Monthly EMI (₹)", "Total Interest (₹)", "Total Payment (₹)"};
        matrixTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable matrixTable = new JTable(matrixTableModel);
        matrixTable.setRowHeight(24);
        matrixPanel.add(new JScrollPane(matrixTable), BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.add(displayPanel, BorderLayout.NORTH);
        centerContainer.add(matrixPanel, BorderLayout.CENTER);

        panel.add(centerContainer, BorderLayout.CENTER);

        // Initial compute
        computeEmiCalculator();

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(accentColor);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(new Color(44, 62, 80));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void computeEmiCalculator() {
        try {
            double p = Double.parseDouble(txtCalcAmount.getText().trim());
            double r = Double.parseDouble(txtCalcRate.getText().trim());
            int n = Integer.parseInt(txtCalcTenure.getText().trim());

            double emi = EMIComputer.computeEMI(p, r, n);
            double totalPayment = emi * n;
            double totalInterest = totalPayment - p;

            lblCalcEmi.setText(String.format("₹%.2f", emi));
            lblCalcTotalInterest.setText(String.format("₹%.2f", totalInterest));
            lblCalcTotalPayment.setText(String.format("₹%.2f", totalPayment));

            // Populate Tenure Matrix
            matrixTableModel.setRowCount(0);
            int[] tenures = {12, 24, 36, 60, 120, 180, 240, 300, 360};
            for (int t : tenures) {
                double tEmi = EMIComputer.computeEMI(p, r, t);
                double tPay = tEmi * t;
                double tInt = tPay - p;
                matrixTableModel.addRow(new Object[]{
                        (t / 12) + " Yrs",
                        t + " Months",
                        String.format("%.2f", tEmi),
                        String.format("%.2f", tInt),
                        String.format("%.2f", tPay)
                });
            }
        } catch (Exception ex) {
            lblCalcEmi.setText("Invalid Input");
        }
    }

    private JPanel createDocsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Required Documents Checklist");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblTitle, BorderLayout.NORTH);

        txtDocuments = new JTextArea();
        txtDocuments.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDocuments.setEditable(false);
        txtDocuments.setMargin(new Insets(10, 10, 10, 10));
        txtDocuments.setText("Submit an eligibility evaluation to view loan-specific document checklist.");

        panel.add(new JScrollPane(txtDocuments), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Applicant", "Loan Type", "Amount (₹)", "Tenure", "Result", "Recommended Bank", "EMI (₹)", "Date/Time"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableHistory = new JTable(historyTableModel);
        tableHistory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHistory.setRowHeight(24);

        panel.add(new JScrollPane(tableHistory), BorderLayout.CENTER);

        JPanel historyControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("🔄 Refresh History");
        btnRefresh.addActionListener(e -> refreshHistoryTable());

        JButton btnClear = new JButton("🗑️ Clear History");
        btnClear.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear SQLite history?",
                    "Confirm Clear", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.clearHistory();
                refreshHistoryTable();
            }
        });

        historyControlPanel.add(btnRefresh);
        historyControlPanel.add(btnClear);
        panel.add(historyControlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void performEvaluation() {
        try {
            String name = txtName.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());
            double income = Double.parseDouble(txtIncome.getText().trim());
            int creditScore = Integer.parseInt(txtCreditScore.getText().trim());
            double existingEmi = Double.parseDouble(txtExistingEmi.getText().trim());

            currentApplicant = new Applicant(name, age, income, creditScore, existingEmi);

            double amount = Double.parseDouble(txtAmount.getText().trim());
            int tenure = Integer.parseInt(txtTenure.getText().trim());
            String selectedLoanType = (String) cbLoanType.getSelectedItem();

            switch (selectedLoanType) {
                case "Personal Loan": currentLoan = new PersonalLoan(amount, tenure); break;
                case "Home Loan": currentLoan = new HomeLoan(amount, tenure); break;
                case "Car Loan": currentLoan = new CarLoan(amount, tenure); break;
                case "Education Loan": currentLoan = new EducationLoan(amount, tenure); break;
                default: throw new InvalidLoanException("Unsupported loan type.");
            }

            currentLoan.validateLoanSpecifics();

            // Execute evaluation in SwingWorker to keep UI responsive
            SwingWorker<List<EligibilityResult>, Void> worker = new SwingWorker<List<EligibilityResult>, Void>() {
                @Override
                protected List<EligibilityResult> doInBackground() throws Exception {
                    return engine.evaluateAllLenders(currentApplicant, currentLoan);
                }

                @Override
                protected void done() {
                    try {
                        currentResults = get();
                        updateResultsUI();
                        saveToDatabase();
                        refreshHistoryTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainGUI.this, "Error during evaluation: " + ex.getMessage(),
                                "Evaluation Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for Age, Income, Score, Amount, and Tenure.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidApplicantException | InvalidLoanException | InvalidCreditScoreException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateResultsUI() {
        if (currentResults == null) return;

        // Evaluate Risk Assessment
        RiskAssessmentEngine.RiskProfile risk = RiskAssessmentEngine.evaluateRisk(currentApplicant, currentLoan);

        // 1. Update Best Recommendation Banner
        panelRecommendation.removeAll();
        EligibilityResult best = engine.getBestRecommendation(currentResults);

        if (best != null) {
            panelRecommendation.setBackground(new Color(230, 247, 236));
            panelRecommendation.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                    new EmptyBorder(12, 15, 12, 15)
            ));

            JLabel lblBestTitle = new JLabel("🏆 BEST RECOMMENDED LENDER: " + best.getLenderName().toUpperCase());
            lblBestTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblBestTitle.setForeground(new Color(39, 174, 96));

            JLabel lblBestDetails = new JLabel(String.format("Lowest Monthly EMI: ₹%.2f / month  |  Rate: %.2f%% p.a.  |  %s",
                    best.getEmi(), best.getInterestRate(), risk.getRiskSummary()));
            lblBestDetails.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            panelRecommendation.add(lblBestTitle, BorderLayout.NORTH);
            panelRecommendation.add(lblBestDetails, BorderLayout.SOUTH);
        } else {
            panelRecommendation.setBackground(new Color(253, 237, 236));
            panelRecommendation.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                    new EmptyBorder(12, 15, 12, 15)
            ));

            JLabel lblRejectTitle = new JLabel("⚠️ APPLICANT INELIGIBLE FOR CURRENT LENDERS");
            lblRejectTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblRejectTitle.setForeground(new Color(192, 57, 43));

            JLabel lblRejectSub = new JLabel(String.format("Risk Grade: %s  |  %s", risk.getRiskGrade(), risk.getRiskSummary()));
            lblRejectSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            panelRecommendation.add(lblRejectTitle, BorderLayout.NORTH);
            panelRecommendation.add(lblRejectSub, BorderLayout.SOUTH);
        }

        panelRecommendation.revalidate();
        panelRecommendation.repaint();

        // 2. Build Bank Cards
        panelBankCards.removeAll();
        for (EligibilityResult result : currentResults) {
            JPanel card = createBankCard(result);
            panelBankCards.add(card);
            panelBankCards.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        panelBankCards.revalidate();
        panelBankCards.repaint();

        // 3. Update Documents Tab
        StringBuilder docSb = new StringBuilder();
        docSb.append("CHECKLIST FOR: ").append(currentLoan.getLoanType().toUpperCase()).append("\n");
        docSb.append("====================================================\n\n");
        int index = 1;
        for (String doc : currentLoan.getRequiredDocuments()) {
            docSb.append(index++).append(". ").append(doc).append("\n");
        }
        txtDocuments.setText(docSb.toString());
    }

    private JPanel createBankCard(EligibilityResult result) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(result.isEligible() ? new Color(46, 204, 113) : new Color(231, 76, 60), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setBackground(Color.WHITE);

        // Header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel lblBankName = new JLabel(result.getLenderName());
        lblBankName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblBadge = new JLabel(result.isEligible() ? " ELIGIBLE " : " INELIGIBLE ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setOpaque(true);
        if (result.isEligible()) {
            lblBadge.setBackground(new Color(46, 204, 113));
            lblBadge.setForeground(Color.WHITE);
        } else {
            lblBadge.setBackground(new Color(231, 76, 60));
            lblBadge.setForeground(Color.WHITE);
        }

        cardHeader.add(lblBankName, BorderLayout.WEST);
        cardHeader.add(lblBadge, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Content
        JPanel cardBody = new JPanel();
        cardBody.setLayout(new BoxLayout(cardBody, BoxLayout.Y_AXIS));
        cardBody.setOpaque(false);

        if (result.isEligible()) {
            JLabel lblEmiInfo = new JLabel(String.format("Monthly EMI: ₹%.2f  |  Risk-Adjusted Rate: %.2f%%  |  Total EMI Ratio: %.1f%%",
                    result.getEmi(), result.getInterestRate(), result.getEmiToIncomeRatio() * 100));
            lblEmiInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cardBody.add(lblEmiInfo);
        } else {
            JLabel lblReasonsHeader = new JLabel("Rejection Reasons:");
            lblReasonsHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblReasonsHeader.setForeground(new Color(192, 57, 43));
            cardBody.add(lblReasonsHeader);

            for (String reason : result.getRejectionReasons()) {
                JLabel lblReason = new JLabel(" • " + reason);
                lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                cardBody.add(lblReason);
            }

            List<String> advice = result.getCounterfactualSuggestion().getActionableAdvice();
            if (!advice.isEmpty()) {
                cardBody.add(Box.createRigidArea(new Dimension(0, 5)));
                JLabel lblAdviceHeader = new JLabel("💡 Mathematical Suggestions to Become Eligible:");
                lblAdviceHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblAdviceHeader.setForeground(new Color(41, 128, 185));
                cardBody.add(lblAdviceHeader);

                for (String adv : advice) {
                    JLabel lblAdv = new JLabel("   ➜ " + adv);
                    lblAdv.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    cardBody.add(lblAdv);
                }
            }
        }

        card.add(cardBody, BorderLayout.CENTER);
        return card;
    }

    private void generatePreApprovalCertificate() {
        if (currentApplicant == null || currentResults == null) {
            JOptionPane.showMessageDialog(this, "Please evaluate an applicant first to generate a certificate.",
                    "Pre-Approval Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EligibilityResult best = engine.getBestRecommendation(currentResults);
        if (best == null) {
            JOptionPane.showMessageDialog(this, "Pre-Approval Certificate cannot be issued because applicant is currently ineligible for all lenders.",
                    "Ineligible Applicant", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String certFile = CertificateGenerator.generatePreApprovalCertificate(currentApplicant, currentLoan, best);
            JOptionPane.showMessageDialog(this, "🎉 Digital Loan Pre-Approval Certificate generated successfully!\nSaved to: " + certFile,
                    "Pre-Approval Certificate Issued", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error issuing certificate: " + e.getMessage(),
                    "Certificate Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveToDatabase() {
        if (currentApplicant == null || currentLoan == null || currentResults == null) return;

        EligibilityResult best = engine.getBestRecommendation(currentResults);
        String status = (best != null) ? "APPROVED" : "REJECTED";
        String recLender = (best != null) ? best.getLenderName() : "None";
        double bestEmi = (best != null) ? best.getEmi() : 0.0;

        DatabaseManager.saveApplicationRecord(
                currentApplicant.getName(), currentApplicant.getAge(), currentApplicant.getMonthlyIncome(),
                currentApplicant.getCreditScore(), currentApplicant.getExistingEmi(), currentLoan.getLoanType(),
                currentLoan.getAmount(), currentLoan.getTenureMonths(), status, recLender, bestEmi
        );
    }

    private void refreshHistoryTable() {
        historyTableModel.setRowCount(0);
        List<Map<String, Object>> history = DatabaseManager.getApplicationHistory();
        for (Map<String, Object> r : history) {
            historyTableModel.addRow(new Object[]{
                    r.get("id"),
                    r.get("applicant_name"),
                    r.get("loan_type"),
                    String.format("%.2f", (Double) r.get("loan_amount")),
                    r.get("tenure"),
                    r.get("result"),
                    r.get("recommended_lender"),
                    String.format("%.2f", (Double) r.get("emi")),
                    r.get("application_date")
            });
        }
    }

    private void exportReport() {
        if (currentApplicant == null || currentResults == null) {
            JOptionPane.showMessageDialog(this, "No active evaluation to export. Please evaluate an applicant first.",
                    "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fileName = "Loan_Assessment_Report_" + currentApplicant.getName().replaceAll("\\s+", "_") + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==================================================");
            writer.println("     SMART LOAN ELIGIBILITY CHECKER REPORT        ");
            writer.println("==================================================");
            writer.println("Applicant Name: " + currentApplicant.getName());
            writer.println("Age: " + currentApplicant.getAge());
            writer.println("Monthly Income: ₹" + currentApplicant.getMonthlyIncome());
            writer.println("Credit Score: " + currentApplicant.getCreditScore());
            writer.println("Existing EMI: ₹" + currentApplicant.getExistingEmi());
            writer.println("Requested Loan: " + currentLoan.getLoanType());
            writer.println("Loan Amount: ₹" + currentLoan.getAmount());
            writer.println("Tenure: " + currentLoan.getTenureMonths() + " months");
            writer.println("--------------------------------------------------");

            EligibilityResult best = engine.getBestRecommendation(currentResults);
            if (best != null) {
                writer.println("RECOMMENDED BANK: " + best.getLenderName());
                writer.printf("Calculated Monthly EMI: ₹%.2f (Rate: %.2f%%)\n", best.getEmi(), best.getInterestRate());
            } else {
                writer.println("RECOMMENDED BANK: None (Ineligible for current lenders)");
            }
            writer.println("==================================================\n");

            writer.println("DETAILED LENDER EVALUATION BREAKDOWN:");
            for (EligibilityResult r : currentResults) {
                writer.println(r.toString());
                if (!r.isEligible()) {
                    for (String adv : r.getCounterfactualSuggestion().getActionableAdvice()) {
                        writer.println("  💡 Suggestion: " + adv);
                    }
                }
                writer.println();
            }

            JOptionPane.showMessageDialog(this, "Report exported successfully to file:\n" + fileName,
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainGUI().setVisible(true);
        });
    }
}
