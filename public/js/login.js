// ===== LOGIN.JS - Login Page Logic =====

const API_BASE = window.location.origin;

let selectedRole = null;

// ===== CREATE PARTICLES =====
function createParticles() {
    const container = document.getElementById('particles');
    const particleCount = 50;
    
    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 10 + 's';
        particle.style.animationDuration = (Math.random() * 10 + 10) + 's';
        container.appendChild(particle);
    }
}

// ===== ROLE SELECTION =====
function selectRole(role) {
    selectedRole = role;
    
    // Update UI
    document.querySelectorAll('.role-card').forEach(card => {
        card.classList.remove('active');
    });
    
    const selectedCard = document.querySelector(`[data-role="${role}"]`);
    if (selectedCard) {
        selectedCard.classList.add('active');
    }
}

// ===== TOGGLE PASSWORD VISIBILITY =====
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleBtn = document.querySelector('.toggle-password');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.textContent = '🙈';
    } else {
        passwordInput.type = 'password';
        toggleBtn.textContent = '👁️';
    }
}

// ===== HANDLE LOGIN =====
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const loginBtn = document.getElementById('loginBtn');
    const errorMsg = document.getElementById('errorMessage');
    
    // Validate role selection
    if (!selectedRole) {
        showError('Please select a role (User, Admin, or Manager)');
        return;
    }
    
    // Clear previous errors
    errorMsg.classList.remove('show');
    errorMsg.textContent = '';
    
    // Show loading state
    loginBtn.classList.add('loading');
    loginBtn.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}&role=${encodeURIComponent(selectedRole)}`
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            // Login successful - redirect to main app
            window.location.href = 'index.html';
        } else {
            showError(data.error || 'Invalid credentials. Please try again.');
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Connection error. Please check if the server is running.');
    } finally {
        loginBtn.classList.remove('loading');
        loginBtn.disabled = false;
    }
}

// ===== SHOW ERROR =====
function showError(message) {
    const errorMsg = document.getElementById('errorMessage');
    errorMsg.textContent = message;
    errorMsg.classList.add('show');
}

// ===== CHECK IF ALREADY LOGGED IN =====
async function checkSession() {
    try {
        const response = await fetch(`${API_BASE}/session-check`);
        const data = await response.json();
        
        if (data.loggedIn) {
            window.location.href = 'index.html';
        }
    } catch (error) {
        console.log('Session check failed');
    }
}

// ===== INITIALIZE =====
document.addEventListener('DOMContentLoaded', function() {
    createParticles();
    checkSession();
    
    // Auto-select first role
    selectRole('user');
});
