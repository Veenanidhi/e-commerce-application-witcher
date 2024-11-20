//document.addEventListener('DOMContentLoaded', function() {
//  const form = document.getElementById('form-id');
//
//  const username = document.getElementById('username');
//  const email = document.getElementById('email');
//  const password = document.getElementById('password');
//  const confirmPassword = document.getElementById('confirmPassword');
//  const number = document.getElementById('number');
//
//  const usernameError = document.getElementById('usernameError');
//  const emailError = document.getElementById('emailError');
//  const passwordError = document.getElementById('passwordError');
//  const confirmPasswordError = document.getElementById('confirmPasswordError');
//  const numberError = document.getElementById('numberError');
//
//  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
//  const passwordPattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
//
//  function validateForm() {
//      let isValid = true;
//
//      if (!username.value) {
//          usernameError.textContent = "Username is required.";
//          isValid = false;
//      } else {
//          usernameError.textContent = "";
//      }
//      if (!emailPattern.test(email.value)) {
//          emailError.textContent = "Invalid email format.";
//          isValid = false;
//      } else {
//          emailError.textContent = "";
//      }
//      if (!passwordPattern.test(password.value)) {
//          passwordError.textContent = "Password must be at least 8 characters long and include an uppercase letter, a number, and a special character.";
//          isValid = false;
//      } else {
//          passwordError.textContent = "";
//      }
//      if (password.value !== confirmPassword.value) {
//          confirmPasswordError.textContent = "Passwords do not match.";
//          isValid = false;
//      } else {
//          confirmPasswordError.textContent = "";
//      }
//      if (number.value.length > 0 && number.value.length !== 10) {
//          numberError.textContent = "Number is invalid.";
//          isValid = false;
//      } else {
//          numberError.textContent = "";
//      }
//
//      return isValid;
//  }
//
//  form.addEventListener('submit', function(event) {
//      if (!validateForm()) {
//          event.preventDefault(); // Prevent form submission if validation fails
//      }
//  });
//});

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-id');

    const username = document.getElementById('username');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const number = document.getElementById('number');

    const usernameError = document.getElementById('usernameError');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    const numberError = document.getElementById('numberError');

    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const passwordPattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    function validateForm() {
        let isValid = true;

        // Validate username
        if (!username.value) {
            usernameError.textContent = "Username is required.";
            isValid = false;
        } else {
            usernameError.textContent = "";
        }

        // Validate email
        if (!email.value) {
            emailError.textContent = "Email is required.";
            isValid = false;
        } else if (!emailPattern.test(email.value)) {
            emailError.textContent = "Invalid email format.";
            isValid = false;
        } else {
            emailError.textContent = "";
        }

        // Validate password
        if (!passwordPattern.test(password.value)) {
            passwordError.textContent = "Password must be at least 8 characters long and include an uppercase letter, a number, and a special character.";
            isValid = false;
        } else {
            passwordError.textContent = "";
        }

        // Validate confirm password
        if (password.value !== confirmPassword.value) {
            confirmPasswordError.textContent = "Passwords do not match.";
            isValid = false;
        } else {
            confirmPasswordError.textContent = "";
        }

        // Validate phone number
        if (number.value.length > 0 && number.value.length !== 10) {
            numberError.textContent = "Number is invalid.";
            isValid = false;
        } else {
            numberError.textContent = "";
        }

        return isValid;
    }

   form.addEventListener('submit', function(event) {
       if (!validateForm()) {
           event.preventDefault(); // Prevent form submission if validation fails
           console.log("Form validation failed. Submission stopped.");
       } else {
           console.log("Form validation succeeded. Proceeding with submission.");
       }
   });

});
