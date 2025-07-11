let username = sessionStorage.getItem('CurrentUser');
let balance = sessionStorage.getItem('balance');

document.getElementsByTagName("title")[0].innerText = `Account - ${username}`;

document.getElementById("welcome").innerText = `Welcome ${username}`;

document.getElementById("balance").innerText = `Your balance is $${balance}`;

