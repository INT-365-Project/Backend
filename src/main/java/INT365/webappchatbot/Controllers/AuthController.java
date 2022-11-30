package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Configs.JwtTokenUtils;
import INT365.webappchatbot.Models.JwtRequest;
import INT365.webappchatbot.Models.JwtResponse;
import INT365.webappchatbot.Models.UserModelHeader;
import INT365.webappchatbot.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(value = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JwtTokenUtils jwtTokenUtil;
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        UserModelHeader userModelHeader = authService.authenticate(authenticationRequest);
        final String token = jwtTokenUtil.generateToken(userModelHeader);
        return ResponseEntity.ok(new JwtResponse(token, userModelHeader.getUserModelDetail()));
    }
}
