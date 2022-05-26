package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Configs.JwtTokenUtils;
import INT365.webappchatbot.Models.JwtRequest;
import INT365.webappchatbot.Models.JwtResponse;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.UserModelHeader;
import INT365.webappchatbot.Services.JwtUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://20.92.229.38:80")
@RestController
public class AuthController {
    @Autowired
    private JwtTokenUtils jwtTokenUtil;
    @Autowired
    private JwtUserDetailService userDetailService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
//        SSOResponse<UserModelDetail> response = externalService.getAuthentication(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//        if (response.getResponseCode().equals("OK")) {
        // check if user exist
        if (authenticationRequest.getUsername().equals("admin")) {
            UserModelDetail user = new UserModelDetail();
            user.setUserId(1L);
            user.setUsername("admin");
            user.setTitleNameTh("นาย");
            user.setFirstNameTh("สมหมาย");
            user.setLastNameTh("ระม้ายคล้ายคลึง");
            user.setFullName("นายสมหมาย ระม้ายคล้ายคลึง");
            List<String> roles = new ArrayList<>();
            roles.add("Admin");
            roles.add("Q&A");
            user.setRoles(roles);
            this.userDetailService.createUser(authenticationRequest, user);
//        this.userDetailService.createUser(authenticationRequest, response.getResponseData());
        }
//        userDetailService.loadUserByUsername(authenticationRequest.getUsername());
        UserModelDetail userModelDetail = userDetailService.findUserByUsername(authenticationRequest.getUsername());
        UserModelHeader userModelHeader = new UserModelHeader(authenticationRequest.getUsername(), authenticationRequest.getPassword(), userDetailService.getAuthority(userModelDetail.getRoles()), userModelDetail);
        final String token = jwtTokenUtil.generateToken(userModelHeader);
        return ResponseEntity.ok(new JwtResponse(token, userModelHeader.getUserModelDetail()));
//        }
    }
}
