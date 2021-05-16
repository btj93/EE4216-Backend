package io.javabrains.springsecurityjwt;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.javabrains.springsecurityjwt.filters.JwtRequestFilter;
import io.javabrains.springsecurityjwt.models.AuthenticationRequest;
import io.javabrains.springsecurityjwt.models.AuthenticationResponse;
import io.javabrains.springsecurityjwt.util.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

@SpringBootApplication
public class SpringSecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJwtApplication.class, args);
    }

}

@CrossOrigin(origins = "*")
@RestController
class HelloWorldController {

    private static String filePath = "ee4216-4c8a8-firebase-adminsdk-q4wwh-cace95a9b9.json";
    private static FileInputStream serviceAccount;

    static {
        try {
            serviceAccount = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String DATABASE_URL = "https://ee4216-4c8a8.firebaseio.com/";
    private static FirebaseOptions options = null;

    static {
        try {
            options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(DATABASE_URL).build();
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public static FirebaseApp myApp = FirebaseApp.initializeApp(options);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    HelloWorldController() throws IOException {
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        FirebaseOperationResult result = new FirebaseOperations(ref).put("users/" + authenticationRequest.getUsername(), "password", authenticationRequest.getPassword(), Boolean.FALSE);
        if (result.success) {
            return ResponseEntity.ok().body(new Status(Status.stat.OK));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(Status.stat.Failed));
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestParam(value = "username") String username) throws Exception {
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        Boolean success = new FirebaseOperations(ref).delete("users/" + username);
        if (success) {
            return ResponseEntity.ok(new Status(Status.stat.OK));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(Status.stat.Failed));
    }

    @RequestMapping(value = "/createNote", method = RequestMethod.POST)
    public ResponseEntity<?> createNote(@RequestHeader(name = "Authorization") String token) throws Exception {
        String username = jwtTokenUtil.extractUsername(token.substring(7));
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        DateTime now = new DateTime(); // Gives the default time zone.
        DateTime dateTime = now.toDateTime(DateTimeZone.UTC); // Converting default zone to UTC
        String time = dateTime.toString().replaceAll("\\.", ":");
        FirebaseOperationResult result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "content", "", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "size", "12", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "color", "#FFFFFF", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "top", "42", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "left", "8", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "width", "190", Boolean.FALSE);
        System.out.println(result.getSuccess());
        result = new FirebaseOperations(ref).put("users/" + username + "/notes/" + time, "height", "190", Boolean.FALSE);
        System.out.println(result.getSuccess());
        createNoteReturn re = new createNoteReturn(result.success, result.snapshot);
        if (result.success) {
            return ResponseEntity.ok(re);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re);
    }

    @RequestMapping(value = "/getNotes", method = RequestMethod.GET)
    public ResponseEntity<?> getNotes(@RequestHeader(name = "Authorization") String token) throws Exception {
        String username = jwtTokenUtil.extractUsername(token.substring(7));
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        DataSnapshot ss = new FirebaseOperations(ref).get("users/" + username + "/notes");
        ArrayList<Note> results = new ArrayList<Note>();
        for (DataSnapshot note : ss.getChildren()) {
            Iterator<DataSnapshot> i = note.getChildren().iterator();
            String color = i.next().getValue().toString();
            String content = i.next().getValue().toString();
            String height = i.next().getValue().toString();
            String left = i.next().getValue().toString();
            String size = i.next().getValue().toString();
            String top = i.next().getValue().toString();
            String width = i.next().getValue().toString();
            Note result = new Note(note.getKey(), content, size, color, top, left, width, height);
            results.add(result);
        }
        return ResponseEntity.ok(results);
    }

    @RequestMapping(value = "/updateNote", method = RequestMethod.PUT)
    public ResponseEntity<?> updateNote(@RequestHeader(name = "Authorization") String token, @RequestParam(name = "noteId") String noteId, @RequestParam(name = "content") String content, @RequestParam(name = "size") String size, @RequestParam(name = "color") String color, @RequestParam(name = "top") String top, @RequestParam(name = "left") String left, @RequestParam(name = "width") String width, @RequestParam(name = "height") String height) throws Exception {
        String username = jwtTokenUtil.extractUsername(token.substring(7));
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        Boolean success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "content", content);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "size", size);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "color", color);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "top", top);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "left", left);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "width", width);
        success = new FirebaseOperations(ref).update("users/" + username + "/notes/" + noteId, "height", height);
        if (success) {
            return ResponseEntity.ok(new Status(Status.stat.OK));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(Status.stat.Failed));
    }

    @RequestMapping(value = "/deleteNote", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNote(@RequestHeader(name = "Authorization") String token, @RequestParam(name = "noteId") String noteId) throws Exception {
        String username = jwtTokenUtil.extractUsername(token.substring(7));
        DatabaseReference ref = FirebaseDatabase.getInstance(myApp).getReference();
        Boolean success = new FirebaseOperations(ref).delete("users/" + username + "/notes/" + noteId);
        if (success) {
            return ResponseEntity.ok(new Status(Status.stat.OK));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(Status.stat.Failed));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService myUserDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()
                .antMatchers("/signup").permitAll()
                .anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
