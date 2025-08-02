document
  .getElementById("accountCreationForm")
  .addEventListener("submit", (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("password-retype").value;

    if (password !== passwordConfirm) {
      document.getElementById("password").value = "";
      document.getElementById("password-retype").value = "";
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

    fetch("/api/create-account", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (!data.success) {
          alert(data.message || "Account creation failed");
          return;
        }

        console.log("Success:", data);
        alert("Account created successfully!");
        window.location.href = "../HTML/index.html";
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Failed to create account. Please try again.aaaaaa");
      });
  });
