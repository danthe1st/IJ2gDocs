package io.github.danthe1st.ij2gdocs.gdocs;

import com.google.api.client.auth.oauth2.Credential;
import com.intellij.ide.util.PropertiesComponent;
import io.github.danthe1st.ide2gdocs.gdocs.CredentialStorage;

import java.io.IOException;

public class IJCredentialStorage implements CredentialStorage {


	@Override
	public Credential loadCredential(Credential.Builder credBuilder) {
		String accessToken = PropertiesComponent.getInstance().getValue("ij2gdocs.accessToken");
		if(accessToken==null){
			return null;
		}
		Credential cred = credBuilder.setTokenServerEncodedUrl(PropertiesComponent.getInstance().getValue("ij2gdocs.tokenServerEncodedUrl")).build();
		cred.setAccessToken(accessToken);
		cred.setRefreshToken(PropertiesComponent.getInstance().getValue("ij2gdocs.refreshToken"));
		String expirationTime = PropertiesComponent.getInstance().getValue("ij2gdocs.expirationTime");
		cred.setExpirationTimeMilliseconds(expirationTime == null ? null : Long.valueOf(expirationTime));
		return cred;
	}

	@Override
	public void saveCredential(Credential cred) {
		PropertiesComponent.getInstance().setValue("ij2gdocs.tokenServerEncodedUrl", cred.getTokenServerEncodedUrl());
		PropertiesComponent.getInstance().setValue("ij2gdocs.accessToken", cred.getAccessToken());
		PropertiesComponent.getInstance().setValue("ij2gdocs.refreshToken", cred.getRefreshToken());
		Long expirationTime = cred.getExpirationTimeMilliseconds();
		PropertiesComponent.getInstance().setValue("ij2gdocs.expirationTime", expirationTime == null ? null : String.valueOf(expirationTime));
	}
}
