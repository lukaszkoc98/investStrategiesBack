package com.controllers;

import com.models.Asset;
import com.models.AuthorizedUser;
import com.models.ChangePasswordDTO;
import com.models.User;
import com.repositories.AssetsRepository;
import com.repositories.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AssetsRepository assetsRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Double NEW_USER_CASH = 10000.00;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    @RequestMapping("/all")
    public List<Object> get() {
        System.out.println(usersRepository.findAll());
        return List.of(usersRepository.findAll());
    }

    @PostMapping()
    @RequestMapping(value = "/add", method = RequestMethod.POST, headers = "Accept=application/json")
    ResponseEntity newUser(@RequestBody User newUser) {
        if (isUsernameTaken(getUsernames(), newUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        } else {
            newUser = hashPasswordAndSaveNewUser(newUser);
            saveNewUserAssets(newUser);
            return ResponseEntity.status(HttpStatus.OK).body(newUser);
        }
    }

    private User hashPasswordAndSaveNewUser(User newUser) {
        newUser.setCreationDate(LocalDate.now());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser = usersRepository.save(newUser);
        return newUser;
    }

    private void saveNewUserAssets(User newUser) {
        Asset newUserAssets = new Asset();
        newUserAssets.setUserID(newUser.getId());
        newUserAssets.setCash(NEW_USER_CASH);
        newUserAssets.setGold(0.00);
        newUserAssets.setSilver(0.00);
        newUserAssets = assetsRepository.save(newUserAssets);
    }

    private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        return dateFormat.format(date);
    }

    @PostMapping()
    @RequestMapping(value = "/changepassword", method = RequestMethod.POST, headers = "Accept=application/json")
    ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
                                          @RequestHeader("x-token") String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
            if (null != changePasswordDTO.newPassword &&
                    changePasswordDTO.newPassword.equals(changePasswordDTO.newPasswordRepeated)) {
                if (checkPasswordPolicies(changePasswordDTO.newPassword)) {
                    User existingUser = getUserByUsername(changePasswordDTO.username);
                    return changePasswordIfCurrentPasswordCorrect(changePasswordDTO, existingUser);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password do not meets password policies");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords are not equal");
        } catch (ExpiredJwtException expiredJwtException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your session expired");
        }

    }

    private ResponseEntity<String> changePasswordIfCurrentPasswordCorrect(ChangePasswordDTO changePasswordDTO, User existingUser) {
        if (isPasswordCorrect(existingUser.getUsername(), changePasswordDTO.currentPassword)) {
            usersRepository.updatePassword(
                    passwordEncoder.encode(changePasswordDTO.newPassword),
                    existingUser.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Password change success");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is not correct");
    }


    private boolean checkPasswordPolicies(String newPassword) {
        Pattern passwordPoliciesPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-z])(?=\\S+$).{4,}$");
        Matcher passwordPoliciesMatcher = passwordPoliciesPattern.matcher(newPassword);
        return passwordPoliciesMatcher.matches();
    }


    @PostMapping()
    @RequestMapping(value = "/authorize", method = RequestMethod.POST, headers = "Accept=application/json")
    ResponseEntity<AuthorizedUser> authorizeUser(@RequestBody User userToAuthorize) {
        Long now = System.currentTimeMillis();
        String jwts = Jwts.builder()
                .setSubject(userToAuthorize.getUsername())
                .claim("roles", "user")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secretkey").compact();
        Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(jwts).getBody();
        try {
            User existingUser = getUserByUsername(userToAuthorize.getUsername());
            if (isPasswordCorrect(existingUser.getUsername(), userToAuthorize.getPassword())) {
                AuthorizedUser authorizedUser = new AuthorizedUser(existingUser.getId(), existingUser.getUsername()
                        , userToAuthorize.getPassword(), existingUser.getEmail(), jwts, existingUser.getCreationDate());
                return ResponseEntity.status(HttpStatus.OK).body(authorizedUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private boolean isPasswordCorrect(String username, String password) {
        User existingUser = getUserByUsername(username);
        return passwordEncoder.matches(password, existingUser.getPassword());
    }

    private User getUserByUsername(String username) throws NoSuchElementException {
        return usersRepository.findAll().stream()
                .filter(users -> users.getUsername()
                        .equals(username)).findFirst().get();
    }

    private List<String> getUsernames() {
        return usersRepository.findAll().stream().map(user -> user.getUsername())
                .collect(Collectors.toList());
    }

    private boolean isUsernameTaken(List<String> usernames, String username) {
        return usernames.stream()
                .filter(takenUsername -> takenUsername.equals(username))
                .findFirst().isPresent();
    }

}
