
      document.getElementById("loginForm").addEventListener("submit", () => {
          e.preventDefault();
          fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              username: document.getElementById("username").value,
              password: document.getElementById("password").value,
            }),
          })
            .then((res) => {
              if (!res.ok) {
                throw new Error("Network response was not ok");
              }
              return res.json();
            })
            .then((data) => {
              document.getElementById("result").innerText = data.message;
              if (data.success) {
                // Optionally redirect or show logged-in UI
              }
            })
            .catch((err) => {
              document.getElementById("result").innerText =
                "Login failed or server error.";
              console.error(err);
            });
        });

        document.getElementById("Create-Account").addEventListener("click", () => {
            console.log("clicked");
            window.location.href = "/AccountCreation.html";
        })
