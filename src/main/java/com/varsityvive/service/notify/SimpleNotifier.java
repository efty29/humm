package com.varsityvive.service.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // তোমার প্রোজেক্টে আছে বলেই ধরলাম
import org.springframework.stereotype.Service;

import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;

@Service
public class SimpleNotifier extends AbstractNotifier {
    private static final Logger log = LoggerFactory.getLogger(SimpleNotifier.class);

    private final UserRepository userRepository; // userId → User রিজলভ করার জন্য

    public SimpleNotifier(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Overriding: AbstractNotifier.doSend(...) ইমপ্লিমেন্ট করছি */
 @Override
protected void doSend(User user, String message, Channel channel) {
    switch (channel) {
        case EMAIL:
            log.info("[EMAIL] to={} msg={}", user.getEmail(), message);
            break;
        case SMS:
            log.info("[SMS] toUserId={} msg={}", user.getId(), message);
            break;
        default:
            throw new IllegalArgumentException("Unknown channel: " + channel);
    }
}


    /** Overloading #1: ডিফল্ট চ্যানেল EMAIL */
    public void notify(User user, String message) {
        send(user, message, Channel.EMAIL);
    }

    /** Overloading #2: চ্যানেলসহ */
    public void notify(User user, String message, Channel channel) {
        send(user, message, channel);
    }

    /** Overloading #3: শুধু userId দিলে (repo দিয়ে User উঠিয়ে নেয়) */
    public void notify(Long userId, String message) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        notify(u, message);
    }
}
