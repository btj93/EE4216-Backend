package io.javabrains.springsecurityjwt.util;

public class Status {
    public enum stat {
        OK,
        Failed
    }

    stat status;

    public Status(stat s) {
        this.status = s;
    }

    public stat getStatus() {
        return status;
    }
}
