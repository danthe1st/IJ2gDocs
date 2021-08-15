package io.github.danthe1st.ij2gdocs.ui;

import com.intellij.openapi.ui.DialogWrapper;
import io.github.danthe1st.ij2gdocs.PluginBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EnterDocumentIdDialog extends DialogWrapper {
	private JTextField input=new JTextField();
	public EnterDocumentIdDialog() {
		super(false);
		setTitle(PluginBundle.message("documentIdPromptTitle"));
		init();
	}

	@Override
	protected @Nullable JComponent createCenterPanel() {
		JPanel dialogPanel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(PluginBundle.message("documentIdPromptText"));
		label.setPreferredSize(new Dimension(100, 30));
		dialogPanel.add(label, BorderLayout.NORTH);

		input.setPreferredSize(new Dimension(150, 30));
		dialogPanel.add(input, BorderLayout.CENTER);

		return dialogPanel;
	}

	public String waitForDocumentId() {
		return showAndGet()?input.getText():"";
	}
}
