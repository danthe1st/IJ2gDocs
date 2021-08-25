package io.github.danthe1st.ide2gdocs.gdocs;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.intellij.ide.util.PropertiesComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleDocsUploaderFactory {

	private static final String APPLICATION_NAME = "IJ2GDocs";
	private static final List <String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS);
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	private final LocalServerReceiver receiver;
	private final CredentialStorage credentialStorage;

	public GoogleDocsUploaderFactory(CredentialStorage credentialStorage) {
		this.credentialStorage = credentialStorage;
		receiver = new LocalServerReceiver.Builder().setPort(8888).build();
	}

	public GoogleDocsUploader build() throws IOException, GeneralSecurityException {
		GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
		Details oAuthCredentials = loadGoogleOAuthCredentials();
		clientSecrets.setInstalled(oAuthCredentials);
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, SCOPES).setAccessType("offline").build();
		Credential cred = getCredential(flow, httpTransport, oAuthCredentials);
		Docs service = new Docs.Builder(httpTransport, JSON_FACTORY, cred).setApplicationName(APPLICATION_NAME).build();
		return new GoogleDocsUploader(service);
	}

	private Credential getCredential(GoogleAuthorizationCodeFlow flow, NetHttpTransport httpTransport,
	                                 Details authDetails) throws IOException {
		String accessToken = PropertiesComponent.getInstance().getValue("ij2gdocs.accessToken");
		Credential cred=credentialStorage.loadCredential(new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
				.setJsonFactory(JSON_FACTORY).setTransport(httpTransport)
				.setClientAuthentication(new ClientParametersAuthentication(authDetails.getClientId(),
						authDetails.getClientSecret())));
		if(accessToken == null) {
			cred = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
			credentialStorage.saveCredential(cred);
		}
		return cred;
	}

	public void cancel() throws IOException {
		receiver.stop();
	}

	public Details loadGoogleOAuthCredentials() throws IOException {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(credentialStorage.getClass().getResourceAsStream("credentials.txt")), StandardCharsets.UTF_8))) {
			String clientId = br.readLine();
			String clientSecret = br.readLine();
			return GoogleDocsUploader.getCredentialDetails(clientId, clientSecret);
		}
	}
}
