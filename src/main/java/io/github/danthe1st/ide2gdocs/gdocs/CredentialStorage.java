package io.github.danthe1st.ide2gdocs.gdocs;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.intellij.ide.util.PropertiesComponent;

import java.io.IOException;

public interface CredentialStorage {
	Credential loadCredential(Credential.Builder credBuilder) throws IOException;
	void saveCredential(Credential cred) throws IOException;

}
