let username = sessionStorage.getItem("CurrentUser");
let balance = sessionStorage.getItem("balance");

document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;

document.getElementById("welcome").innerText = `Welcome ${username}`;

document.getElementById("balance").innerText = `Your Balance: ${parseFloat(
  balance
).toLocaleString("en-US", { style: "currency", currency: "USD" })}`;

document.getElementById("deposit").addEventListener("click", () => {
  const amount = parseFloat(document.getElementById("amount").value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to deposit.");
    return;
  }
  fetch("https://bank-7qbm.onrender.com/api/deposit", {
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
      ).innerText = `Your Balance: ${parseFloat(data.balance).toLocaleString(
        "en-US",
        { style: "currency", currency: "USD" }
      )}`;
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
  fetch("https://bank-7qbm.onrender.com/api/withdraw", {
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
      ).innerText = `Your Balance: ${parseFloat(data.balance).toLocaleString(
        "en-US",
        { style: "currency", currency: "USD" }
      )}`;
      // Clear the input field
      document.getElementById("amount").value = ""; // Clear the input field
      alert("Money Withdrawn");
    })
    .catch((error) => {
      console.error("Error during Withdraw:", error);
      alert("Failed to Withdraw. Please try again.");
    });
});

document.getElementById(
  "accountNumber"
).innerHTML = `Account Number: <b>${sessionStorage.getItem(
  "accountNumber"
)}</b>`;

document.getElementById("transfer").addEventListener("click", () => {
  const input = document.getElementById("amount");

  const amount = parseFloat(input.value);
  if (
    isNaN(amount) ||
    amount <= 0 ||
    amount > sessionStorage.getItem("balance")
  ) {
    alert("Please enter a valid amount to transfer.");
    return;
  }

  // Use prompt to get the account number and store the result
  const toAccountNum = prompt(
    "Enter the 9 character account you want to transfer to:"
  );

  if (!toAccountNum || toAccountNum.length !== 9) {
    alert("Please enter a valid 9-digit account number.");
    input.value = ""; // Clear the amount input
    return;
  }

  fetch("https://bank-7qbm.onrender.com/api/transfer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      fromAccountNum: sessionStorage.getItem("accountNumber"),
      toAccountNum: toAccountNum,
      amount: amount.toString(),
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
      ).innerText = `Your Balance: ${parseFloat(data.balance).toLocaleString(
        "en-US",
        { style: "currency", currency: "USD" }
      )}`;
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
  sessionStorage.removeItem("accountNumber");
  window.location.replace("../index.html");
});