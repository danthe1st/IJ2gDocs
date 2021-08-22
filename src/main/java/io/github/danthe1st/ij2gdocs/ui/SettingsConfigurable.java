package io.github.danthe1st.ij2gdocs.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.DialogBuilder;
import io.github.danthe1st.ij2gdocs.PluginBundle;
import io.github.danthe1st.ij2gdocs.actions.MainAction;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsConfigurable implements Configurable {
	@Override
	public String getDisplayName() {
		return PluginBundle.message("name");
	}

	@Override
	public @Nullable JComponent createComponent() {
		JPanel dialogPanel = new JPanel();
		GroupLayout layout = new GroupLayout(dialogPanel);
		dialogPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JButton resetAuthBtn = new JButton(PluginBundle.message("resetAuthBtnText"));

		JLabel resetAuthTextFirstLine = new JLabel(PluginBundle.message("resetAuthTextLine1"));
		JLabel resetAuthTextSecondLine = new JLabel(PluginBundle.message("resetAuthTextLine2"));

		resetAuthBtn.addActionListener(e -> {
			PropertiesComponent.getInstance().unsetValue("ij2gdocs.tokenServerEncodedUrl");
			PropertiesComponent.getInstance().unsetValue("ij2gdocs.accessToken");
			PropertiesComponent.getInstance().unsetValue("ij2gdocs.refreshToken");
			PropertiesComponent.getInstance().unsetValue("ij2gdocs.expirationTime");
			AnAction action = ActionManager.getInstance().getAction("ij2gDocs.mainAction");

			if(action instanceof MainAction) {
				((MainAction) action).resetEditorHandler();
			}
			DialogBuilder dialogBuilder=new DialogBuilder()
					.title(PluginBundle.message("authResetConfirmationTitle"))
					.centerPanel(new JLabel(PluginBundle.message("authConfirmationText")));
			dialogBuilder.removeAllActions();
			dialogBuilder.addCloseButton();
			dialogBuilder.show();
		});

		JPanel fillerPane = new JPanel();

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(resetAuthTextFirstLine)
						.addComponent(resetAuthTextSecondLine)
						.addComponent(resetAuthBtn, 150, 150, 150)
				).addComponent(fillerPane)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(resetAuthTextFirstLine)
						.addComponent(resetAuthTextSecondLine)
						.addComponent(resetAuthBtn, 30, 30, 30))
				.addComponent(fillerPane)
		);


		return dialogPanel;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void apply() {
		//currently, there are no settings to apply
	}
}
