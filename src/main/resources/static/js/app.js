let currentUser = null;
let chats = [];
let selectedChatId = null;
let selectedChatName = '';
let selectedChatColor = null;

const CHAT_COLORS = [
    { base: '#1d4ed8', soft: '#dbe4fb' }, // blu intenso
    { base: '#06b6d4', soft: '#d6f5fa' }, // ciano
    { base: '#6366f1', soft: '#e3e4fd' }  // indaco/violaceo
];

function colorForUser(user) {
    const seed = String(user.id != null ? user.id : user.username || '');
    let hash = 0;
    for (let i = 0; i < seed.length; i++) {
        hash = (hash * 31 + seed.charCodeAt(i)) % 1000;
    }
    return CHAT_COLORS[hash % CHAT_COLORS.length];
}

async function init() {
    try {
        currentUser = await Api.me();
    } catch (e) {
        return;
    }
    document.getElementById('current-user-name').textContent = currentUser.username;

    await loadChats();
    ChatSocket.connect(onWsMessage);
    wireEvents();
}

async function loadChats() {
    chats = await Api.listChats();
    renderChatList();
}

function renderChatList() {
    const list = document.getElementById('chat-list');
    list.innerHTML = '';
    chats.forEach(chat => {
        const item = document.createElement('div');
        item.className = 'chat-list-item' + (chat.id === selectedChatId ? ' active' : '');
        item.innerHTML = `
            <div class="avatar" style="background:${colorForUser(chat.otherUser).base}">${initials(chat.otherUser.username)}</div>
            <div class="chat-list-item-text">
                <div class="chat-list-item-name">${escapeHtml(chat.otherUser.username)}</div>
                <div class="chat-list-item-preview">${escapeHtml(chat.lastMessagePreview || 'Nessun messaggio ancora')}</div>
            </div>
        `;
        item.addEventListener('click', () => selectChat(chat.id, chat.otherUser.username));
        list.appendChild(item);
    });
}

function initials(name) {
    return name.split(' ').filter(Boolean).slice(0, 2).map(p => p[0].toUpperCase()).join('');
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

async function selectChat(chatId, otherName) {
    selectedChatId = chatId;
    selectedChatName = otherName;
    const chat = chats.find(c => c.id === chatId);
    selectedChatColor = colorForUser(chat ? chat.otherUser : { username: otherName });
    document.getElementById('empty-state').hidden = true;
    document.getElementById('chat-view').hidden = false;
    const nameEl = document.getElementById('chat-with-name');
    nameEl.textContent = otherName;
    nameEl.style.background = selectedChatColor.base;
    nameEl.style.color = '#fff';
    renderChatList();

    const messages = await Api.getMessages(chatId);
    renderMessages(messages);
}

function renderMessages(messages) {
    const container = document.getElementById('messages');
    container.innerHTML = '';
    messages.forEach(appendMessageBubble);
    container.scrollTop = container.scrollHeight;
}

function appendMessageBubble(message) {
    const container = document.getElementById('messages');
    const bubble = document.createElement('div');
    const isOwn = message.senderId === currentUser.id;
    bubble.className = 'bubble ' + (isOwn ? 'bubble-out' : 'bubble-in');
    const color = selectedChatColor || colorForUser({ id: message.senderId });
    bubble.style.background = isOwn ? color.base : color.soft;
    bubble.style.color = isOwn ? '#fff' : '#050505';
    bubble.innerHTML = `
        <div class="bubble-content">${escapeHtml(message.content)}</div>
        <div class="bubble-time">${new Date(message.sentAt).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})}</div>
    `;
    container.appendChild(bubble);
    container.scrollTop = container.scrollHeight;
}

function onWsMessage(message) {
    const existingChat = chats.find(c => c.id === message.chatId);
    if (existingChat) {
        existingChat.lastMessagePreview = message.content;
        existingChat.lastMessageAt = message.sentAt;
        chats.sort((a, b) => new Date(b.lastMessageAt) - new Date(a.lastMessageAt));
        renderChatList();
    } else {
        loadChats();
    }

    const isOwnMessage = message.senderId === currentUser.id;
    if (message.chatId === selectedChatId && !isOwnMessage) {
        appendMessageBubble(message);
    }
}

function sendCurrentMessage() {
    const input = document.getElementById('message-input');
    const content = input.value.trim();
    if (!content || !selectedChatId) return;
    try {
        ChatSocket.sendMessage(selectedChatId, content);
    } catch (e) {
        alert('Impossibile inviare il messaggio: connessione non attiva');
        return;
    }
    appendMessageBubble({ senderId: currentUser.id, content, sentAt: new Date().toISOString() });
    input.value = '';
}

async function askAiSuggestion() {
    if (!selectedChatId) return;
    const btn = document.getElementById('ai-help-btn');
    btn.disabled = true;
    btn.textContent = '...';
    try {
        const { suggestion } = await Api.aiSuggest(selectedChatId);
        document.getElementById('message-input').value = suggestion;
    } catch (e) {
        alert('Suggerimento AI non disponibile: ' + e.message);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Aiuto';
    }
}

function wireEvents() {
    document.getElementById('send-btn').addEventListener('click', sendCurrentMessage);
    document.getElementById('message-input').addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendCurrentMessage();
        }
    });
    document.getElementById('ai-help-btn').addEventListener('click', askAiSuggestion);

    document.getElementById('logout-btn').addEventListener('click', async () => {
        ChatSocket.disconnect();
        await Api.logout();
        location.href = '/login.html?logout';
    });

    const newChatModal = document.getElementById('new-chat-modal');
    document.getElementById('new-chat-btn').addEventListener('click', async () => {
        const users = await Api.listUsers();
        const userList = document.getElementById('user-list');
        userList.innerHTML = '';
        users.forEach(user => {
            const item = document.createElement('div');
            item.className = 'user-list-item';
            item.innerHTML = `<div class="avatar" style="background:${colorForUser(user).base}">${initials(user.username)}</div><span>${escapeHtml(user.username)}</span>`;
            item.addEventListener('click', async () => {
                const chat = await Api.openChat(user.id);
                newChatModal.hidden = true;
                await loadChats();
                selectChat(chat.id, user.username);
            });
            userList.appendChild(item);
        });
        newChatModal.hidden = false;
    });
    document.getElementById('close-new-chat').addEventListener('click', () => newChatModal.hidden = true);

    const statsModal = document.getElementById('stats-modal');
    document.getElementById('stats-btn').addEventListener('click', async () => {
        document.getElementById('stats-message').hidden = true;
        const stats = await Api.getStats();
        document.getElementById('stat-sent').textContent = stats.messagesSent;
        document.getElementById('stat-received').textContent = stats.messagesReceived;
        document.getElementById('stat-chats').textContent = stats.openChats;
        statsModal.hidden = false;
    });
    document.getElementById('close-stats').addEventListener('click', () => statsModal.hidden = true);
    document.getElementById('email-stats-btn').addEventListener('click', async () => {
        const msg = document.getElementById('stats-message');
        try {
            await Api.emailStats();
            msg.textContent = 'Email inviata! Controlla la tua casella di posta.';
        } catch (e) {
            msg.textContent = 'Invio email fallito: ' + e.message;
        }
        msg.hidden = false;
    });
}

init();
