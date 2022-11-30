package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.User;
import INT365.webappchatbot.Models.JwtRequest;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.UserModelHeader;
import INT365.webappchatbot.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class AuthService {
    @Autowired
    private JwtUserDetailService userDetailService;
    @Autowired
    private UserRepository userRepository;

    public UserModelHeader authenticate(JwtRequest authenticationRequest) {
        User usr = userRepository.findUserByUsername(authenticationRequest.getUsername());
        UserModelDetail user = new UserModelDetail();
        user.setUserId(1L);
        user.setUsername(usr.getUsername());
        user.setTitleNameTh(usr.getTitleNameTh());
        user.setFirstNameTh(usr.getFirstNameTh());
        user.setLastNameTh(usr.getLastNameTh());
        user.setFullName(usr.getTitleNameTh() + usr.getFirstNameTh() + " " + usr.getLastNameTh());
        user.setRoles(new ArrayList<>(Arrays.asList(usr.getRoles().split(","))));
        this.userDetailService.createUser(authenticationRequest, user);
        UserModelDetail userModelDetail = userDetailService.findUserByUsername(authenticationRequest.getUsername());
        return new UserModelHeader(authenticationRequest.getUsername(), authenticationRequest.getPassword(), userDetailService.getAuthority(userModelDetail.getRoles()), userModelDetail);
    }
}
