
      const username = document.getElementById("username").value;
      const password = document.getElementById("password").value;

      document.getElementById("loginForm").addEventListener("submit", (e) => {
          e.preventDefault();
          console.log("Login submitted for:", username);
          
          fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              username: username,
              password: password
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
                console.log(data.username, data.balance);
                sessionStorage.setItem('currentUser', data.username);
                sessionStorage.setItem('balance', data.balance);
              }
            })
            .catch((err) => {
              document.getElementById("result").innerText =
                "Login failed or server error.";
              console.error(err);
            });
            window.location.href = "../HTML/Account.html";
        });

        document.getElementById("Create-Account").addEventListener("click", () => {
            console.log("clicked");
            window.location.href = "../HTML/AccountCreation.html";
        })
