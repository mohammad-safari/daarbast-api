package ce.web.daarbast.service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class NotificationService {
    @Async("taskExecutor")
    public void sendNotification(SseEmitter emitter, String eventName, String message) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(message));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
