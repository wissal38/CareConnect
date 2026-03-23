// header.js: updates the header and greeting based on JWT token
async function updateHeaderAndGreeting() {
    const headerLink = document.getElementById('headerUserLink');
    if (!headerLink) return; // nothing to do
    // try several keys in localStorage to be robust
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    const token = localStorage.getItem('accessToken') || localStorage.getItem('token') || localStorage.getItem('jwt') || getCookie('ACCESS_TOKEN');
    // Quick fallback: try to show cached user data from localStorage immediately
    try {
        const cached = localStorage.getItem('user');
        if (cached) {
            const obj = JSON.parse(cached);
            const name = ((obj.firstName || '') + ' ' + (obj.lastName || '')).trim() || obj.username || 'Mon compte';
            headerLink.textContent = name;
            headerLink.href = '/compte';
            const nameEl = document.getElementById('userFullName');
            if (nameEl) nameEl.textContent = name;
            const cityEl = document.getElementById('userCity');
            if (cityEl && obj.city) cityEl.textContent = obj.city;
        }
    } catch (e) {
        // ignore parsing errors
    }
    console.debug('[header.js] found token (length):', token ? token.length : 0);
    // Try to get user info via Authorization header or HttpOnly cookie
    try {
        let res;
        if (token) {
            res = await fetch('/api/users/me', { headers: { 'Authorization': 'Bearer ' + token }, credentials: 'include' });
        } else {
            // No token in localStorage: try to call API without Authorization so cookie (HttpOnly) can be used
            res = await fetch('/api/users/me', { credentials: 'include' });
        }
        console.debug('[header.js] /api/users/me response status:', res.status);
        if (!res.ok) {
            headerLink.textContent = 'Se connecter';
            headerLink.href = '/connexion';
            removeLogoutLink();
            return;
        }
        const user = await res.json();
        // Cache user in localStorage for fast subsequent renders
        try {
            localStorage.setItem('user', JSON.stringify(user));
        } catch (e) {
            console.debug('[header.js] cannot cache user in localStorage', e);
        }
        const name = ((user.firstName || '') + ' ' + (user.lastName || '')).trim() || user.username || 'Mon compte';
        headerLink.textContent = name;
        headerLink.href = '/compte';
        const nameEl = document.getElementById('userFullName');
        if (nameEl) nameEl.textContent = name;
        const cityEl = document.getElementById('userCity');
        if (cityEl) cityEl.textContent = user.city || cityEl.textContent;
        addLogoutLink();
        // Add admin nav links if the user has ROLE_ADMIN
        try {
            // Roles can come back as strings (['ROLE_ADMIN']) or objects ({ name: 'ROLE_ADMIN' }) depending on serialization.
            const hasAdmin = Array.isArray(user.roles) && user.roles.some(r => {
                if (!r) return false;
                if (typeof r === 'string') return (r === 'ROLE_ADMIN' || r === 'ADMIN');
                if (typeof r === 'object') return (r.name === 'ROLE_ADMIN' || r.name === 'ADMIN');
                return false;
            });
            if (hasAdmin) {
                setAdminNav();
            } else {
                setDefaultNav();
            }
        } catch (err) {
            console.debug('[header.js] cannot check admin role', err);
        }
        console.debug('[header.js] header updated with user:', name);
    } catch (err) {
        console.error('[header.js] updateHeaderAndGreeting', err);
    }
}

function addLogoutLink() {
    if (document.getElementById('logoutLink')) return;
    // If server rendered logout link exists, simply ensure it has the click handler and don't add dynamic link
    const server = document.getElementById('serverLogoutLink');
    if (server) {
        // Make the server link visible and ensure it's wired up
        server.style.display = '';
        if (!server._logoutAttached) {
            server.addEventListener('click', (e) => {
                e.preventDefault();
                doLogout();
            });
            // set class to style properly
            if (!server.classList.contains('logout')) server.classList.add('logout');
            server._logoutAttached = true;
        }
        return;
    }
    const header = document.querySelector('header');
    const logout = document.createElement('a');
    logout.id = 'logoutLink';
    logout.href = '#';
    logout.textContent = 'Déconnexion';
    logout.className = 'btn btn-sm';
    logout.style.marginLeft = '12px';
    logout.style.color = '#ef4444';
    logout.style.fontWeight = '600';
    logout.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        // Remove cookie if present (ACCESS_TOKEN)
        // Request server to clear cookie (HttpOnly)
        try {
            fetch('/api/auth/signout', { method: 'POST', credentials: 'include' });
        } catch (err) {
            console.debug('Signout request failed', err);
        }
        // reload page to reflect logged-out UI
        window.location.href = '/';
    });
    const headerUser = document.getElementById('headerUserLink');
    if (headerUser && headerUser.parentNode) headerUser.parentNode.appendChild(logout);
    else if (header) header.appendChild(logout);
}

function removeLogoutLink() {
    const el = document.getElementById('logoutLink');
    if (el && el.parentNode) el.parentNode.removeChild(el);
    // Also remove serverRendered logout link event handlers if present (it will be left to server to hide after reload)
    const serverLink = document.getElementById('serverLogoutLink');
    if (serverLink) serverLink.onclick = null;
}

// Global logout handler: unified used by server-side link and JS link
async function doLogout() {
    console.debug('[header.js] doLogout started');
    // Remove local tokens early so UI updates quickly even if network fails
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
        try {
            const res = await fetch('/api/auth/signout', { method: 'POST', credentials: 'include' });
        if (res.ok) {
            console.debug('[header.js] signout succeeded', res.status);
        } else {
            console.debug('[header.js] signout returned', res.status);
        }
    } catch (err) {
        console.debug('[header.js] signout fetch failed', err);
    }
    // Redirect to home to show logged-out UI state
    try {
        window.location.href = '/';
    } catch (err) {
        console.error('[header.js] redirect failed', err);
    }
}

(function attachServerLogoutListener() {
    // Attach if the server-rendered logout link exists in DOM
    document.addEventListener('DOMContentLoaded', () => {
        const server = document.getElementById('serverLogoutLink');
        if (server) {
            server.addEventListener('click', (e) => {
                e.preventDefault();
                doLogout();
            });
        }
    });
})();

document.addEventListener('DOMContentLoaded', () => {
    updateHeaderAndGreeting();
    // Try a few times in case the token or DOM populates shortly after load
    setTimeout(updateHeaderAndGreeting, 1000);
    setTimeout(updateHeaderAndGreeting, 3000);
    // Also try once after 8s as a fallback
    setTimeout(updateHeaderAndGreeting, 8000);
});

// Expose for manual calls (optional)
window.updateHeaderAndGreeting = updateHeaderAndGreeting;

/*
 * Legacy functions removed — we now replace the nav via `setAdminNav` and `setDefaultNav`.
 */

// Keep original nav content so we can restore it on logout or when a non-admin logs in.
let originalNavHTML = null;
function captureOriginalNav() {
    const menu = document.getElementById('navbarMenu');
    if (menu && originalNavHTML == null) {
        originalNavHTML = menu.innerHTML;
    }
}

function setAdminNav() {
    const menu = document.getElementById('navbarMenu');
    if (!menu) return;
    captureOriginalNav();
    menu.innerHTML = `
        <ul class="navbar-nav">
            <li class="nav-item"><a class="nav-link" href="/admin/users">Gérer utilisateurs</a></li>
            <li class="nav-item"><a class="nav-link" href="/admin/publications">Gérer publications</a></li>
            <li class="nav-item"><a class="nav-link" href="/admin">Dashboard</a></li>
        </ul>`;
}

function setDefaultNav() {
    const menu = document.getElementById('navbarMenu');
    if (!menu) return;
    if (originalNavHTML !== null) menu.innerHTML = originalNavHTML;
}

// Navigation toggler
document.addEventListener('DOMContentLoaded', () => {
    // Capture the initial nav content for later restoration
    captureOriginalNav();
    const toggler = document.getElementById('navbarToggler');
    const menu = document.getElementById('navbarMenu');
    if (toggler && menu) {
        toggler.addEventListener('click', () => {
            menu.classList.toggle('active');
            toggler.classList.toggle('open');
        });
    }

    // User dropdown
    const userDropdownToggle = document.getElementById('userDropdownToggle');
    const userDropdown = document.querySelector('.user-dropdown');
    if (userDropdownToggle && userDropdown) {
        userDropdownToggle.addEventListener('click', (e) => {
            e.preventDefault();
            userDropdown.classList.toggle('open');
            userDropdownToggle.setAttribute('aria-expanded', userDropdown.classList.contains('open'));
        });
        // Close dropdown when clicking outside
        document.addEventListener('click', (ev) => {
            if (!userDropdown.contains(ev.target) && userDropdown.classList.contains('open')) {
                userDropdown.classList.remove('open');
                userDropdownToggle.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // Search toggler can be implemented later - placeholder now
    const searchToggler = document.getElementById('searchToggler');
    if (searchToggler) searchToggler.addEventListener('click', () => alert('Recherche non implémentée (placeholder)'));
});
