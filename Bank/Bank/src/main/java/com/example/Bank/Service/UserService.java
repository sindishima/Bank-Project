package com.example.Bank.Service;

import com.example.Bank.Enum.Role;
import com.example.Bank.Exceptions.NoAuthorizationException;
import com.example.Bank.Exceptions.ResourceNotFoundException;
import com.example.Bank.Model.User;
import com.example.Bank.Repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    public List<User> getAllUsers(){
        User user = getCurrentLoggedUser();
        if(user!=null) {
            if (user.getRole().equals(Role.ADMIN)) {
                return userRepository.findUserByRole(Role.TELLER);
            } else if (user.getRole().equals(Role.TELLER)) {
                return userRepository.findUserByRole(Role.CLIENT);
            }
            else {
                log.error(String.format("User with id %s is not authorized to see all users", user.getId()));
                throw new NoAuthorizationException("User not authorized");
            }
        }
        else {
            throw new ResourceNotFoundException("No user found");
        }
    }


    public User getUser(Integer userId){
        User loggedUser = getCurrentLoggedUser();
        Optional<User> user = userRepository.findById(userId);

        if(!user.isEmpty() && loggedUser!=null){
            User user1 = user.get();
            if (loggedUser.getRole().equals(Role.ADMIN) && user1.getRole().equals(Role.TELLER)) {
                return user1;
            }
            else if (loggedUser.getRole().equals(Role.TELLER) && user1.getRole().equals(Role.CLIENT)) {
                return user1;
            }
            else if (loggedUser.getId()==user1.getId()) {   //if a user want to see its own profile
                return user1;
            }
            else{
                log.error(String.format("User with id %s is not authorized to see user with id %s", loggedUser.getId(), user1.getId()));
                throw new NoAuthorizationException("User not authorized");
            }
        }
        else {
            log.error(String.format("User with id %s doesn't exists", userId));
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public User createAdmin(User admin){
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        log.info("Admin created");
        return userRepository.save(admin);
    }


    public User createClient(User client){
        client.setRole(Role.CLIENT);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        log.info("Client created");
        return userRepository.save(client);
    }


    public User createTeller(User teller){
        teller.setRole(Role.TELLER);
        teller.setPassword(passwordEncoder.encode(teller.getPassword()));
        log.info("Teller created");
        return userRepository.save(teller);
    }


    public User updateUser(Integer id, User user){
        User loggedUser = getCurrentLoggedUser();
        Optional<User> optionalUser = userRepository.findById(id);

        if(loggedUser!=null && !optionalUser.isEmpty()){
            User user1 = optionalUser.get();     //user that will be updated

            if (loggedUser.getRole().equals(Role.ADMIN) && user1.getRole().equals(Role.TELLER)) {
                user1.setUsername(user.getUsername());
                user1.setPassword(passwordEncoder.encode(user.getPassword()));
                log.info(String.format("User with id %s is updated", user.getId()));
                return userRepository.save(user1);
            }
            else if (loggedUser.getRole().equals(Role.TELLER) && user1.getRole().equals(Role.CLIENT)) {
                user1.setUsername(user.getUsername());
                user1.setPassword(passwordEncoder.encode(user.getPassword()));
                log.info(String.format("User with id %s is updated", user.getId()));
                return userRepository.save(user1);
            }
//            else if (loggedUser.getId()==user1.getId()) {    //if a user want to update itself
//                user1.setUsername(user.getUsername());
//                user1.setPassword(passwordEncoder.encode(user.getPassword()));
//                log.info(String.format("User with id %s is updated", user.getId()));
//                return userRepository.save(user1);
//            }
            else {
                log.error(String.format("User with id %s is not authorized to update user with id %s", loggedUser.getId(), user1.getId()));
                throw new NoAuthorizationException("User not authorized");
            }
        }
        else {
            log.error(String.format("User with id %s doesn't exists", id));
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public void deleteUser(Integer id){
        User loggedUser = getCurrentLoggedUser();
        Optional<User> optionalUser = userRepository.findById(id);

        if(!optionalUser.isEmpty() && loggedUser!=null){
            User user = optionalUser.get();
            if (loggedUser.getRole().equals(Role.ADMIN) && user.getRole().equals(Role.TELLER)) {
                log.info(String.format("User with id %s id deleted", user.getId()));
                userRepository.deleteById(id);
            }
            else if (loggedUser.getRole().equals(Role.TELLER) && user.getRole().equals(Role.CLIENT)) {
                log.info(String.format("User with id %s id deleted", user.getId()));
                userRepository.deleteById(id);
            }
//            else if(loggedUser.getId()==user.getId()){
//                log.info(String.format("User with id %s id deleted", user.getId()));
//                userRepository.deleteById(id);
//            }
            else {
                log.error(String.format("User with id %s is not authorized to delete user with id %s", loggedUser.getId(), user.getId()));
                throw new NoAuthorizationException("This user is not authorized");
            }
        }
        else {
            log.error(String.format("User with id %s doesn't exist", id));
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public User getCurrentLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User loggedUser = userRepository.findByUsername(username);
        return loggedUser;
    }
}
