package INT365.webappchatbot.Services;

import INT365.webappchatbot.Models.JwtRequest;
import INT365.webappchatbot.Models.UserModel;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.UserModelHeader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserModelHeader(username, null, null, null);
    }

    @Transactional
    public void createUser(JwtRequest request, UserModelDetail user) {
        //
    }

    @Transactional
    public UserModelDetail findUserByUsername(String username) {
        UserModelDetail userModelDetail = new UserModelDetail();
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
//                .roles(userModelDetail.getPrivilegeList())
                .userModelDetail(userModelDetail)
                .build();
    }
}
