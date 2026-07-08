# Analysis

## A01:2025 Broken Access Control
Anyone can access the endpoints because no security is implemented.

## A02:2025 Security Misconfiguration
Because there is no security implemented it lacks configuration that will ensure safety.

## A10:2025 Mishandling of Exceptional Conditions
There is no ControllerAdvice implemented to handle exceptions as of now.


# Resolving

## According to spring security, the adding of security with configured UserDetailService covers the following.

Require authentication to every URL in your application

Generate a login form for you

Let the user with a Username of user and a Password of password authenticate with form based authentication

Let the user logout

CSRF attack prevention (Disabled right now for testing. Can be dissabled if switching to JWT)

Session Fixation protection

Security Header integration:

    HTTP Strict Transport Security for secure requests

    X-Content-Type-Options integration

    Cache Control (which you can override later in your application to allow caching of your static resources)

    X-XSS-Protection integration

    X-Frame-Options integration to help prevent Clickjacking

Integration with the following Servlet API methods:

    HttpServletRequest#getRemoteUser()

    HttpServletRequest#getUserPrincipal()

    HttpServletRequest#isUserInRole(java.lang.String)

    HttpServletRequest#login(java.lang.String, java.lang.String)

    HttpServletRequest#logout()
