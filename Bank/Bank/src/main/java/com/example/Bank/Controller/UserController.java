package com.example.Bank.Controller;

import com.example.Bank.Model.User;
import com.example.Bank.Repostory.UserRepository;
import com.example.Bank.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('TELLER', 'ADMIN')")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'ADMIN', 'CLIENT')")
    public ResponseEntity<User> getUser(@PathVariable Integer id){
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PostMapping("/admin")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<User> createAdmin(@RequestBody User admin){
        return new ResponseEntity<>(userService.createAdmin(admin), HttpStatus.CREATED);
    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<User> createClient(@RequestBody User client){
        return new ResponseEntity<>(userService.createClient(client), HttpStatus.CREATED);
    }

    @PostMapping("/teller")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<User> createTeller(@RequestBody User teller){
        return new ResponseEntity<>(userService.createTeller(teller), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'ADMIN', 'CLIENT')")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user){
        return new ResponseEntity<>(userService.updateUser(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'ADMIN', 'CLIENT')")
    public void deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
    }
}
