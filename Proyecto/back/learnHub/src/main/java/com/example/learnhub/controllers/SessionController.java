package com.example.learnhub.controllers;

import com.example.learnhub.DTO.Token;
import com.example.learnhub.services.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;


// usado para traer datos de la sesion
@RestController
@RequestMapping("/session")
public class SessionController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final TokenService tokenService;

    public SessionController(OAuth2AuthorizedClientService authorizedClientService, TokenService tokenService) {
        this.authorizedClientService = authorizedClientService;
        this.tokenService = tokenService;
    }

    // usado para los datos del usuario autenticado por Google
    @GetMapping(value = "/dataUser")
    public ResponseEntity<Map<String, Object>> getSessionData() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> sessionData = new HashMap<>();
        if(auth != null && auth.isAuthenticated()) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) auth.getPrincipal();
            sessionData = oAuth2User.getAttributes();
        }
        return new ResponseEntity<>(sessionData,headers, HttpStatus.OK);
    }

    // url para la validacion del token y saber si el usuario es valido.
    @GetMapping(value = "/user")
    public ResponseEntity<Map<String,Object>> getUserInfo(OAuth2AuthenticationToken auth2AuthenticationToken){
        Map<String,Object> map = new HashMap<>();
        String clientRegistrationId = auth2AuthenticationToken.getAuthorizedClientRegistrationId();


        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, auth2AuthenticationToken.getName());
        var tokens = new Token(client.getAccessToken().getTokenValue());

        Boolean valid = tokenService.validateToken(tokens.tokenByGoogle());

        map.put("token by google", tokens.tokenByGoogle());
        map.put("es valido?",valid);
        System.out.println(map);
        return ResponseEntity.ok(map);
    }
}
