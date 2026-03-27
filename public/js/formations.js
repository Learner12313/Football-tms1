// ===== FORMATIONS.JS - Formations & Tactics Logic =====

const API = window.location.origin;
let formations = [];
let currentFormation = null;

// ===== INITIALIZATION =====
document.addEventListener('DOMContentLoaded', async () => {
    await checkSession();
    initEventListeners();
    loadFormations();
});

// ===== SESSION CHECK =====
async function checkSession() {
    try {
        const res = await fetch(`${API}/session-check`);
        const data = await res.json();
        
        if (!data.loggedIn) {
            window.location.href = 'login.html';
            return;
        }
        
        // Check role - only Manager and Admin can access
        if (data.role === 'user') {
            alert('Access Denied. Users cannot view formations.');
            window.location.href = 'index.html';
            return;
        }
        
        updateUserDisplay(data);
    } catch (error) {
        console.error('Session check failed:', error);
        window.location.href = 'login.html';
    }
}

// ===== UPDATE USER DISPLAY =====
function updateUserDisplay(data) {
    const userInitial = document.getElementById('userInitial');
    const userName = document.getElementById('userName');
    const userRole = document.getElementById('userRole');
    
    if (userInitial && data.username) {
        userInitial.textContent = data.username.charAt(0).toUpperCase();
    }
    if (userName && data.username) {
        userName.textContent = data.username;
    }
    if (userRole && data.role) {
        userRole.textContent = data.role.charAt(0).toUpperCase() + data.role.slice(1);
    }
}

// ===== INIT EVENT LISTENERS =====
function initEventListeners() {
    document.getElementById('logoutBtn').addEventListener('click', logout);
    
    document.getElementById('sidebarToggle').addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('collapsed');
    });
}

// ===== LOGOUT =====
async function logout() {
    try {
        await fetch(`${API}/logout`);
        window.location.href = 'login.html';
    } catch (error) {
        console.error('Logout failed:', error);
    }
}

// ===== LOAD FORMATIONS =====
async function loadFormations() {
    showLoading();
    
    try {
        const res = await fetch(`${API}/formations`);
        const data = await res.json();
        
        formations = data.formations || [];
        
        if (formations.length > 0) {
            renderFormationTabs();
            selectFormation(formations[0].id);
        }
        
    } catch (error) {
        console.error('Error loading formations:', error);
    }
    
    hideLoading();
}

// ===== RENDER FORMATION TABS =====
function renderFormationTabs() {
    const tabsContainer = document.getElementById('formationTabs');
    
    tabsContainer.innerHTML = formations.map(f => `
        <button class="formation-tab ${f.id === currentFormation?.id ? 'active' : ''}" 
                onclick="selectFormation('${f.id}')">
            ${f.name}
        </button>
    `).join('');
}

// ===== SELECT FORMATION =====
function selectFormation(formationId) {
    currentFormation = formations.find(f => f.id === formationId);
    
    if (!currentFormation) return;
    
    // Update tabs
    document.querySelectorAll('.formation-tab').forEach(tab => {
        tab.classList.remove('active');
        if (tab.textContent.trim() === currentFormation.name) {
            tab.classList.add('active');
        }
    });
    
    // Render pitch
    renderFormation();
    
    // Update info
    updateFormationInfo();
}

// ===== RENDER FORMATION ON PITCH =====
function renderFormation() {
    const container = document.getElementById('playersContainer');
    
    container.innerHTML = currentFormation.positions.map((pos, index) => {
        // Determine position type for coloring
        let posType = 'midfielder';
        if (pos.code === 'GK') posType = 'gk';
        else if (['CB', 'LB', 'RB', 'LWB', 'RWB'].includes(pos.code)) posType = 'defender';
        else if (['CM', 'CDM', 'CAM', 'LM', 'RM'].includes(pos.code)) posType = 'midfielder';
        else if (['ST', 'LW', 'RW', 'CF'].includes(pos.code)) posType = 'attacker';
        
        return `
            <div class="player-marker ${posType}" 
                 style="left: ${pos.x}%; top: ${pos.y}%; animation-delay: ${index * 0.05}s"
                 title="${pos.name}">
                <div class="player-dot">${pos.code}</div>
                <div class="player-label">${pos.name}</div>
            </div>
        `;
    }).join('');
}

// ===== UPDATE FORMATION INFO =====
function updateFormationInfo() {
    // Update header
    document.getElementById('formationName').textContent = currentFormation.name;
    document.getElementById('formationDescription').textContent = currentFormation.description;
    
    // Update positions grid
    const positionsGrid = document.getElementById('positionsGrid');
    positionsGrid.innerHTML = currentFormation.positions.map(pos => `
        <div class="position-item">
            <div class="position-code">${pos.code}</div>
            <span class="position-name">${pos.name}</span>
        </div>
    `).join('');
    
    // Update tactics list
    const tacticsList = document.getElementById('tacticsList');
    tacticsList.innerHTML = currentFormation.tactics.map(tactic => `
        <li>${tactic}</li>
    `).join('');
}

// ===== LOADING =====
function showLoading() {
    document.getElementById('loadingOverlay').classList.add('active');
}

function hideLoading() {
    document.getElementById('loadingOverlay').classList.remove('active');
}

// Make selectFormation globally accessible
window.selectFormation = selectFormation;
