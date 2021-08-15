package io.github.danthe1st.ij2gdocs.actions;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import io.github.danthe1st.ij2gdocs.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainAction extends AnAction {

	private EditorHandler editorHandler;

	private static final Logger log = Logger.getInstance(MainAction.class);

	private static final NotificationGroup NOTIFICATION_GROUP =
			new NotificationGroup("IJ2GDocs", NotificationDisplayType.BALLOON, true);

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getProject();
		if(project == null) {
			log.error("Tried to mirror a file to Google Docs without an open project");
			return;
		}
		try {
			if(editorHandler == null) {
				editorHandler = new EditorHandler();
			}
			Document currentDoc = editorHandler.getDocument();
			if(currentDoc == null) {
				Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
				if(editor == null) {
					notifyError(project, PluginBundle.message("missingEditor"));
					return;
				}
				currentDoc = editor.getDocument();
			} else {
				currentDoc = null;
			}
			editorHandler.setDocument(currentDoc);
		} catch(GeneralSecurityException | IOException ex) {
			String errorMessage = ex instanceof IOException ? PluginBundle.message("gDocsIOE") : PluginBundle.message("gDocsSecurityException");
			errorMessage += ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
			notifyError(project, errorMessage);
			log.warn(errorMessage, ex);
		}
	}

	public static void notifyError(Project project, String content) {
		NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
				.notify(project);
	}
}
