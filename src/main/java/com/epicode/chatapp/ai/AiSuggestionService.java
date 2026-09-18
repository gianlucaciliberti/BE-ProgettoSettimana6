package com.epicode.chatapp.ai;

import com.epicode.chatapp.chat.Chat;
import com.epicode.chatapp.chat.ChatService;
import com.epicode.chatapp.message.Message;
import com.epicode.chatapp.message.MessageRepository;
import com.epicode.chatapp.user.User;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiSuggestionService {

    private static final String SYSTEM_PROMPT = """
            Sei un assistente che aiuta una persona a scrivere il prossimo messaggio in una \
            conversazione di chat con un'altra persona. Proponi UN SOLO messaggio breve, naturale \
            e colloquiale, in italiano, dal punto di vista dell'utente, che continui la conversazione \
            in modo coerente con il tono e il contesto. Rispondi SOLO con il testo del messaggio \
            proposto, senza virgolette, prefissi o spiegazioni aggiuntive.""";

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final OpenRouterClient openRouterClient;

    public String suggestNextMessage(User currentUser, Long chatId) {
        Chat chat = chatService.getByIdForParticipant(chatId, currentUser);
        User otherUser = chat.theOtherUser(currentUser);

        List<Message> recent = messageRepository.findTop10ByChatOrderBySentAtDesc(chat);
        Collections.reverse(recent);

        String transcript = buildTranscript(recent, currentUser, otherUser);
        return openRouterClient.complete(SYSTEM_PROMPT, transcript);
    }

    private String buildTranscript(List<Message> messages, User currentUser, User otherUser) {
        StringBuilder sb = new StringBuilder();
        if (messages.isEmpty()) {
            sb.append("La conversazione con ").append(otherUser.getUsername())
                    .append(" non ha ancora messaggi.\n");
        } else {
            for (Message message : messages) {
                String label = message.getSender().getId().equals(currentUser.getId())
                        ? "Utente"
                        : otherUser.getUsername();
                sb.append('[').append(label).append("]: ").append(message.getContent()).append('\n');
            }
        }
        sb.append("\nScrivi il prossimo messaggio da inviare, come Utente.");
        return sb.toString();
    }
}
