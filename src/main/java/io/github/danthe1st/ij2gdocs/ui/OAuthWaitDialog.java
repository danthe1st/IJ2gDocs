package io.github.danthe1st.ij2gdocs.ui;

import com.intellij.openapi.ui.DialogWrapper;
import io.github.danthe1st.ij2gdocs.PluginBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OAuthWaitDialog extends DialogWrapper {
	public OAuthWaitDialog() {
		super(false);
		setTitle(PluginBundle.message("authorizePromptTitle"));
		setOKActionEnabled(false);
		init();
	}

	@Override
	protected @Nullable JComponent createCenterPanel() {
		JPanel dialogPanel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(PluginBundle.message("authorizePromptText"));
		label.setPreferredSize(new Dimension(100, 20));
		dialogPanel.add(label, BorderLayout.CENTER);

		return dialogPanel;
	}

	@Override
	protected @NotNull JPanel createButtonsPanel(@NotNull List <? extends JButton> buttons) {
		return super.createButtonsPanel(buttons);
	}

	@Override
	protected void createDefaultActions() {
		super.createDefaultActions();
	}

	@Override
	protected Action @NotNull [] createActions() {
		return new Action[]{myCancelAction};
	}
}
