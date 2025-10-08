// Check if user is already logged in
fetch("https://bank-7qbm.onrender.com/api/account", {
  method: "GET",
  credentials: "include",
  headers: {
    "Content-Type": "application/json",
  }
})
.then((response) => {
  if (response.ok) {
    return response.json();
  }
})
.then((data) => {
  if (data && data.success) {
    // User is already logged in, redirect to account page
    window.location.href = "HTML/Account.html";
  }
})
.catch(() => {
  // Not logged in, stay on login page
});

document.getElementById("loginForm").addEventListener("submit", (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  const loadingIcon = document.getElementsByClassName("loader");

  loadingIcon[0].classList.remove("hidden");

  fetch("https://bank-7qbm.onrender.com/api/login", {
    method: "POST",
    credentials: "include", 
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: username,
      password: password,
    }),
  })
    .then((res) => {
      if (!res.ok) {
        loadingIcon[0].classList.add("hidden");
        throw new Error("Network response was not ok");
      }
      return res.json();
    })
    .then((data) => {
      loadingIcon[0].classList.add("hidden");
      if (!data.success) {
        const resultElement = document.getElementById("result");
        resultElement.style.visibility = "visible";
        resultElement.innerText = data.message;
      } else if (data.success) {
        document.getElementById("username").value = "";
        document.getElementById("password").value = "";

        window.location.href = "HTML/Account.html";
      }
    })
    .catch((err) => {
      loadingIcon[0].classList.add("hidden");
      const resultElement = document.getElementById("result");
      resultElement.style.visibility = "visible";
      resultElement.innerText = "Login failed or server error";
      resultElement.style.backgroundColor = "#f0f0f063";

      console.error(err);
    });
});

document.getElementById("Create-Account").addEventListener("click", () => {
  console.log("clicked");
  window.location.href = "HTML/AccountCreation.html";
});
