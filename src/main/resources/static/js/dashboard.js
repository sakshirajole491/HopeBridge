// Dashboard JavaScript Functions
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard JS loaded');
    initializeCharts();
    loadDashboardData();
    setupEventListeners();
});

// Initialize Charts
function initializeCharts() {
    console.log('Initializing charts...');
    
    // Check if Chart.js is loaded
    if (typeof Chart === 'undefined') {
        console.warn('Chart.js not found');
        return;
    }

    // Initialize Sponsorship Chart (Pie Chart)
    const sponsorshipCtx = document.getElementById('sponsorshipChart');
    if (sponsorshipCtx) {
        new Chart(sponsorshipCtx, {
            type: 'doughnut',
            data: {
                labels: ['Sponsored', 'Unsponsored'],
                datasets: [{
                    data: [1000, 2000],
                    backgroundColor: ['#6A0DAD', '#DCC6FF'],
                    borderColor: ['#6A0DAD', '#DCC6FF'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // Initialize Donation Chart (Bar Chart)
    const donationCtx = document.getElementById('donationChart');
    if (donationCtx) {
        new Chart(donationCtx, {
            type: 'bar',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
                datasets: [{
                    label: 'Donations ($)',
                    data: [500, 750, 600, 900, 1100],
                    backgroundColor: '#6A0DAD',
                    borderColor: '#6A0DAD',
                    borderWidth: 1,
                    borderRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    }
                }
            }
        });
    }
}

// Load Dashboard Data
function loadDashboardData() {
    console.log('Loading dashboard data...');
    
    // Fetch and update statistics
    updateStatistics();
    
    // Load recent transactions
    loadRecentTransactions();
    
    // Load alerts
    loadAlerts();
}

// Update Statistics
function updateStatistics() {
    console.log('Updating statistics...');
    
    // Simulate API calls - replace with real API endpoints
    const stats = {
        totalDonations: 50000,
        todaysDonations: 5000,
        totalDonors: 500
    };

    // Update stat cards
    document.querySelectorAll('[data-stat]').forEach(element => {
        const stat = element.getAttribute('data-stat');
        if (stats[stat]) {
            element.textContent = formatNumber(stats[stat]);
        }
    });
}

// Load Recent Transactions
function loadRecentTransactions() {
    console.log('Loading recent transactions...');
    
    const transactionsTable = document.querySelector('[data-transactions-table]');
    if (!transactionsTable) return;

    // Simulate loading data
    const transactions = [
        { donor: 'John Doe', amount: 1000, type: 'General', date: '2024-01-15' },
        { donor: 'Jane Smith', amount: 2000, type: 'Sponsorship', date: '2024-01-14' },
        { donor: 'Mike Johnson', amount: 500, type: 'General', date: '2024-01-13' }
    ];

    // Clear existing rows
    transactionsTable.querySelectorAll('tbody tr').forEach(row => row.remove());

    // Add new rows
    transactions.forEach(transaction => {
        const row = transactionsTable.querySelector('tbody').insertRow();
        row.innerHTML = `
            <td>${transaction.donor}</td>
            <td>$${transaction.amount}</td>
            <td>${transaction.type}</td>
            <td>${transaction.date}</td>
        `;
    });
}

// Load Alerts
function loadAlerts() {
    console.log('Loading alerts...');
    
    const alertsContainer = document.querySelector('[data-alerts-container]');
    if (!alertsContainer) return;

    // Simulate loading alerts
    const alerts = [
        { type: 'warning', message: 'Fund levels are low for Orphanage A' },
        { type: 'success', message: 'New donation received from donor B' },
    ];

    alertsContainer.innerHTML = '';

    alerts.forEach(alert => {
        const alertElement = document.createElement('div');
        alertElement.className = `alert alert-${alert.type} alert-dismissible fade show`;
        alertElement.innerHTML = `
            ${alert.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        alertsContainer.appendChild(alertElement);
    });
}

// Setup Event Listeners
function setupEventListeners() {
    console.log('Setting up event listeners...');

    // Donate button
    document.querySelectorAll('[data-action="donate"]').forEach(button => {
        button.addEventListener('click', function() {
            window.location.href = '/donor/donate';
        });
    });

    // Profile button
    document.querySelectorAll('[data-action="profile"]').forEach(button => {
        button.addEventListener('click', function() {
            window.location.href = '/donor/profile';
        });
    });

    // Form submissions
    document.querySelectorAll('form[data-async-submit]').forEach(form => {
        form.addEventListener('submit', handleAsyncSubmit);
    });
}

// Handle Async Form Submission
function handleAsyncSubmit(e) {
    e.preventDefault();
    console.log('Submitting form asynchronously...');

    const form = e.target;
    const formData = new FormData(form);

    // Show loading state
    const submitButton = form.querySelector('[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Loading...';

    // Simulate API call
    setTimeout(() => {
        // Reset button
        submitButton.disabled = false;
        submitButton.textContent = originalText;

        // Show success message
        showNotification('Success!', 'Form submitted successfully', 'success');
    }, 1500);
}

// Format Numbers
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

// Show Notification
function showNotification(title, message, type = 'info') {
    const alertClass = `alert-${type}`;
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            <strong>${title}</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    // Insert at top of page
    const container = document.querySelector('[data-alerts-container]') || document.body;
    const alertElement = document.createElement('div');
    alertElement.innerHTML = alertHtml;
    container.insertBefore(alertElement.firstElementChild, container.firstChild);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        const alert = container.querySelector('.alert');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

// Refresh Dashboard Data
function refreshDashboard() {
    console.log('Refreshing dashboard...');
    loadDashboardData();
    showNotification('Dashboard', 'Data refreshed successfully', 'success');
}

// Export Data
function exportData(format = 'csv') {
    console.log('Exporting data as ' + format);
    
    if (format === 'csv') {
        exportToCSV();
    } else if (format === 'pdf') {
        window.print();
    }
}

// Export to CSV
function exportToCSV() {
    const table = document.querySelector('table');
    if (!table) return;

    let csv = [];
    const headers = [];
    
    // Get headers
    table.querySelectorAll('thead th').forEach(th => {
        headers.push(th.textContent.trim());
    });
    csv.push(headers.join(','));

    // Get rows
    table.querySelectorAll('tbody tr').forEach(tr => {
        const row = [];
        tr.querySelectorAll('td').forEach(td => {
            row.push(td.textContent.trim());
        });
        csv.push(row.join(','));
    });

    // Create and download file
    const csvContent = csv.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'data.csv';
    link.click();
}

// Print Report
function printReport() {
    console.log('Printing report...');
    window.print();
}

// Search Functionality
function searchTable(tableId, searchTerm) {
    const table = document.getElementById(tableId);
    if (!table) return;

    const rows = table.querySelectorAll('tbody tr');
    let visibleCount = 0;

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm.toLowerCase())) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    console.log(`Found ${visibleCount} matching rows`);
}

// Pagination
function paginate(items, pageSize) {
    const pages = [];
    for (let i = 0; i < items.length; i += pageSize) {
        pages.push(items.slice(i, i + pageSize));
    }
    return pages;
}

// Date Range Filter
function filterByDateRange(startDate, endDate) {
    console.log(`Filtering data from ${startDate} to ${endDate}`);
    loadDashboardData();
}

// Export summary statistics
window.dashboardUtils = {
    formatNumber,
    showNotification,
    refreshDashboard,
    exportData,
    exportToCSV,
    printReport,
    searchTable,
    paginate,
    filterByDateRange
};

console.log('Dashboard utilities loaded successfully');
