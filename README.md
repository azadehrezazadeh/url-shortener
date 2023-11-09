# url-shortener

tips:

1- KeyCloak 21.1.2 is used for authentication purpose. 
   - User can register through {{keyCloakServer}}/realms/{test}/login-actions/registration?client_id=account-console&tab_id= 
   - Access token generated via {{keyCloakServer}}/realms/{test}/protocol/openid-connect/token
   - sample of some emmbeded data in token:  
      "name": "user1 user1",
      "preferred_username": "user1",
      "given_name": "user1",
      "family_name": "user1",
      "email": "user1@site.ir"
  - urlCount of each user saved as an attribute.

2- Liquibase is used to make database by scripts.

3- Current database is MySQL but in future it may be replaced by NoSQL database.
