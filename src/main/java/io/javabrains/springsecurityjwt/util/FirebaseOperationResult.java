package io.javabrains.springsecurityjwt.util;

import com.google.firebase.database.DataSnapshot;

public class FirebaseOperationResult {
    public Boolean success;
    public DataSnapshot snapshot;

    public FirebaseOperationResult(Boolean success, DataSnapshot snapshot) {
        this.success = success;
        this.snapshot = snapshot;
    }

    public Boolean getSuccess() {
        return success;
    }

    public DataSnapshot getSnapshot() {
        return snapshot;
    }
}
