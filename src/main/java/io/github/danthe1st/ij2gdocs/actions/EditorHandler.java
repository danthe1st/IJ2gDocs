package io.github.danthe1st.ij2gdocs.actions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import io.github.danthe1st.ide2gdocs.gdocs.GoogleDocsUploader;
import io.github.danthe1st.ide2gdocs.gdocs.GoogleDocsUploaderFactory;
import io.github.danthe1st.ij2gdocs.gdocs.IJCredentialStorage;
import io.github.danthe1st.ij2gdocs.ui.EnterDocumentIdDialog;
import io.github.danthe1st.ij2gdocs.ui.OAuthWaitDialog;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.CredentialException;
import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class EditorHandler implements DocumentListener {
	private Document document;
	private final GoogleDocsUploader docsUploader;

	public EditorHandler() throws GeneralSecurityException, IOException {
		docsUploader = setupOAuth2();
	}

	private static class UploaderHolder {
		private GoogleDocsUploader uploader;
		private IOException ioe;
		private GeneralSecurityException gse;
		private boolean finished;
		private volatile boolean cancelled;
	}

	private static GoogleDocsUploader setupOAuth2()
			throws IOException, GeneralSecurityException {
		GoogleDocsUploaderFactory uploaderFactory = new GoogleDocsUploaderFactory(new IJCredentialStorage());
		UploaderHolder uploaderHolder = new UploaderHolder();
		OAuthWaitDialog dlg = new OAuthWaitDialog();
		Thread uploaderCreationThread = new Thread(() -> {
			try {
				uploaderHolder.uploader = uploaderFactory.build();
			} catch(GeneralSecurityException e) {
				uploaderHolder.gse = e;
			} catch(IOException e) {
				uploaderHolder.ioe = e;
			}catch(NullPointerException e){
				if(!uploaderHolder.cancelled){
					throw e;
				}
			} finally {
				uploaderHolder.finished = true;
				SwingUtilities.invokeLater(() -> dlg.close(0));
			}
		});
		uploaderCreationThread.start();
		dlg.show();
		if(!uploaderHolder.finished) {
			uploaderHolder.cancelled=true;
			uploaderFactory.cancel();
			throw new CredentialException("Authorization cancelled");
		} else if(uploaderHolder.uploader != null) {
			return uploaderHolder.uploader;
		} else if(uploaderHolder.ioe != null) {
			throw uploaderHolder.ioe;
		} else if(uploaderHolder.gse != null) {
			throw uploaderHolder.gse;
		} else {
			throw new IllegalStateException("creating google docs uploader failed in an unknown way");
		}
	}

	@Override
	public void documentChanged(@NotNull DocumentEvent event) {
		if(document != null && document.equals(event.getDocument())) {
			if(event.isWholeTextReplaced()||event.getMoveOffset()!=event.getOffset()){
				docsUploader.overwriteEverything(document.getText(), IOException::printStackTrace);
			}else{
				docsUploader.overwritePart(document.getText().substring(event.getOffset(),event.getOffset()+event.getNewLength()),event.getOffset(),event.getOldLength(),document.getText(),IOException::printStackTrace);
			}
		}else{
			event.getDocument().removeDocumentListener(this);
		}
	}

	public void setDocument(Document document) throws IOException {
		if(document == null) {
			disable();
			this.document=null;
		} else {
			EnterDocumentIdDialog dlg = new EnterDocumentIdDialog();
			String docId = dlg.waitForDocumentId();
			if(docId.isEmpty()) {
				disable();
			}else{
				docsUploader.setDocument(docId);
				enable(document);
				docsUploader.overwriteEverything(document.getText(), IOException::printStackTrace);
			}
		}
	}

	public Document getDocument() {
		return document;
	}

	private void enable(Document document) {
		this.document=document;
		document.addDocumentListener(this);
	}

	private void disable() {
		if(document!=null){
			document.removeDocumentListener(this);
			this.document=null;
		}
	}


}
