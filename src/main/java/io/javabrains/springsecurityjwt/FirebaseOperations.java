package io.javabrains.springsecurityjwt;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import io.javabrains.springsecurityjwt.util.FirebaseOperationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FirebaseOperations {

    DatabaseReference ref;

    public FirebaseOperations(DatabaseReference ref) {
        this.ref = ref;
    }

    public DataSnapshot get(String path) {
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference usersRef = this.ref.getDatabase().getReference(path);
        final DataSnapshot[] result = {null};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result[0] = dataSnapshot;
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.addListenerForSingleValueEvent(listener);
        try {
            done.await();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result[0];
    }


    public FirebaseOperationResult put(String path, String key, String value, Boolean push) {
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference usersRef = this.ref.getDatabase().getReference(path + "/" + key);
        DatabaseReference ref = this.ref;
        FirebaseOperationResult result = new FirebaseOperationResult(Boolean.FALSE, null);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (push) {
                        usersRef.push().setValueAsync(value);
                    } else {
                        usersRef.setValueAsync(value);
                    }
                    result.success = Boolean.TRUE;
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.addListenerForSingleValueEvent(listener);
        try {
            done.await();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        result.snapshot = new FirebaseOperations(usersRef).get(path);
        return result;
    }

    public Boolean update(String path, String key, String value) {
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference usersRef = this.ref.getDatabase().getReference(path);
        DatabaseReference ref = this.ref;
        FirebaseOperationResult result = new FirebaseOperationResult(Boolean.FALSE, null);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(key, value);
                    usersRef.updateChildrenAsync(map);
                    result.success = Boolean.TRUE;
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.addListenerForSingleValueEvent(listener);
        try {
            done.await();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result.success;
    }


    public Boolean delete(String path) {
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference usersRef = this.ref.getDatabase().getReference(path);
        final Boolean[] success = {Boolean.FALSE};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersRef.removeValueAsync();
                    success[0] = Boolean.TRUE;
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.addListenerForSingleValueEvent(listener);
        try {
            done.await();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (success[0]) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
