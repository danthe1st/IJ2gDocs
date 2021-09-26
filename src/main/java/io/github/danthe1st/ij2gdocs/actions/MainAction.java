package io.github.danthe1st.ij2gdocs.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import io.github.danthe1st.ij2gdocs.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainAction extends AnAction implements DumbAware {

	private EditorHandler editorHandler;

	private static final Logger log = Logger.getInstance(MainAction.class);

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
			notifyInfo(project,PluginBundle.message(currentDoc==null? "mirrorEndInfo" : "mirrorStartInfo"));

		} catch(GeneralSecurityException | IOException ex) {
			String errorMessage = ex instanceof IOException ? PluginBundle.message("gDocsIOE") : PluginBundle.message("gDocsSecurityException");
			errorMessage += ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
			notifyError(project, errorMessage);
			log.warn(errorMessage, ex);
		}
	}

	@Override
	public void update(AnActionEvent e){
		Presentation presentation = e.getPresentation();
		Project project = e.getProject();
		if(project == null) {
			presentation.setEnabled(false);
			return;
		}
		presentation.setEnabled(true);
		if(editorHandler == null||editorHandler.getDocument()==null){
			presentation.setText(PluginBundle.message("actionTextEnable"));
		}else{
			presentation.setText(PluginBundle.message("actionTextDisable"));
		}
	}

	public static void notifyInfo(Project project, String content) {
		notify(project,content,NotificationType.INFORMATION);
	}
	public static void notifyError(Project project, String content) {
		notify(project,content,NotificationType.ERROR);
	}
	private static void notify(Project project,String content,NotificationType notifType){
		NotificationGroupManager.getInstance()
				.getNotificationGroup("ij2gDocs.notifs")
				.createNotification(content,notifType)
				.notify(project);
	}

	public void resetEditorHandler() {
		if(editorHandler!=null){
			try {
				editorHandler.setDocument(null);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		editorHandler=null;
	}
}
