// ===== APP.JS - Main Application JavaScript =====

const API = window.location.origin;

// ===== STATE =====
let currentPage = 'dashboard';
let selectedLeague = 'PL';
let sessionData = null;

// ===== LEAGUE INFO =====
const leagueInfo = {
    'PL': { name: 'Premier League', country: 'England', flag: '🏴󠁧󠁢󠁥󠁮󠁧󠁿' },
    'PD': { name: 'La Liga', country: 'Spain', flag: '🇪🇸' },
    'SA': { name: 'Serie A', country: 'Italy', flag: '🇮🇹' },
    'BL1': { name: 'Bundesliga', country: 'Germany', flag: '🇩🇪' },
    'FL1': { name: 'Ligue 1', country: 'France', flag: '🇫🇷' },
    'CL': { name: 'Champions League', country: 'Europe', flag: '🏆' },
    'ELC': { name: 'Championship', country: 'England', flag: '🏴󠁧󠁢󠁥󠁮󠁧󠁿' },
    'DED': { name: 'Eredivisie', country: 'Netherlands', flag: '🇳🇱' },
    'PPL': { name: 'Primeira Liga', country: 'Portugal', flag: '🇵🇹' },
    'BSA': { name: 'Brasileirão', country: 'Brazil', flag: '🇧🇷' },
    'EC': { name: 'Euro Championship', country: 'Europe', flag: '🇪🇺' },
    'WC': { name: 'World Cup', country: 'World', flag: '🌍' }
};

// ===== INITIALIZATION =====
document.addEventListener('DOMContentLoaded', async () => {
    await checkSession();
    initNavigation();
    initEventListeners();
    updateTime();
    setInterval(updateTime, 1000);
    loadDashboard();
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
        
        sessionData = data;
        updateUserDisplay(data);
        applyRoleBasedAccess(data.role);
    } catch (error) {
        console.error('Session check failed:', error);
        window.location.href = 'login.html';
    }
}

// ===== APPLY ROLE-BASED ACCESS =====
function applyRoleBasedAccess(role) {
    const navTournament = document.getElementById('navTournament');
    const navFormations = document.getElementById('navFormations');
    const toolsSection = document.getElementById('toolsSection');
    
    // Show tools section if any tool is available
    let showTools = false;
    
    // Create Tournament - User & Admin only
    if (role === 'user' || role === 'admin') {
        if (navTournament) {
            navTournament.style.display = 'flex';
            showTools = true;
        }
    }
    
    // Formations - Manager & Admin only
    if (role === 'manager' || role === 'admin') {
        if (navFormations) {
            navFormations.style.display = 'flex';
            showTools = true;
        }
    }
    
    // Admin has access to both
    if (role === 'admin') {
        if (navTournament) navTournament.style.display = 'flex';
        if (navFormations) navFormations.style.display = 'flex';
    }
    
    // Hide tools section if no tools available
    if (!showTools && toolsSection) {
        toolsSection.style.display = 'none';
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

// ===== INIT NAVIGATION =====
function initNavigation() {
    document.querySelectorAll('.nav-item[data-page]').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const page = item.dataset.page;
            if (page) navigateTo(page);
        });
    });
    
    document.querySelectorAll('.view-all').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = link.dataset.page;
            if (page) navigateTo(page);
        });
    });
}

// ===== NAVIGATE TO PAGE =====
function navigateTo(page) {
    currentPage = page;
    
    // Update nav items
    document.querySelectorAll('.nav-item[data-page]').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.page === page) {
            item.classList.add('active');
        }
    });
    
    // Update pages
    document.querySelectorAll('.page').forEach(p => {
        p.classList.remove('active');
    });
    
    const targetPage = document.getElementById(`${page}Page`);
    if (targetPage) {
        targetPage.classList.add('active');
    }
    
    // Update title
    updatePageTitle(page);
    
    // Load page content
    loadPageContent(page);
}

// ===== UPDATE PAGE TITLE =====
function updatePageTitle(page) {
    const titleEl = document.getElementById('pageTitle');
    const subtitleEl = document.getElementById('pageSubtitle');
    
    const titles = {
        dashboard: { title: 'Dashboard', subtitle: 'Overview of today\'s football action' },
        live: { title: 'Live Matches', subtitle: 'Currently in progress' },
        upcoming: { title: 'Upcoming Matches', subtitle: 'Scheduled fixtures' },
        recent: { title: 'Recent Results', subtitle: 'Completed matches' },
        teams: { title: 'All Teams', subtitle: 'Click on a team to view details' },
        standings: { title: 'League Standings', subtitle: 'Current table positions' },
        teamDetails: { title: 'Team Details', subtitle: 'Complete team information' }
    };
    
    if (titles[page]) {
        titleEl.textContent = titles[page].title;
        subtitleEl.textContent = titles[page].subtitle;
    }
}

// ===== LOAD PAGE CONTENT =====
function loadPageContent(page) {
    switch (page) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'live':
            loadLiveMatches();
            break;
        case 'upcoming':
            loadUpcomingMatches();
            break;
        case 'recent':
            loadRecentMatches();
            break;
        case 'teams':
            loadTeams();
            break;
        case 'standings':
            loadStandings();
            break;
    }
}

// ===== INIT EVENT LISTENERS =====
function initEventListeners() {
    // League selector
    document.getElementById('leagueSelect').addEventListener('change', (e) => {
        selectedLeague = e.target.value;
        loadPageContent(currentPage);
    });
    
    // Refresh button
    document.getElementById('refreshBtn').addEventListener('click', () => {
        loadPageContent(currentPage);
    });
    
    // Logout
    document.getElementById('logoutBtn').addEventListener('click', logout);
    
    // Sidebar toggle
    document.getElementById('sidebarToggle').addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('collapsed');
    });
}

// ===== UPDATE TIME =====
function updateTime() {
    const timeEl = document.getElementById('currentTime');
    const now = new Date();
    timeEl.textContent = now.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit',
        second: '2-digit'
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

// ===== SHOW/HIDE LOADING =====
function showLoading() {
    document.getElementById('loadingOverlay').classList.add('active');
}

function hideLoading() {
    document.getElementById('loadingOverlay').classList.remove('active');
}

// ===== FORMAT DATE TIME =====
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });
}

// ===== CREATE MATCH CARD =====
function createMatchCard(match, type = '') {
    const home = match.homeTeam?.name || 'TBD';
    const away = match.awayTeam?.name || 'TBD';
    const crestHome = match.homeTeam?.crest || '';
    const crestAway = match.awayTeam?.crest || '';
    const matchDateTime = formatDateTime(match.utcDate);
    const competition = leagueInfo[selectedLeague]?.name || selectedLeague;
    
    let score = '-- : --';
    let statusClass = 'upcoming';
    let statusText = 'Scheduled';
    
    const status = match.status?.toUpperCase();
    
    if (['LIVE', 'IN_PLAY', 'PAUSED', 'HALF_TIME'].includes(status)) {
        statusClass = 'live';
        statusText = 'LIVE';
        const h = match.score?.fullTime?.home ?? match.score?.halfTime?.home ?? 0;
        const a = match.score?.fullTime?.away ?? match.score?.halfTime?.away ?? 0;
        score = `${h} : ${a}`;
    } else if (['FINISHED', 'FULL_TIME', 'AWARDED'].includes(status)) {
        statusClass = 'finished';
        statusText = 'FT';
        const h = match.score?.fullTime?.home ?? 0;
        const a = match.score?.fullTime?.away ?? 0;
        score = `${h} : ${a}`;
    }
    
    if (type) statusClass = type;
    
    return `
        <div class="match-card ${statusClass}" onclick="viewMatchDetails(${match.id})">
            <div class="match-header">
                <span class="match-competition">${competition}</span>
                <span class="match-status ${statusClass}">${statusText}</span>
            </div>
            <div class="match-body">
                <div class="match-datetime">${matchDateTime}</div>
                <div class="match-teams">
                    <div class="team-info">
                        ${crestHome ? `<img src="${crestHome}" alt="${home}" class="team-crest" onerror="this.style.display='none'">` : '<div class="team-crest"></div>'}
                        <span class="team-name">${home}</span>
                    </div>
                    <div class="match-score">
                        <span class="score-value">${score}</span>
                        <span class="score-label">Score</span>
                    </div>
                    <div class="team-info">
                        ${crestAway ? `<img src="${crestAway}" alt="${away}" class="team-crest" onerror="this.style.display='none'">` : '<div class="team-crest"></div>'}
                        <span class="team-name">${away}</span>
                    </div>
                </div>
            </div>
            <div class="match-footer">
                <span class="match-venue">📍 ${match.venue || 'Venue TBA'}</span>
            </div>
        </div>
    `;
}

// ===== CREATE EMPTY STATE =====
function createEmptyState(icon, title, message) {
    return `
        <div class="empty-state">
            <div class="empty-state-icon">${icon}</div>
            <h3>${title}</h3>
            <p>${message}</p>
        </div>
    `;
}

// ===== LOAD DASHBOARD =====
async function loadDashboard() {
    showLoading();
    
    try {
        // Fetch matches for stats
        const res = await fetch(`${API}/live?league=${selectedLeague}`);
        const data = await res.json();
        
        const matches = data.matches || [];
        
        // Count by status
        const liveMatches = matches.filter(m => 
            ['LIVE', 'IN_PLAY', 'PAUSED', 'HALF_TIME'].includes(m.status?.toUpperCase())
        );
        const upcomingMatches = matches.filter(m => 
            ['SCHEDULED', 'TIMED'].includes(m.status?.toUpperCase())
        );
        const finishedMatches = matches.filter(m => 
            ['FINISHED', 'FULL_TIME'].includes(m.status?.toUpperCase())
        );
        
        // Update stats
        document.getElementById('statLive').textContent = liveMatches.length;
        document.getElementById('statUpcoming').textContent = upcomingMatches.length;
        document.getElementById('statRecent').textContent = finishedMatches.length;
        document.getElementById('liveBadge').textContent = liveMatches.length;
        
        // Display preview matches
        const dashboardLive = document.getElementById('dashboardLive');
        const dashboardUpcoming = document.getElementById('dashboardUpcoming');
        
        if (liveMatches.length > 0) {
            dashboardLive.innerHTML = liveMatches.slice(0, 4).map(m => createMatchCard(m, 'live')).join('');
        } else {
            dashboardLive.innerHTML = createEmptyState('😴', 'No Live Matches', 'Check back later for live action');
        }
        
        if (upcomingMatches.length > 0) {
            dashboardUpcoming.innerHTML = upcomingMatches.slice(0, 4).map(m => createMatchCard(m, 'upcoming')).join('');
        } else {
            dashboardUpcoming.innerHTML = createEmptyState('📅', 'No Upcoming Matches', 'No scheduled matches found');
        }
        
    } catch (error) {
        console.error('Error loading dashboard:', error);
        document.getElementById('dashboardLive').innerHTML = createEmptyState('⚠️', 'Error Loading', 'Failed to fetch matches');
    }
    
    hideLoading();
}

// ===== LOAD LIVE MATCHES =====
async function loadLiveMatches() {
    showLoading();
    const container = document.getElementById('liveMatches');
    
    try {
        const res = await fetch(`${API}/live?league=${selectedLeague}`);
        const data = await res.json();
        
        const matches = data.matches || [];
        const liveMatches = matches.filter(m => 
            ['LIVE', 'IN_PLAY', 'PAUSED', 'HALF_TIME'].includes(m.status?.toUpperCase())
        );
        
        if (liveMatches.length > 0) {
            container.innerHTML = liveMatches.map(m => createMatchCard(m, 'live')).join('');
        } else {
            container.innerHTML = createEmptyState('🔴', 'No Live Matches', 'There are no matches in progress right now. Check back soon!');
        }
        
    } catch (error) {
        console.error('Error loading live matches:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load live matches');
    }
    
    hideLoading();
}

// ===== LOAD UPCOMING MATCHES =====
async function loadUpcomingMatches() {
    showLoading();
    const container = document.getElementById('upcomingMatches');
    
    try {
        const res = await fetch(`${API}/live?league=${selectedLeague}&status=SCHEDULED`);
        const data = await res.json();
        
        const matches = data.matches || [];
        const upcomingMatches = matches.filter(m => 
            ['SCHEDULED', 'TIMED', 'POSTPONED'].includes(m.status?.toUpperCase())
        );
        
        if (upcomingMatches.length > 0) {
            container.innerHTML = upcomingMatches.slice(0, 20).map(m => createMatchCard(m, 'upcoming')).join('');
        } else {
            container.innerHTML = createEmptyState('📅', 'No Upcoming Matches', 'No scheduled matches found');
        }
        
    } catch (error) {
        console.error('Error loading upcoming matches:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load upcoming matches');
    }
    
    hideLoading();
}

// ===== LOAD RECENT MATCHES =====
async function loadRecentMatches() {
    showLoading();
    const container = document.getElementById('recentMatches');
    
    try {
        const res = await fetch(`${API}/live?league=${selectedLeague}&status=FINISHED`);
        const data = await res.json();
        
        const matches = data.matches || [];
        const recentMatches = matches.filter(m => 
            ['FINISHED', 'FULL_TIME', 'AWARDED'].includes(m.status?.toUpperCase())
        );
        
        // Sort by date, newest first
        recentMatches.sort((a, b) => new Date(b.utcDate) - new Date(a.utcDate));
        
        if (recentMatches.length > 0) {
            container.innerHTML = recentMatches.slice(0, 20).map(m => createMatchCard(m, 'finished')).join('');
        } else {
            container.innerHTML = createEmptyState('✅', 'No Recent Results', 'No completed matches found');
        }
        
    } catch (error) {
        console.error('Error loading recent matches:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load recent matches');
    }
    
    hideLoading();
}

// ===== LOAD TEAMS =====
async function loadTeams() {
    showLoading();
    const container = document.getElementById('teamsList');
    
    try {
        const res = await fetch(`${API}/teams?league=${selectedLeague}`);
        const data = await res.json();
        
        const teams = data.teams || [];
        
        // Update stats
        document.getElementById('statTeams').textContent = teams.length;
        
        if (teams.length > 0) {
            container.innerHTML = teams.map(team => `
                <div class="team-card" onclick="loadTeamDetails(${team.id})">
                    ${team.crest ? `<img src="${team.crest}" alt="${team.name}" class="team-card-crest" onerror="this.style.display='none'">` : '<div class="team-card-crest"></div>'}
                    <div class="team-card-name">${team.name}</div>
                    <div class="team-card-area">${team.area?.name || ''}</div>
                </div>
            `).join('');
        } else {
            container.innerHTML = createEmptyState('👥', 'No Teams Found', 'No teams data available');
        }
        
    } catch (error) {
        console.error('Error loading teams:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load teams');
    }
    
    hideLoading();
}

// ===== LOAD TEAM DETAILS =====
async function loadTeamDetails(teamId) {
    showLoading();
    
    // Navigate to team details page
    navigateTo('teamDetails');
    
    const container = document.getElementById('teamDetailsContent');
    
    try {
        const res = await fetch(`${API}/teams/${teamId}`);
        const data = await res.json();
        
        const squad = data.squad || [];
        const coach = data.coach || {};
        
        // Group squad by position
        const positions = {
            'Goalkeeper': [],
            'Defender': [],
            'Midfielder': [],
            'Attacker': []
        };
        
        squad.forEach(player => {
            const pos = player.position;
            if (positions[pos]) {
                positions[pos].push(player);
            }
        });
        
        container.innerHTML = `
            <div class="team-details-header">
                ${data.crest ? `<img src="${data.crest}" alt="${data.name}" class="team-details-crest" onerror="this.style.display='none'">` : ''}
                <div class="team-details-info">
                    <h1>${data.name}</h1>
                    <div class="team-details-meta">
                        <div class="meta-item">
                            <span>🏴</span>
                            <span>${data.area?.name || 'N/A'}</span>
                        </div>
                        <div class="meta-item">
                            <span>📅</span>
                            <span>Founded: ${data.founded || 'N/A'}</span>
                        </div>
                        <div class="meta-item">
                            <span>🎨</span>
                            <span>${data.clubColors || 'N/A'}</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- PROMINENT VENUE CARD -->
            <div class="venue-card">
                <h2>🏟️ STADIUM / VENUE</h2>
                <div class="venue-name">${data.venue || 'Stadium information not available'}</div>
                <div class="venue-location">
                    <span>📍</span>
                    <span>${data.address || data.area?.name || 'Location not specified'}</span>
                </div>
            </div>
            
            <div class="team-details-grid">
                <div class="detail-card">
                    <h3>🌐 Official Website</h3>
                    <p>
                        ${data.website ? `<a href="${data.website}" target="_blank">${data.website}</a>` : 'Not available'}
                    </p>
                </div>
                
                <div class="detail-card">
                    <h3>👨‍💼 Head Coach</h3>
                    <div class="coach-card">
                        <div class="coach-avatar">👤</div>
                        <div>
                            <div class="coach-name">${coach.name || 'Not available'}</div>
                            <div class="coach-role">${coach.nationality || ''}</div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="squad-section">
                <h3>👥 Squad (${squad.length} players)</h3>
                <table class="squad-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Position</th>
                            <th>Nationality</th>
                            <th>Shirt Number</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${squad.map(player => `
                            <tr>
                                <td>${player.name}</td>
                                <td>
                                    <span class="position-badge ${(player.position || '').toLowerCase()}">${player.position || 'N/A'}</span>
                                </td>
                                <td>${player.nationality || 'N/A'}</td>
                                <td>${player.shirtNumber || '-'}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
        
        // Update page title
        document.getElementById('pageTitle').textContent = data.name;
        document.getElementById('pageSubtitle').textContent = `Founded ${data.founded || 'Unknown'} • ${data.area?.name || ''}`;
        
    } catch (error) {
        console.error('Error loading team details:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load team details');
    }
    
    hideLoading();
}

// ===== VIEW MATCH DETAILS =====
function viewMatchDetails(matchId) {
    // For now, just log - could navigate to a match details page
    console.log('View match:', matchId);
}

// ===== LOAD STANDINGS =====
async function loadStandings() {
    showLoading();
    const container = document.getElementById('standingsTable');
    
    try {
        const res = await fetch(`${API}/standings?league=${selectedLeague}`);
        const data = await res.json();
        
        const standings = data.standings || [];
        const tableStandings = standings.find(s => s.type === 'TOTAL') || standings[0];
        
        if (tableStandings && tableStandings.table) {
            const table = tableStandings.table;
            
            container.innerHTML = `
                <table class="standings-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Team</th>
                            <th>P</th>
                            <th>W</th>
                            <th>D</th>
                            <th>L</th>
                            <th>GF</th>
                            <th>GA</th>
                            <th>GD</th>
                            <th>Pts</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${table.map((team, index) => {
                            const posClass = index < 4 ? 'top' : (index >= table.length - 3 ? 'bottom' : 'mid');
                            const rowClass = index < 4 ? 'top-4' : (index >= table.length - 3 ? 'relegation' : '');
                            return `
                                <tr class="${rowClass}" onclick="loadTeamDetails(${team.team.id})">
                                    <td><span class="position-cell ${posClass}">${team.position}</span></td>
                                    <td>
                                        <div class="team-cell">
                                            ${team.team.crest ? `<img src="${team.team.crest}" alt="${team.team.name}" onerror="this.style.display='none'">` : ''}
                                            <span>${team.team.name}</span>
                                        </div>
                                    </td>
                                    <td>${team.playedGames}</td>
                                    <td>${team.won}</td>
                                    <td>${team.draw}</td>
                                    <td>${team.lost}</td>
                                    <td>${team.goalsFor}</td>
                                    <td>${team.goalsAgainst}</td>
                                    <td>${team.goalDifference > 0 ? '+' : ''}${team.goalDifference}</td>
                                    <td class="points-cell">${team.points}</td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            `;
        } else {
            container.innerHTML = createEmptyState('📊', 'No Standings', 'No standings data available');
        }
        
    } catch (error) {
        console.error('Error loading standings:', error);
        container.innerHTML = createEmptyState('⚠️', 'Error', 'Failed to load standings');
    }
    
    hideLoading();
}

// ===== EXPOSE GLOBAL FUNCTIONS =====
window.loadTeamDetails = loadTeamDetails;
window.viewMatchDetails = viewMatchDetails;
