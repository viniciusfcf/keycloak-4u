package br.com.gilvansfilho.spi.authenticator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class CustomAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        List<String> clients = getClients(context);
        System.out.println("Clients " + clients);
        System.out.println("Required Actions");
        context.getUser().getRequiredActionsStream().forEach(System.out::println);
        System.out.println("----");
        ClientModel client = context.getAuthenticationSession().getClient();
        // String username = context.getUser().getUsername();
        boolean passwordlessCadastrado = passwordlessCadastrado(context);

        if (clients.contains(client.getClientId()) && !passwordlessCadastrado) {
            context.getUser().addRequiredAction("webauthn-register-passwordless");
        }

        System.out.println("----");
        context.success();
    }

    private boolean passwordlessCadastrado(AuthenticationFlowContext context) {
        String type = "webauthn-passwordless";
        Iterator<CredentialModel> iterator = context.getUser().credentialManager().getStoredCredentialsStream()
                .iterator();
        while (iterator.hasNext()) {
            CredentialModel credential = iterator.next();
            if (credential.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getClients(AuthenticationFlowContext context) {
        String clients = context.getAuthenticatorConfig().getConfig().get(CustomAuthenticatorFactory.PROPERTY_NAME);
        if (clients == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(clients.split(","));
    }

    @Override
    public void action(AuthenticationFlowContext context) {

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {

    }

}