# Example of flexible authentication with Security 4.0

## Prerequisites

Either GlassFish server 8 
or Embedded GlassFish 8 (both at least milestone M12 or newer, or a final version). Web distribution is enough but the all/full are recommended.

You can download them from https://glassfish.org

## Build project

```
mvn install
```

## Just run, no questions

If Embedded GlassFish JAR is located at the root of the project as `glassfish-embedded-all.jar`, in the root directory run:

```
java -jar glassfish-embedded-all.jar 'deploy --contextroot openid-server OIDC-provider/target/OIDC-provider-1.0-SNAPSHOT.war' 'deploy --contextroot / security-4.0-example/target/security-4.0-example-1.0-SNAPSHOT.war'
```

Then access http://localhost:8080

## Run as 2 separate apps with Embedded GlassFish

Run the OIDC-provider app:
```
java -jar glassfish-embedded-all.jar 'deploy --contextroot openid-server OIDC-provider/target/OIDC-provider-1.0-SNAPSHOT.war'
```

Run the web app (on port 8090):

```
java -jar glassfish-embedded-all.jar --port=8090 security-4.0-example/target/security-4.0-example-1.0-SNAPSHOT.war
```

Then access http://localhost:8090

## How the app works and runs

The build produces 2 applications, both must be started for the demo:

* OIDC-provider - a dummy OpenID Connect provider which always authenticates as a user, without asking for password
* security-4.0-example - an example web application, which either aithenticates using a form with user/password or delegates authentication to an OpenID Connect provider (in this case, to the OIDC-provider app)

OIDC-provider app must be running at http://localhost:8080/openid-server, while the security-4.0-example can run anywhere. By default, it runs on http://localhost:8080/

