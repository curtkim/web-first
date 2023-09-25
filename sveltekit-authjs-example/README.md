https://medium.com/@uriser/authentication-in-sveltekit-with-auth-js-7ff505d584c4


## mongo

    > db.users.find();
    { 
        "_id" : ObjectId("6510fbb7d897d64621995d98"), 
        "name" : 
        "email" : 
        "image" : 
        "emailVerified" : null 
    }
    > db.sessions.find();
    { 
        "_id" : ObjectId("6510fbb7d897d64621995d9a"), 
        "sessionToken" : "f1d7ba06-8b22-483e-b37a-c145127cb4ea", 
        "userId" : ObjectId("6510fbb7d897d64621995d98"), 
        "expires" : ISODate("2023-10-25T03:17:11.864Z") 
    }
    > db.accounts.find();
    { 
        "_id" : ObjectId("6510fbb7d897d64621995d99"), 
        "access_token" : 
        "id_token" : 
        "expires_at" : 1695615430, 
        "scope" : "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid", 
        "token_type" : "bearer", 
        "providerAccountId" : 
        "provider" : "google", 
        "type" : "oidc", 
        "userId" : ObjectId("6510fbb7d897d64621995d98") 
    }