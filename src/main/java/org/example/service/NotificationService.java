package org.example.service;
import org.example.exception.ApplicationException;
import java.util.HashSet;
import java.util.Set;

public class NotificationService {
    private static NotificationService INSTANCE;
    private static Set<Long> subscribers;

    private NotificationService() {
        subscribers = new HashSet<>();
    }

    public static NotificationService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NotificationService();
        }
        return INSTANCE;
    }

    public void notify(Long id) {
        if (subscribers.contains(id)) {
            throw new ApplicationException ("Уведомление: превышение установленного лимита расходования");
        }
    }

    public String activate(Long id) {
        if (!subscribers.add(id)) {
            subscribers.remove(id);
            return "Уведомления успешно деактивированы";
        } else return "Уведомления успешно активированы";
    }
}
