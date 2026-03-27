const API = window.location.origin;

document.addEventListener('DOMContentLoaded', async () => {
    await checkSession();
    initEvents();
    updateTeamInputs();
    loadTournaments();
});

async function checkSession() {
    try {
        const res = await fetch(`${API}/session-check`);
        const data = await res.json();
        if (!data.loggedIn) { window.location.href = 'login.html'; return; }
        if (data.role === 'manager') { alert('Access Denied'); window.location.href = 'index.html'; return; }
        document.getElementById('userInitial').textContent = data.username.charAt(0).toUpperCase();
        document.getElementById('userName').textContent = data.username;
        document.getElementById('userRole').textContent = data.role;
    } catch (e) { window.location.href = 'login.html'; }
}

function initEvents() {
    document.getElementById('logoutBtn').onclick = async () => {
        await fetch(`${API}/logout`);
        window.location.href = 'login.html';
    };
    document.getElementById('tournamentForm').onsubmit = createTournament;
    document.getElementById('teamsCount').onchange = updateTeamInputs;
}

function updateTeamInputs() {
    const n = parseInt(document.getElementById('teamsCount').value);
    document.getElementById('teamsInputGrid').innerHTML = 
        Array.from({length: n}, (_, i) => `<input type="text" class="team-input" placeholder="Team ${i+1}" required>`).join('');
}

async function createTournament(e) {
    e.preventDefault();
    const name = document.getElementById('tournamentName').value.trim();
    const teamsCount = document.getElementById('teamsCount').value;
    const format = document.getElementById('format').value;
    
    const inputs = document.querySelectorAll('.team-input');
    const teams = Array.from(inputs).map(i => i.value.trim()).filter(t => t);
    if (teams.length < parseInt(teamsCount)) { alert('Enter all team names'); return; }
    
    try {
        const res = await fetch(`${API}/tournament`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name, teamsCount, format, teams})
        });
        const data = await res.json();
        if (res.ok && data.success) {
            alert('Tournament created!');
            document.getElementById('tournamentForm').reset();
            updateTeamInputs();
            loadTournaments();
        } else {
            alert('Error: ' + (data.error || 'Failed'));
        }
    } catch (e) { alert('Connection error: ' + e.message); }
}

async function loadTournaments() {
    const grid = document.getElementById('tournamentsGrid');
    try {
        const res = await fetch(`${API}/tournament`);
        const list = await res.json();
        
        if (!list || list.length === 0) {
            grid.innerHTML = '<div class="empty-tournaments"><h3>No Tournaments</h3></div>';
            return;
        }
        
        grid.innerHTML = list.map(t => `
            <div class="tournament-card">
                <div class="tournament-card-header">
                    <span class="tournament-card-name">${t.name}</span>
                    <span class="tournament-card-format">${t.format}</span>
                </div>
                <div class="tournament-card-stats">
                    <div class="tournament-stat">
                        <span class="tournament-stat-value">${t.teams_count}</span>
                        <span class="tournament-stat-label">Teams</span>
                    </div>
                </div>
                <div class="tournament-card-footer">
                    <span>By ${t.created_by}</span>
                    <button class="tournament-btn view" onclick="viewTournament(${t.id})">View</button>
                    <button class="tournament-btn delete" onclick="deleteTournament(${t.id})">X</button>
                </div>
            </div>
        `).join('');
    } catch (e) { grid.innerHTML = '<p style="color:red">Error loading</p>'; }
}

async function viewTournament(id) {
    try {
        const res = await fetch(`${API}/tournament/${id}`);
        const t = await res.json();
        
        let fixturesHtml = '<p style="padding:20px;text-align:center;color:#888">No fixtures</p>';
        if (t.fixtures && t.fixtures.length > 0) {
            fixturesHtml = '<table class="fixtures-table"><thead><tr><th>#</th><th>Home</th><th>Away</th></tr></thead><tbody>' +
                t.fixtures.map(f => `<tr><td>${f.round_number}</td><td>${f.home_team}</td><td>${f.away_team}</td></tr>`).join('') +
                '</tbody></table>';
        }
        
        const modal = document.createElement('div');
        modal.className = 'tournament-modal active';
        modal.innerHTML = `
            <div class="tournament-modal-content">
                <div class="tournament-modal-header">
                    <h2>${t.name}</h2>
                    <button onclick="this.closest('.tournament-modal').remove()">X</button>
                </div>
                <div class="tournament-modal-body">
                    <p>${t.teams_count} Teams | ${t.format} | By ${t.created_by}</p>
                    <h3>Fixtures</h3>
                    ${fixturesHtml}
                </div>
            </div>`;
        document.body.appendChild(modal);
        modal.onclick = e => { if (e.target === modal) modal.remove(); };
    } catch (e) { alert('Error loading tournament: ' + e.message); }
}

async function deleteTournament(id) {
    if (!confirm('Delete?')) return;
    const res = await fetch(`${API}/tournament/${id}`, {method: 'DELETE'});
    const data = await res.json();
    if (data.success) { alert('Deleted'); loadTournaments(); }
}

window.viewTournament = viewTournament;
window.deleteTournament = deleteTournament;