package com.example.capstoneproject;

public class LinkedInConstraints {

    // LinkedIn OAuth 2.0 Client ID (Application-specific)
    public static final String CLIENT_ID = "Given in the LinkedIn App Auth tab";

    // LinkedIn OAuth 2.0 Client Secret (Application-specific)
    public static final String CLIENT_SECRET = "Given in the LinkedIn App Auth tab";

    // Redirect URI - Where LinkedIn will redirect the user after authorization
    // This should match the redirect URI registered in your LinkedIn app settings
    public static final String REDIRECT_URI = "Specified when creating the LinkedIn App";

    // OAuth 2.0 Scopes - Defines the level of access requested from the user
    // Includes permissions to access profile info, social posts, and email
    public static final String SCOPE = "openid profile w_member_social email";

    // Authorization URL - Used to request authorization from the user
    public static final String AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";

    // Token URL - Used to exchange the authorization code for an access token
    public static final String TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
}