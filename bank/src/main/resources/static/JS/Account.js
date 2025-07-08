let username = sessionStorage.getItem('currentUser');
let balance = sessionStorage.getItem('balance');

console.log("UsernameJS:", username);
console.log("BalanceJS:", balance);

document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;

document.getElementById("balance").innerText = `Your balance is $${balance}`;

