// admin.js: helper functions for admin pages (delete users/publications)

async function apiDelete(path) {
    try {
        // Try a few storage locations and cookie fallback (to match header.js behavior)
        function getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
            return null;
        }
        const token = localStorage.getItem('accessToken') || localStorage.getItem('token') || localStorage.getItem('jwt') || getCookie('ACCESS_TOKEN');
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = 'Bearer ' + token;
        const res = await fetch(path, { method: 'DELETE', headers, credentials: 'include' });
        return res;
    } catch (err) {
        console.error('apiDelete failed', err);
        throw err;
    }
}

async function deleteUser(id) {
    console.debug('deleteUser called for id', id);
    if (!confirm('Confirmer la suppression de cet utilisateur ?')) return;
    try {
        const res = await apiDelete('/api/users/' + id);
        console.debug('deleteUser status', res.status);
        if (res.status === 204) {
            const row = document.getElementById('userRow-' + id);
            if (row && row.parentNode) row.parentNode.removeChild(row);
            alert('Utilisateur supprimé');
        } else if (res.status === 404) {
            alert('Utilisateur non trouvé');
        } else if (res.status === 401 || res.status === 403) {
            // Provide more debugging details in case of permission issues
            const body = await res.text();
            console.debug('deleteUser unauthorized response text:', body);
            alert('Vous n\'avez pas les droits pour supprimer cet utilisateur');
        } else {
            const text = await res.text();
            alert('Erreur suppression utilisateur: ' + text);
        }
    } catch (err) {
        alert('Erreur réseau');
    }
}

async function blockUser(id) {
    if (!confirm('Confirmer le blocage de cet utilisateur ?')) return;
    try {
        const token = localStorage.getItem('accessToken');
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = 'Bearer ' + token;
        const res = await fetch('/api/users/' + id + '/block', { method: 'PUT', headers, credentials: 'include' });
        if (res.ok) {
            // Optionally mark the row as blocked or remove it
            const row = document.getElementById('userRow-' + id);
            if (row) {
                row.style.opacity = '0.6';
                // Add a small 'Bloqué' badge
                const td = row.querySelector('td:nth-child(4)');
                const span = document.createElement('span');
                span.className = 'text-muted';
                span.style.marginLeft = '8px';
                span.textContent = ' (Bloqué)';
                if (td && !td.querySelector('.blocked-badge')) {
                    span.classList.add('blocked-badge');
                    row.querySelector('td:nth-child(5)').insertBefore(span, row.querySelector('td:nth-child(5)').firstChild);
                }
                // swap block button to unblock
                const actionTd = row.querySelector('td:nth-child(5)');
                const blockBtn = actionTd.querySelector('.btn-primary');
                if (blockBtn) {
                    blockBtn.classList.remove('btn-primary');
                    blockBtn.classList.add('btn-secondary');
                    blockBtn.textContent = 'Débloquer';
                    blockBtn.onclick = function() { window.unblockUser(id); };
                }
            }
            alert('Utilisateur bloqué');
        } else if (res.status === 404) {
            alert('Utilisateur non trouvé');
        } else if (res.status === 401 || res.status === 403) {
            alert('Vous n\'avez pas les droits pour bloquer cet utilisateur');
        } else {
            const text = await res.text();
            alert('Erreur blocage utilisateur: ' + text);
        }
    } catch (err) {
        alert('Erreur réseau');
    }
}

async function deletePublication(id) {
    console.debug('deletePublication called for id', id);
    if (!confirm('Confirmer la suppression de cette publication ?')) return;
    try {
        const res = await apiDelete('/api/publications/' + id);
        console.debug('deletePublication response status', res.status);
        if (res.status === 204) {
            const row = document.getElementById('publicationRow-' + id);
            if (row && row.parentNode) row.parentNode.removeChild(row);
            alert('Publication supprimée');
        } else if (res.status === 404) {
            alert('Publication non trouvée');
        } else if (res.status === 401 || res.status === 403) {
            const body = await res.text();
            console.debug('deletePublication unauthorized response text:', body);
            alert('Vous n\'avez pas les droits pour supprimer cette publication');
        } else {
            const text = await res.text();
            alert('Erreur suppression publication: ' + text);
        }
    } catch (err) {
        alert('Erreur réseau');
    }
}

// Expose globally for markup inline to call
window.deleteUser = deleteUser;
window.deletePublication = deletePublication;
window.blockUser = blockUser;
window.unblockUser = async function(id) {
    if (!confirm('Confirmer le déblocage de cet utilisateur ?')) return;
    try {
        const token = localStorage.getItem('accessToken');
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = 'Bearer ' + token;
        const res = await fetch('/api/users/' + id + '/unblock', { method: 'PUT', headers, credentials: 'include' });
        if (res.ok) {
            const row = document.getElementById('userRow-' + id);
            if (row) {
                row.style.opacity = '1';
                // remove blocked badge if present
                const badge = row.querySelector('.blocked-badge');
                if (badge && badge.parentNode) badge.parentNode.removeChild(badge);
                const actionTd = row.querySelector('td:nth-child(5)');
                const unblockBtn = actionTd.querySelector('.btn-secondary');
                if (unblockBtn) {
                    unblockBtn.classList.remove('btn-secondary');
                    unblockBtn.classList.add('btn-primary');
                    unblockBtn.textContent = 'Bloquer';
                    unblockBtn.onclick = function() { window.blockUser(id); };
                }
            }
            alert('Utilisateur débloqué');
        } else {
            const text = await res.text();
            alert('Erreur déblocage utilisateur: ' + text);
        }
    } catch (err) {
        alert('Erreur réseau');
    }
};
