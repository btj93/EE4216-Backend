package io.javabrains.springsecurityjwt;

import com.google.firebase.database.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Service
public class MyUserDetailsService implements UserDetailsService {



    public MyUserDetailsService() throws IOException {
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference ref = FirebaseDatabase.getInstance(HelloWorldController.myApp).getReference();
        DatabaseReference usersRef = ref.getDatabase().getReference("users/" + username + "/password");
        final String[] password = {""};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                password[0] = dataSnapshot.getValue().toString();
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
        return new User(username, password[0], new ArrayList<>());
    }
}