let username = sessionStorage.getItem("CurrentUser");
let balance = sessionStorage.getItem("balance");

document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;

document.getElementById("welcome").innerText = `Welcome ${username}`;

document.getElementById("balance").innerText = `Your Balance: $${balance}`;

document.getElementById("deposit").addEventListener("click", () => {
  const amount = parseFloat(document.getElementById("amount").value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to deposit.");
    return;
  }
  fetch("/api/deposit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username,
      amount: amount,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        alert(data.message || "Desposit Failed");
        return;
      }
      console.log("Deposit Success:", data);

      sessionStorage.setItem("balance", data.balance);
      document.getElementById(
        "balance"
      ).innerText = `Your Balance: $${sessionStorage.getItem("balance")}`;
      // Clear the input field
      document.getElementById("amount").value = ""; // Clear the input field
      alert("Money Deposited");
    })
    .catch((error) => {
      console.error("Error during deposit:", error);
      alert("Failed to deposit. Please try again.");
    });
});

document.getElementById("withdraw").addEventListener("click", () => {
  const amount = parseFloat(document.getElementById("amount").value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to withdraw.");
    return;
  }
  fetch("/api/withdraw", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username,
      amount: amount,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        alert(data.message || "Withdraw Failed");
        return;
      }
      console.log("Withdraw Success:", data);

      sessionStorage.setItem("balance", data.balance);
      document.getElementById(
        "balance"
      ).innerText = `Your Balance: $${sessionStorage.getItem("balance")}`;
      // Clear the input field
      document.getElementById("amount").value = ""; // Clear the input field
      alert("Money Withdrawn");
    })
    .catch((error) => {
      console.error("Error during Withdraw:", error);
      alert("Failed to Withdraw. Please try again.");
    });
});

document.getElementById("transfer").addEventListener("click", () => {
  const input = document.getElementById("amount");

  const amount = parseFloat(input.value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to transfer.");
    return;
  }

  // Use prompt to get the account number and store the result
  const toAccountNum = prompt(
    "Enter the 9-digit account number you want to transfer to:"
  );

  if (!toAccountNum || toAccountNum.length !== 9 || isNaN(toAccountNum)) {
    alert("Please enter a valid 9-digit account number.");
    return;
  }

  fetch("/api/transfer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      fromAccountNum: sessionStorage.getItem("CurrentUser"),
      toAccountNum: parseInt(toAccountNum),
      amount: amount,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        alert(data.message || "Transfer Failed");
        return;
      }
      console.log("Transfer Success:", data);
      sessionStorage.setItem("balance", data.balance);
      document.getElementById(
        "balance"
      ).innerText = `Your Balance: $${sessionStorage.getItem("balance")}`;
      input.value = ""; // Clear the amount input
      alert("Money Transferred");
    })
    .catch((error) => {
      console.error("Error during transfer:", error);
      alert("Failed to transfer. Please try again.");
    });
});

document.getElementById("logout").addEventListener("click", () => {
  sessionStorage.removeItem("CurrentUser");
  sessionStorage.removeItem("balance");
  window.location.href = "../HTML/Homepage.html";
});
