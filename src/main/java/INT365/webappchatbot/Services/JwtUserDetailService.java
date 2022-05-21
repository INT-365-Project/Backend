package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.User;
import INT365.webappchatbot.Models.JwtRequest;
import INT365.webappchatbot.Models.UserModel;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.UserModelHeader;
import INT365.webappchatbot.Repositories.UserRepository;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserModelHeader(username, null, null, null);
    }

    @Transactional
    public void createUser(JwtRequest request, UserModelDetail user) {
        User findUser = this.userRepository.findUserByUsername(request.getUsername());
        if (Objects.isNull(findUser)) {
            this.saveUser(user, new User());
        } else {
            this.saveUser(user, findUser);
        }
    }

    @Transactional
    private void saveUser(UserModelDetail user, User findUser) {
        findUser.setUsername(user.getUsername());
        findUser.setTitleNameTh(user.getTitleNameTh());
        findUser.setFirstNameTh(user.getFirstNameTh());
        findUser.setLastNameTh(user.getLastNameTh());
        findUser.setRoles(StringUtils.join(user.getRoles(), ','));
        userRepository.saveAndFlush(findUser);
    }

    @Transactional
    public UserModelDetail findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        UserModelDetail userModelDetail = new UserModelDetail();
        userModelDetail.setUserId(user.getUserId());
        userModelDetail.setUsername(user.getUsername());
        userModelDetail.setTitleNameTh(user.getTitleNameTh());
        userModelDetail.setFirstNameTh(user.getFirstNameTh());
        userModelDetail.setFullName(user.getTitleNameTh() + user.getFirstNameTh() + " " + user.getLastNameTh());
        userModelDetail.setLastNameTh(user.getLastNameTh());
        userModelDetail.setRoles(Arrays.stream(user.getRoles().split(",")).collect(Collectors.toList()));
        return userModelDetail;
    }

    public List<GrantedAuthority> getAuthority(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }

    public UserModel getUserModel(Authentication authentication) {
        UserModelHeader userDetails = (UserModelHeader) authentication.getPrincipal();
        System.out.println("userDetail :" + userDetails.getUserModelDetail());
        UserModelDetail userModelDetail = this.findUserByUsername(userDetails.getUsername());
        return UserModel.builder()
                .username(userDetails.getUsername())
                .roles(userModelDetail.getRoles())
                .userModelDetail(userModelDetail)
                .build();
    }
}
