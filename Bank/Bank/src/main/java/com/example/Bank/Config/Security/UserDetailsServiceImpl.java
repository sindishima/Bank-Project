package com.example.Bank.Config.Security;

import com.example.Bank.Exceptions.ResourceNotFoundException;
import com.example.Bank.Model.User;
import com.example.Bank.Repostory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
//    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new ResourceNotFoundException("This user doesnt exists");
        }
        else {
            return UserDetailsImpl.build(user);
        }
    }
}