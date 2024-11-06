//package com.RNR_CarSaleApp.carsale.mailing;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.RNR_CarSaleApp.carsale.model.Users;
//
//public class AccountVerificationEmailContext extends AbstractEmailContext {
//
//
//
//    private String token;
//
//    @Override
//    public <T> void init(T context) {
//        Users users = (Users) context;
//
//        put("name", users.getName());
//        setTemplateLocation("mailing/email-verification");
//        setSubject("Complete Your Registration");
//        setFrom("kanymuno@gmail.com");
//        setTo(users.getEmail());
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//        put("token", token);
//    }
//
//    public void buildVerificationUrl(final String baseURL, final String token) {
//        final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
//                .path("/register/verify").queryParam("token", token).toUriString();
//        put("verificationURL", url);
//    }
//
//
//}
