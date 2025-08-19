package com.varsityvive.service.notify;

import java.util.Objects;

import com.varsityvive.model.User;

/** Abstraction: সাবক্লাস আসল সেন্ড লজিক দেবে; এই ক্লাস টেমপ্লেট/গার্ড করে। */
public abstract class AbstractNotifier {

    // Inner enum: আলাদা ফাইল লাগবে না
    public enum Channel { EMAIL, SMS }

    // Template method: common validation + delegate
    public final void send(User user, String message, Channel channel) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        doSend(user, message, channel); // ← সাবক্লাস override করবে
    }

    // Hook to override
    protected abstract void doSend(User user, String message, Channel channel);
}
