package ce.web.daarbast.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ce.web.daarbast.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    @Qualifier("concurrent")
    private final Map<Long, SseEmitter> userEmitters;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    @GetMapping("/status/{userId}/online")
    public void setUserOnline(@PathVariable Long userId) {
        redisTemplate.opsForValue().set("user:status:" + userId, true);
        notifyUserStatusChange(userId, true);
    }

    @GetMapping("/status/{userId}/offline")
    public void setUserOffline(@PathVariable Long userId) {
        redisTemplate.opsForValue().set("user:status:" + userId, false);
        notifyUserStatusChange(userId, false);
    }

    @GetMapping("/notifications/{userId}")
    public SseEmitter getNotifications(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter();
        userEmitters.put(userId, emitter);

        emitter.onCompletion(() -> userEmitters.remove(userId));
        emitter.onTimeout(() -> userEmitters.remove(userId));

        return emitter;
    }

    private void notifyUserStatusChange(Long userId, boolean isOnline) {
        String statusMessage = isOnline ? "User is online" : "User is offline";
        for (Map.Entry<Long, SseEmitter> entry : userEmitters.entrySet()) {
            notificationService.sendNotification(entry.getValue(), "status", statusMessage);
        }
    }

    public void notifyTaskAssignee(Long assigneeId, String message) {
        SseEmitter emitter = userEmitters.get(assigneeId);
        if (emitter != null) {
            notificationService.sendNotification(emitter, "task", message);
        }
    }

    public void notifyWatchers(List<Long> watcherIds, String message) {
        for (Long watcherId : watcherIds) {
            SseEmitter emitter = userEmitters.get(watcherId);
            if (emitter != null) {
                notificationService.sendNotification(emitter, "task", message);
            }
        }
    }
}
