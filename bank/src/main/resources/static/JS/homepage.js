document.getElementById("loginForm").addEventListener("submit", (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  console.log("Login submitted for:", username);

  fetch("/api/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: username,
      password: password,
    }),
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("Network response was not ok");
      }
      return res.json();
    })
    .then((data) => {
      if (!data.success) {
        const resultElement = document.getElementById("result");
        resultElement.style.visibility = "visible";
        resultElement.innerText = data.message;
      } else if (data.success) {
        sessionStorage.setItem("CurrentUser", data.username);
        sessionStorage.setItem("balance", data.balance);
        sessionStorage.setItem("accountNumber", data.accountNumber);

        document.getElementById("username").value = "";
        document.getElementById("password").value = "";

        window.location.href = "../HTML/Account.html";
      }
    })
    .catch((err) => {
      const resultElement = document.getElementById("result");
      resultElement.style.visibility = "visible";
      resultElement.innerText = "Login failed or server error";
      resultElement.style.backgroundColor = "#f0f0f063";

      console.error(err);
    });
});

document.getElementById("Create-Account").addEventListener("click", () => {
  console.log("clicked");
  window.location.href = "../HTML/AccountCreation.html";
});
