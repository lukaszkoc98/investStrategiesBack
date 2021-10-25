package com.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.models.Ratio;
import com.models.Ratios;
import com.models.User;
import com.repositories.AuthorizedUser;
import com.repositories.UsersRepository;
import com.utils.jsonToRatiosMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping
    @RequestMapping("/all")
    public List<Object> get() {
        System.out.println(usersRepository.findAll());
        return List.of(usersRepository.findAll());
    }

    class Test {
        public Test() {
        }

        private String metal;

        public Test(String metal, String currency, String weight_unit) {
            this.metal = metal;
            this.currency = currency;
            this.weight_unit = weight_unit;
        }

        private String currency;
        private String weight_unit;

        public String getMetal() {
            return metal;
        }

        public void setMetal(String metal) {
            this.metal = metal;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getWeight_unit() {
            return weight_unit;
        }

        public void setWeight_unit(String weight_unit) {
            this.weight_unit = weight_unit;
        }
    }
    @GetMapping
    @RequestMapping("/gold")
    public ResponseEntity<Ratios> getGold() throws IOException, JSONException {
//        URL url = new URL("https://goldbroker.com/api/historical-spot-prices?metal=XAG&currency=PLN&weight_unit=oz");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        in.close();
        Path path = Paths.get("src/test/resources/string.txt");

        BufferedReader reader = Files.newBufferedReader(path);
        String content = reader.readLine();
        JSONObject jsonObject = new JSONObject(String.valueOf(content));
        Ratios ratios = jsonToRatiosMapper.map(jsonObject);
        return ResponseEntity.status(HttpStatus.OK).body(ratios);
    }

    @GetMapping
    @RequestMapping("/ratios")
    public ResponseEntity<ArrayList<Ratio>> getRatios() throws IOException, JSONException {
//        URL url = new URL("https://goldbroker.com/api/historical-spot-prices?metal=XAG&currency=PLN&weight_unit=oz");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        in.close();
        Path path = Paths.get("src/test/resources/string.txt");

        BufferedReader reader = Files.newBufferedReader(path);
        String content = reader.readLine();
        JSONObject jsonObject = new JSONObject(String.valueOf(content));
        JSONObject jsonEmbedded = jsonObject.getJSONObject("_embedded");
        return ResponseEntity.status(HttpStatus.OK).body(jsonToRatiosMapper.extractRatios(jsonEmbedded));
    }

    @PostMapping()
    @RequestMapping(value = "/add", method = RequestMethod.POST, headers="Accept=application/json")
    ResponseEntity<String> newUser(@RequestBody User newUser) {
        if(isUsernameTaken(getUsernames(),newUser.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        }
        else {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            return ResponseEntity.status(HttpStatus.OK).body(usersRepository.save(newUser).toString());
        }
    }

    @PostMapping()
    @RequestMapping(value = "/authorize", method = RequestMethod.POST, headers="Accept=application/json")
    ResponseEntity<AuthorizedUser> authorizeUser(@RequestBody User userToAuthorize) {
        Long now = System.currentTimeMillis();
        String jwts = Jwts.builder()
                .setSubject(userToAuthorize.getUsername())
                .claim("roles", "user")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 36000))
                .signWith(SignatureAlgorithm.HS512, "secretkey").compact();
        Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(jwts).getBody();
        try {
            User existingUser = getUserByUsername(userToAuthorize.getUsername());
            if(passwordEncoder.matches(userToAuthorize.getPassword(), existingUser.getPassword())){
                AuthorizedUser authorizedUser = new AuthorizedUser(existingUser.getId(), existingUser.getUsername()
                ,userToAuthorize.getPassword(), existingUser.getEmail(), jwts);
                return ResponseEntity.status(HttpStatus.OK).body(authorizedUser);
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthorizedUser());
            }
        } catch(NoSuchElementException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthorizedUser());
        }
    }


    private User getUserByUsername(String username) throws NoSuchElementException {
        return usersRepository.findAll().stream()
                .filter(users -> users.getUsername()
                        .equals(username)).findFirst().get();
    }

    private List<String> getUsernames() {
        return usersRepository.findAll().stream().map(user->user.getUsername())
                .collect(Collectors.toList());
    }

    private boolean isUsernameTaken(List <String> usernames, String username){
        return usernames.stream()
                .filter(takenUsername->takenUsername.equals(username))
                .findFirst().isPresent();
    }

}
