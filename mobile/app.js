// Mobile Application Logic - Smart Loan Eligibility & Standalone EMI Tool

const LENDER_POLICIES = [
  { name: "SBI Bank", minAge: 21, maxAge: 60, minScore: 650, minIncome: 25000, maxEmiRatio: 0.50, interestRate: 8.50, maxTenure: 360, maxAmount: 10000000 },
  { name: "HDFC Bank", minAge: 22, maxAge: 65, minScore: 700, minIncome: 30000, maxEmiRatio: 0.55, interestRate: 9.25, maxTenure: 300, maxAmount: 15000000 },
  { name: "ICICI Bank", minAge: 21, maxAge: 60, minScore: 680, minIncome: 28000, maxEmiRatio: 0.50, interestRate: 8.90, maxTenure: 360, maxAmount: 12000000 },
  { name: "Axis Bank", minAge: 21, maxAge: 62, minScore: 670, minIncome: 27000, maxEmiRatio: 0.48, interestRate: 9.10, maxTenure: 300, maxAmount: 9000000 },
  { name: "Punjab National Bank", minAge: 18, maxAge: 65, minScore: 620, minIncome: 20000, maxEmiRatio: 0.45, interestRate: 8.40, maxTenure: 360, maxAmount: 8000000 },
  { name: "Kotak Mahindra Bank", minAge: 23, maxAge: 60, minScore: 720, minIncome: 35000, maxEmiRatio: 0.60, interestRate: 9.50, maxTenure: 240, maxAmount: 20000000 }
];

const LOAN_DOCUMENTS = {
  "Personal Loan": [
    "PAN Card & Aadhaar Card (Identity & Address Proof)",
    "Salary Slips for the last 3 months",
    "Bank Account Statements for the last 6 months",
    "Form 16 / ITR for the last 2 years",
    "Employment ID Card / Offer Letter"
  ],
  "Home Loan": [
    "PAN Card & Aadhaar Card",
    "Property Sale Agreement / Allotment Letter",
    "Property Title Deeds & Approved Building Plan",
    "Bank Statements for the last 6 months",
    "ITR with Computation of Income for 3 years",
    "NOC from Builder / Society"
  ],
  "Car Loan": [
    "PAN Card & Driving License / Passport",
    "Vehicle Proforma Invoice / Quotation",
    "Salary Slips (3 months) / Business Income Proof",
    "Bank Statements (6 months)",
    "Proof of Residence"
  ],
  "Education Loan": [
    "Admission Offer Letter from University / College",
    "Fee Structure Breakdown Document",
    "Academic Marksheets & Certificates (10th, 12th, Degree)",
    "Co-Applicant / Parent Income & Identity Proofs",
    "Collateral Documents (if loan > ₹7.5 Lakhs)"
  ]
};

// DOM Elements
const inputName = document.getElementById("input-name");
const inputAge = document.getElementById("input-age");
const inputIncome = document.getElementById("input-income");
const inputScore = document.getElementById("input-score");
const inputExistingEmi = document.getElementById("input-existing-emi");
const inputLoanType = document.getElementById("input-loan-type");

const sliderAmount = document.getElementById("slider-amount");
const valAmount = document.getElementById("val-amount");

const sliderTenure = document.getElementById("slider-tenure");
const valTenure = document.getElementById("val-tenure");

const lblLiveEmi = document.getElementById("lbl-live-emi");
const btnEvaluate = document.getElementById("btn-evaluate");

const toolAmount = document.getElementById("tool-amount");
const toolRate = document.getElementById("tool-rate");
const toolTenure = document.getElementById("tool-tenure");
const btnComputeTool = document.getElementById("btn-compute-tool");
const toolResultsContainer = document.getElementById("tool-results-container");

const resultsBanner = document.getElementById("results-banner");
const resultsList = document.getElementById("results-list");
const docsListContainer = document.getElementById("docs-list-container");
const historyContainer = document.getElementById("history-container");
const btnClearHistory = document.getElementById("btn-clear-history");

function formatINR(val) {
  return "₹" + Number(val).toLocaleString("en-IN", { maximumFractionDigits: 0 });
}

function calculateEMI(principal, annualRate, tenureMonths) {
  if (principal <= 0 || tenureMonths <= 0) return 0;
  if (annualRate <= 0) return principal / tenureMonths;

  const r = (annualRate / 12) / 100;
  return (principal * r * Math.pow(1 + r, tenureMonths)) / (Math.pow(1 + r, tenureMonths) - 1);
}

function calculateMaxPrincipal(maxEmi, annualRate, tenureMonths) {
  if (maxEmi <= 0 || tenureMonths <= 0) return 0;
  if (annualRate <= 0) return maxEmi * tenureMonths;

  const r = (annualRate / 12) / 100;
  return (maxEmi * (Math.pow(1 + r, tenureMonths) - 1)) / (r * Math.pow(1 + r, tenureMonths));
}

function updateLiveEmi() {
  const amount = Number(sliderAmount.value);
  const tenure = Number(sliderTenure.value);
  
  valAmount.textContent = formatINR(amount);
  valTenure.textContent = `${tenure} Months (${(tenure / 12).toFixed(1)} Yrs)`;

  const liveEmi = calculateEMI(amount, 8.5, tenure);
  lblLiveEmi.textContent = formatINR(liveEmi);
}

sliderAmount.addEventListener("input", updateLiveEmi);
sliderTenure.addEventListener("input", updateLiveEmi);

// Navigation Switcher
document.querySelectorAll(".nav-item").forEach(nav => {
  nav.addEventListener("click", () => {
    document.querySelectorAll(".nav-item").forEach(n => n.classList.remove("active"));
    document.querySelectorAll(".tab-view").forEach(t => t.classList.remove("active"));

    nav.classList.add("active");
    const tabId = nav.getAttribute("data-tab");
    document.getElementById(tabId).classList.add("active");
  });
});

// Standalone EMI Tool Calculation
btnComputeTool.addEventListener("click", () => {
  const p = Number(toolAmount.value);
  const r = Number(toolRate.value);
  const n = Number(toolTenure.value);

  if (p <= 0 || n <= 0) {
    alert("Please enter valid principal amount and tenure.");
    return;
  }

  const emi = calculateEMI(p, r, n);
  const totalPay = emi * n;
  const totalInt = totalPay - p;

  const tenures = [12, 24, 36, 60, 120, 180, 240, 360];

  toolResultsContainer.innerHTML = `
    <div class="card" style="background: #f8fafc;">
      <div style="font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">Calculated Monthly EMI</div>
      <div style="font-size: 28px; font-weight: 800; color: var(--primary); margin: 4px 0;">${formatINR(emi)}</div>
      
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-top: 10px; padding-top: 10px; border-top: 1px dashed #cbd5e1;">
        <div>Total Interest: <strong style="color: #d97706;">${formatINR(totalInt)}</strong></div>
        <div>Total Payment: <strong style="color: #059669;">${formatINR(totalPay)}</strong></div>
      </div>
    </div>

    <div class="card">
      <div class="card-title">📊 Tenure Sensitivity Matrix</div>
      <div style="font-size: 11px; color: var(--text-muted); margin-bottom: 8px;">Comparing monthly EMI across tenure options:</div>
      ${tenures.map(t => {
        const tEmi = calculateEMI(p, r, t);
        return `
          <div style="display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px solid #f1f5f9; font-size: 12px;">
            <span>${t} Months (${(t/12).toFixed(1)} Yrs)</span>
            <strong style="color: var(--primary);">${formatINR(tEmi)} / mo</strong>
          </div>
        `;
      }).join('')}
    </div>
  `;
});

// Evaluate Multi-Lender Eligibility
btnEvaluate.addEventListener("click", async () => {
  const name = inputName.value.trim();
  const age = Number(inputAge.value);
  const income = Number(inputIncome.value);
  const score = Number(inputScore.value);
  const existingEmi = Number(inputExistingEmi.value);
  const loanType = inputLoanType.value;
  const amount = Number(sliderAmount.value);
  const tenure = Number(sliderTenure.value);

  if (!name || age < 18 || income <= 0 || score < 300 || score > 900) {
    alert("Please enter valid applicant details (Age >= 18, Credit Score 300-900, Positive Income).");
    return;
  }

  // Multithreaded Evaluation Simulation via Async Promises
  const evaluationPromises = LENDER_POLICIES.map(policy => {
    return new Promise(resolve => {
      setTimeout(() => {
        // Risk-Based Pricing Adjustment
        let rateAdj = 0;
        if (score >= 780) rateAdj = -0.50;
        else if (score >= 720) rateAdj = -0.25;
        else if (score < 650) rateAdj = 0.75;

        const adjustedRate = Math.max(5.0, policy.interestRate + rateAdj);
        const emi = calculateEMI(amount, adjustedRate, tenure);
        const totalEmi = existingEmi + emi;
        const ratio = totalEmi / income;

        const reasons = [];
        const suggestions = [];

        let isAgeOk = age >= policy.minAge && age <= policy.maxAge;
        let isScoreOk = score >= policy.minScore;
        let isIncomeOk = income >= policy.minIncome;
        let isAmountOk = amount <= policy.maxAmount;
        let isTenureOk = tenure <= policy.maxTenure;
        let isRatioOk = ratio <= policy.maxEmiRatio;

        if (!isAgeOk) reasons.push(`Age (${age}) outside lender limits (${policy.minAge}-${policy.maxAge} yrs)`);
        if (!isScoreOk) {
          reasons.push(`Credit score (${score}) below minimum required (${policy.minScore})`);
          suggestions.push(`Boost credit score by ${policy.minScore - score} points.`);
        }
        if (!isIncomeOk) {
          reasons.push(`Income (${formatINR(income)}) below minimum required (${formatINR(policy.minIncome)})`);
          suggestions.push(`Increase monthly income by ${formatINR(policy.minIncome - income)}.`);
        }
        if (!isAmountOk) reasons.push(`Amount (${formatINR(amount)}) exceeds bank limit (${formatINR(policy.maxAmount)})`);
        if (!isTenureOk) reasons.push(`Tenure (${tenure} m) exceeds max allowed (${policy.maxTenure} m)`);

        if (!isRatioOk) {
          reasons.push(`EMI-to-Income ratio (${(ratio * 100).toFixed(1)}%) exceeds maximum cap (${(policy.maxEmiRatio * 100).toFixed(0)}%)`);
          
          const maxAllowedTotalEmi = income * policy.maxEmiRatio;
          const maxAllowedNewEmi = maxAllowedTotalEmi - existingEmi;

          if (maxAllowedNewEmi <= 0) {
            suggestions.push(`Existing EMI (${formatINR(existingEmi)}) already exceeds max allowed EMI (${formatINR(maxAllowedTotalEmi)}). Reduce existing debt first.`);
          } else {
            const maxEligibleAmount = calculateMaxPrincipal(maxAllowedNewEmi, adjustedRate, tenure);
            if (maxEligibleAmount > 0 && maxEligibleAmount < amount) {
              suggestions.push(`Reduce requested loan amount to ~${formatINR(maxEligibleAmount)}.`);
            }

            if (tenure < policy.maxTenure) {
              const emiWithMaxTenure = calculateEMI(amount, adjustedRate, policy.maxTenure);
              if ((existingEmi + emiWithMaxTenure) / income <= policy.maxEmiRatio) {
                suggestions.push(`Increase tenure from ${tenure} to ${policy.maxTenure} months.`);
              }
            }
          }
        }

        const isEligible = isAgeOk && isScoreOk && isIncomeOk && isAmountOk && isTenureOk && isRatioOk;

        resolve({
          lenderName: policy.name,
          interestRate: adjustedRate,
          isEligible,
          emi,
          ratio,
          reasons,
          suggestions
        });
      }, 40);
    });
  });

  const results = await Promise.all(evaluationPromises);

  results.sort((a, b) => {
    if (a.isEligible && !b.isEligible) return -1;
    if (!a.isEligible && b.isEligible) return 1;
    if (a.isEligible && b.isEligible) return a.emi - b.emi;
    return a.lenderName.localeCompare(b.lenderName);
  });

  renderResults(results, name, loanType, amount, tenure);
  renderDocuments(loanType);
  saveApplicationHistory(name, loanType, amount, tenure, results);

  document.querySelector('[data-tab="tab-results"]').click();
});

function renderResults(results, name, loanType, amount, tenure) {
  const best = results.find(r => r.isEligible);

  if (best) {
    resultsBanner.innerHTML = `
      <div class="rec-card eligible">
        <div style="font-size: 11px; font-weight: 700; color: #047857; text-transform: uppercase;">🏆 Best Recommended Lender</div>
        <div style="font-size: 20px; font-weight: 800; color: #065f46; margin: 4px 0;">${best.lenderName}</div>
        <div style="font-size: 13px; color: #047857;">Lowest EMI: <strong>${formatINR(best.emi)} / mo</strong> @ ${best.interestRate.toFixed(2)}% p.a.</div>
        <button onclick="downloadPreApproval('${name}', '${best.lenderName}', ${amount}, ${tenure}, ${best.emi})" style="margin-top: 10px; width: 100%; padding: 8px; font-size: 12px; font-weight: 700; background: #059669; color: white; border: none; border-radius: 8px; cursor: pointer;">🎓 Download Pre-Approval Certificate</button>
      </div>
    `;
  } else {
    resultsBanner.innerHTML = `
      <div class="rec-card ineligible">
        <div style="font-size: 11px; font-weight: 700; color: #b91c1c; text-transform: uppercase;">⚠️ Ineligible for Current Lenders</div>
        <div style="font-size: 13px; color: #991b1b; margin-top: 4px;">Review mathematical suggestions below to adjust your application details.</div>
      </div>
    `;
  }

  resultsList.innerHTML = results.map(r => `
    <div class="card" style="border-left: 5px solid ${r.isEligible ? '#10b981' : '#ef4444'};">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
        <span style="font-weight: 700; font-size: 15px;">${r.lenderName}</span>
        <span class="badge ${r.isEligible ? 'badge-success' : 'badge-danger'}">${r.isEligible ? 'Eligible' : 'Rejected'}</span>
      </div>
      
      ${r.isEligible ? `
        <div style="font-size: 13px; color: var(--text-muted);">
          Monthly EMI: <strong style="color: var(--text-main);">${formatINR(r.emi)}</strong> | Rate: ${r.interestRate.toFixed(2)}% | Ratio: ${(r.ratio * 100).toFixed(1)}%
        </div>
      ` : `
        <div style="font-size: 12px; color: var(--danger); margin-bottom: 6px;">
          ${r.rejectionReasons.map(reason => `• ${reason}`).join('<br>')}
        </div>
        ${r.suggestions.length > 0 ? `
          <div class="suggestion-box">
            <strong>💡 Smart Mathematical Suggestions:</strong><br>
            ${r.suggestions.map(s => `➜ ${s}`).join('<br>')}
          </div>
        ` : ''}
      `}
    </div>
  `).join('');
}

window.downloadPreApproval = function(name, bank, amount, tenure, emi) {
  const content = `OFFICIAL DIGITAL LOAN PRE-APPROVAL CERTIFICATE\n` +
    `Verification Code: CERT-${Date.now()}\n` +
    `Applicant: ${name}\n` +
    `Sanctioning Bank: ${bank}\n` +
    `Approved Loan: ${formatINR(amount)} (${tenure} Months)\n` +
    `Monthly EMI: ${formatINR(emi)}\n` +
    `Status: PRE-APPROVED BY LENDER`;
  
  const blob = new Blob([content], { type: "text/plain" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = `PreApproval_Certificate_${name.replace(/\s+/g, '_')}.txt`;
  a.click();
};

function renderDocuments(loanType) {
  const docs = LOAN_DOCUMENTS[loanType] || [];
  docsListContainer.innerHTML = `
    <div style="font-weight: 700; font-size: 13px; color: var(--primary); margin-bottom: 10px;">Checklist for ${loanType}:</div>
    <ul style="padding-left: 18px; font-size: 13px; color: var(--text-main); display: flex; flex-direction: column; gap: 8px;">
      ${docs.map(d => `<li>${d}</li>`).join('')}
    </ul>
  `;
}

function saveApplicationHistory(name, loanType, amount, tenure, results) {
  const best = results.find(r => r.isEligible);
  const record = {
    id: Date.now(),
    name,
    loanType,
    amount,
    tenure,
    status: best ? "APPROVED" : "REJECTED",
    recommendedBank: best ? best.lenderName : "None",
    emi: best ? best.emi : 0,
    timestamp: new Date().toLocaleString()
  };

  const history = JSON.parse(localStorage.getItem("loan_history") || "[]");
  history.unshift(record);
  localStorage.setItem("loan_history", JSON.stringify(history));

  renderHistory();
}

function renderHistory() {
  const history = JSON.parse(localStorage.getItem("loan_history") || "[]");
  if (history.length === 0) {
    historyContainer.innerHTML = `<p style="font-size: 13px; color: var(--text-muted);">No application history recorded yet.</p>`;
    return;
  }

  historyContainer.innerHTML = history.map(h => `
    <div style="padding: 10px 0; border-bottom: 1px solid #f1f5f9; font-size: 12px;">
      <div style="display: flex; justify-content: space-between; font-weight: 700;">
        <span>${h.name} (${h.loanType})</span>
        <span style="color: ${h.status === 'APPROVED' ? '#10b981' : '#ef4444'};">${h.status}</span>
      </div>
      <div style="color: var(--text-muted); margin-top: 2px;">
        Loan: ${formatINR(h.amount)} (${h.tenure} m) | Rec: ${h.recommendedBank} ${h.emi ? '| EMI: ' + formatINR(h.emi) : ''}
      </div>
      <div style="font-size: 10px; color: #94a3b8; margin-top: 2px;">${h.timestamp}</div>
    </div>
  `).join('');
}

btnClearHistory.addEventListener("click", () => {
  localStorage.removeItem("loan_history");
  renderHistory();
});

// Initialize App
updateLiveEmi();
renderHistory();
btnComputeTool.click();
