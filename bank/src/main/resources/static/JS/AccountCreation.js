document.getElementById("accountCreationForm").addEventListener("submit", (e) => {
    e.preventDefault();
    
    const formData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    };

    console.log("Form Data:", formData);

    fetch("/api/create-account", {
        method: "POST", 
         headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.json();
    })
    .then(data => {

        if(!data.success) {
            alert(data.message || "Account creation failed");
            return;
        }

        console.log("Success:", data);
        alert("Account created successfully!");
        window.location.href = "/HTML/Account.html"; // Redirect to login page
    })
  .catch(error => {
        console.error("Error:", error);
        alert("Failed to create account. Please try again.");
    });
});