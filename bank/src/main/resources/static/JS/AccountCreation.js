document
  .getElementById("accountCreationForm")
  .addEventListener("submit", (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const loadingIcon = document.getElementsByClassName("loader");

    if (password !== confirmPassword) {
      document.getElementById("password").value = "";
      document.getElementById("confirmPassword").value = "";
      alert("Passwords do not match");
      return;
    }

    if (!username || !password || username.length < 3 || username.length > 50) {
      alert("Username must be between 3 and 50 characters");
      return;
    }

    const formData = {
      username: username,
      password: password,
    };

    loadingIcon[0].classList.remove("hidden");
    fetch("https://bank-7qbm.onrender.com/api/create-account", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
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
          alert(data.message || "Account creation failed");
          return;
        }

        console.log("Success:", data);
        loadingIcon[0].classList.add("hidden");
        alert("Account created successfully!");
        window.location.href = "../index.html";
      })
      .catch((error) => {
        loadingIcon[0].classList.add("hidden");
        console.error("Error:", error);
        alert("Failed to create account. Please try again.");
      });
  });
