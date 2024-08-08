
    function showTab(tabId) {
        // Hide all tab content
        var tabContents = document.querySelectorAll('.tab-content');
        tabContents.forEach(function(content) {
            content.classList.remove('active');
        });

        // Remove active class from all tab buttons
        var tabButtons = document.querySelectorAll('.tab-button');
        tabButtons.forEach(function(button) {
            button.classList.remove('active');
        });

        // Show the selected tab content
        var activeTabContent = document.getElementById(tabId);
        if (activeTabContent) {
            activeTabContent.classList.add('active');
        }

        // Add active class to the clicked tab button
        var activeButton = Array.from(tabButtons).find(button => button.textContent.toLowerCase() === tabId);
        if (activeButton) {
            activeButton.classList.add('active');
        }
    }

    // Initialize the first tab
    showTab('customer-box');

    // Set up event listeners for tab buttons
    var tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var tabId = this.textContent.toLowerCase();
            showTab(tabId);
        });
    });
