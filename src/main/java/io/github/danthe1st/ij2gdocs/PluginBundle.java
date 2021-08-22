package io.github.danthe1st.ij2gdocs;

import com.intellij.AbstractBundle;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class PluginBundle extends AbstractBundle {
	private static final String BUNDLE = "io.github.danthe1st.ij2gdocs.messages";

	private static final PluginBundle INSTANCE;

	private PluginBundle() {
		super(BUNDLE);
	}

	static {
		INSTANCE = new PluginBundle();
	}

	public static String message(@PropertyKey(resourceBundle = BUNDLE) @NotNull String key, @NotNull Object... params) {
		return INSTANCE.getMessage(key, params);
	}

	public static Supplier<String> messagePointer(@PropertyKey(resourceBundle = BUNDLE) @NotNull String key, @NotNull Object... params) {
		return INSTANCE.getLazyMessage(key, params);
	}
}
