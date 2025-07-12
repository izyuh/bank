let username = sessionStorage.getItem('CurrentUser');
let balance = sessionStorage.getItem('balance');

document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;

document.getElementById("welcome").innerText = `Welcome ${username}`;

document.getElementById("balance").innerText = `Your Balance: $${balance}`;

document.getElementById('deposit').addEventListener('click', () => {
    const amount = parseFloat(document.getElementById("amount").value);
    if (isNaN(amount) || amount <= 0) {
        alert("Please enter a valid amount to deposit.");
        return;
    }
    fetch("/api/deposit", {
        method: "POST", 
         headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            amount: amount
        })
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.json();
    })
    .then(data => {
        if(!data.success) {
            alert(data.message || "Desposit Failed");
            return;
        }
        console.log("Deposit Success:", data);
        
        sessionStorage.setItem("balance", data.balance);
        document.getElementById("balance").innerText = `Your Balance: $${sessionStorage.getItem('balance') }`;
         // Clear the input field
        document.getElementById("amount").value = ""; // Clear the input field
        alert("Money Deposited");
    })
    .catch(error => {
        console.error("Error during deposit:", error);
        alert("Failed to deposit. Please try again.");
    });

});


    document.getElementById('withdraw').addEventListener('click', () => {
    const amount = parseFloat(document.getElementById("amount").value);
    if (isNaN(amount) || amount <= 0) {
        alert("Please enter a valid amount to withdraw.");
        return;
    }
    fetch("/api/withdraw", {
        method: "POST", 
         headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            amount: amount
        })
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.json();
    })
    .then(data => {
        if(!data.success) {
            alert(data.message || "Withdraw Failed");
            return;
        }
        console.log("Withdraw Success:", data);
        
        sessionStorage.setItem("balance", data.balance);
        document.getElementById("balance").innerText = `Your Balance: $${sessionStorage.getItem('balance') }`;
         // Clear the input field
        document.getElementById("amount").value = ""; // Clear the input field
        alert("Money Withdrawn");
    })
    .catch(error => {
        console.error("Error during Withdraw:", error);
        alert("Failed to Withdraw. Please try again.");
    });

})

 document.getElementById('logout').addEventListener('click', () => {
    sessionStorage.removeItem("CurrentUser");
    sessionStorage.removeItem("balance");
    window.location.href = "../HTML/Homepage.html";
})