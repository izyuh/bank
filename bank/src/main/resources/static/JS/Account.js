let username = "";
let balance = 0;
let accountNumber = "";

const loadingIcon = document.getElementsByClassName("loader");
const amountInput = document.getElementById("amount");

// Fetch account data from backend on page load
fetch("https://bank-7qbm.onrender.com/api/account", {
  method: "GET",
  credentials: "include", 
  headers: {
    "Content-Type": "application/json",
  }
})
.then((response) => {
  if (!response.ok) {
    throw new Error("Network response was not ok");
  }
  return response.json();
})
.then((data) => {
  if (data.success) {
    username = data.username;
    balance = data.balance;
    accountNumber = data.accountNumber;

    console.log("Fetched account data:", data); //testing purposes
    
    // Update UI with fetched data
    document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;
    document.getElementById("welcome").innerText = `Welcome ${username}`;
    document.getElementById("account-balance").innerHTML = `Your Balance: <br> ${addBreaksAfterCommas(balance)}`;
    document.getElementById("accountNumber").innerHTML = `Account Number: <b>${accountNumber}</b>`;
  } else {
    alert("Session expired. Please login again.");
    window.location.replace("../index.html");
  }
})
.catch((error) => {
  console.error("Error fetching account data:", error);
  alert("Failed to load account data. Please login again.");
  window.location.replace("../index.html");
});

amountInput.value = numberWithCommas(amountInput.value);


function numberWithCommas(x) {
    x = x.toString();
    var pattern = /(-?\d+)(\d{3})/;
    while (pattern.test(x))
        x = x.replace(pattern, "$1,$2");
    return x;
}


/////////// ALL BANKING FUNCTIONS ///////////
document.getElementById("deposit").addEventListener("click", () => {
  disableButtons();
  const amount = parseFloat(amountInput.value); 
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to deposit.");
    loadingIcon[0].classList.add("hidden");
    return;
  }
  fetch("https://bank-7qbm.onrender.com/api/deposit", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      amount: amount,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      loadingIcon[0].classList.add("hidden");
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        alert(data.message || "Desposit Failed");
        loadingIcon[0].classList.add("hidden");
        return;
      }
      console.log("Deposit Success:", data);

      balance = data.balance;
      document.getElementById(
        "account-balance"
      ).innerHTML = `Your Balance: <br> ${addBreaksAfterCommas(data.balance)}`;

      document.getElementById("amount").value = "";
      loadingIcon[0].classList.add("hidden");
      alert("Money Deposited");
      enableButtons();
    })
    .catch((error) => {
      console.error("Error during deposit:", error);
      loadingIcon[0].classList.add("hidden");
      alert("Failed to deposit. Please try again.");
      enableButtons();
    });

});

document.getElementById("withdraw").addEventListener("click", () => {
  disableButtons();
  loadingIcon[0].classList.remove("hidden");
  const amount = parseFloat(amountInput.value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to withdraw.");
    return;
  }
  loadingIcon[0].classList.remove("hidden");
  fetch("https://bank-7qbm.onrender.com/api/withdraw", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      amount: amount,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        loadingIcon[0].classList.add("hidden");
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        loadingIcon[0].classList.add("hidden");
        alert(data.message || "Withdraw Failed");
        return;
      }
      console.log("Withdraw Success:", data);

      balance = data.balance;
      document.getElementById(
        "account-balance"
      ).innerHTML = `Your Balance: <br> ${addBreaksAfterCommas(data.balance)}`;


      loadingIcon[0].classList.add("hidden");
      document.getElementById("amount").value = "";
      alert("Money Withdrawn");
      enableButtons();
    })
    .catch((error) => {
      console.error("Error during Withdraw:", error);
      loadingIcon[0].classList.add("hidden");
      alert("Failed to Withdraw. Please try again.");
      enableButtons();
    });
});

// Account number will be set when account data is fetched

document.getElementById("transfer").addEventListener("click", () => {
  disableButtons();
  const amount = parseFloat(amountInput.value);
  if (
    isNaN(amount) ||
    amount <= 0 ||
    amount > balance
  ) {
    alert("Please enter a valid amount to transfer.");
    loadingIcon[0].classList.add("hidden");
    return;
  }

  const toAccountNum = prompt(
    "Enter the 9 character account you want to transfer to:"
  );

  if (!toAccountNum || toAccountNum.length !== 9) {
    alert("Please enter a valid 9-digit account number.");
    amountInput.value = ""; 
    return;
  }

  loadingIcon[0].classList.remove("hidden");
  fetch("https://bank-7qbm.onrender.com/api/transfer", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      toAccountNumber: toAccountNum,
      amount: amount.toString(),
    }),
  })
    .then((response) => {
      if (!response.ok) {
        loadingIcon[0].classList.add("hidden");
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      if (!data.success) {
        loadingIcon[0].classList.add("hidden");
        alert(data.message || "Transfer Failed");
        return;
      }
      loadingIcon[0].classList.add("hidden");
      console.log("Transfer Success:", data);
      balance = data.balance;
      document.getElementById(
        "account-balance"
      ).innerHTML = `Your Balance: <br> ${addBreaksAfterCommas(data.balance)}`;
      amountInput.value = "";
      alert("Money Transferred");
      enableButtons();
    })
    .catch((error) => {
      console.error("Error during transfer:", error);
      loadingIcon[0].classList.add("hidden");
      alert("Failed to transfer. Please try again.");
      enableButtons();
    });
});

document.getElementById("logout").addEventListener("click", () => {
  loadingIcon[0].classList.remove("hidden");
  fetch("https://bank-7qbm.onrender.com/api/logout", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    }
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      console.log("Logout successful");
      window.location.href = "../index.html";
    })
    .catch((error) => {
      console.error("Error during logout:", error);
    });
  loadingIcon[0].classList.add("hidden");
  window.location.replace("../index.html");
});


// formats to us currency and then adds breaks after commas for wrapping purposes
function addBreaksAfterCommas(text) {
  return parseFloat(text).toLocaleString("en-US", {style: "currency", currency: "USD",}).replace(/,/g, ',&#8203;');
}

function disableButtons() {
  document.getElementById("deposit").disabled = true;
  document.getElementById("withdraw").disabled = true;
  document.getElementById("transfer").disabled = true;
  amountInput.disabled = true;
}

function enableButtons() {
  document.getElementById("deposit").disabled = false;
  document.getElementById("withdraw").disabled = false;
  document.getElementById("transfer").disabled = false;
  amountInput.disabled = false;
}
