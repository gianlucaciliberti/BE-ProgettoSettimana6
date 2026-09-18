const Api = (() => {

    async function request(url, options = {}) {
        const response = await fetch(url, {
            credentials: 'same-origin',
            headers: { 'Content-Type': 'application/json' },
            ...options
        });

        if (response.status === 401) {
            location.href = '/login.html';
            throw new Error('Non autenticato');
        }

        if (!response.ok) {
            let message = 'Errore nella richiesta';
            try {
                const body = await response.json();
                message = body.message || message;
            } catch (_) { /* risposta senza body json */ }
            throw new Error(message);
        }

        if (response.status === 204 || response.status === 202) {
            return null;
        }
        return response.json();
    }

    return {
        register: (payload) => request('/api/users/register', { method: 'POST', body: JSON.stringify(payload) }),
        me: () => request('/api/users/me'),
        listUsers: () => request('/api/users'),
        listChats: () => request('/api/chats'),
        openChat: (otherUserId) => request('/api/chats', { method: 'POST', body: JSON.stringify({ otherUserId }) }),
        getMessages: (chatId) => request(`/api/chats/${chatId}/messages`),
        aiSuggest: (chatId) => request(`/api/chats/${chatId}/ai-suggest`, { method: 'POST' }),
        getStats: () => request('/api/stats/me'),
        emailStats: () => request('/api/stats/me/email', { method: 'POST' }),
        logout: () => fetch('/logout', { method: 'POST', credentials: 'same-origin' })
    };
})();
