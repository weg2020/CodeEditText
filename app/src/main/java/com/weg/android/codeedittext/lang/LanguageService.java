package com.weg.android.codeedittext.lang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weg.android.codeedittext.MainActivity;
import com.weg.android.codeedittext.StreamUtilities;
import com.weg.android.editor.EditorView;
import com.weg.android.editor.syntax.Highlighting;
import com.weg.android.editor.text.TextChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LanguageService implements TextChangeListener {
	
	private LanguageService() {
	}
	
	private static final LanguageService INSTANCE = new LanguageService();
	
	public static LanguageService getInstance() {
		return INSTANCE;
	}
	
	private final List<FileSupport> supports = new ArrayList<>();
	private MainActivity activity;
	private EditorView view;
	private OpenFileModel model;
	
	public void init(MainActivity activity, EditorView view) {
		this.activity = activity;
		this.view = view;
		this.view.addTextChangeListener(this);
	}
	
	public void addSupport(FileSupport support) {
		if (!supports.contains(support)) {
			supports.add(support);
		}
	}
	
	public void open(OpenFileModel fileModel) {
		this.model = fileModel;
		for (FileSupport support : supports) {
			support.fileOpened(fileModel);
		}
		activity.runOnUiThread(() -> {
			try {
				view.setText(StreamUtilities.readFully(fileModel.getReader()));
			} catch (IOException e) {
				view.setText(Objects.requireNonNull(e.getMessage()));
			}
		});
	}
	
	
	@Override
	public void textSet() {
		OpenFileModel fileModel = OpenFileModels.of(model.getName(), view.getText());
		new Thread(() -> {
			for (FileSupport support : supports) {
				for (String extension : support.getFileExtensions()) {
					if (extension.equals(model.getExtension())) {
						Highlighting.Builder builder = new Highlighting.Builder(128);
						for (SyntaxHighlighting highlighting : support.getHighlightingList()) {
							highlighting.highlighting(fileModel, builder);
						}
						activity.runOnUiThread(() -> view.setHighlighting(builder.build()));
						break;
					}
				}
			}
		}).start();
	}
	
	@Override
	public void textChanging(int start, int end, @Nullable CharSequence newText) {
	
	}
	
	@Override
	public void textInserted(int index, @NonNull CharSequence text) {
		OpenFileModel fileModel = OpenFileModels.of(model.getName(), view.getText());
		new Thread(() -> {
			for (FileSupport support : supports) {
				for (String extension : support.getFileExtensions()) {
					if (extension.equals(model.getExtension())) {
						Highlighting.Builder builder = new Highlighting.Builder(128);
						for (SyntaxHighlighting highlighting : support.getHighlightingList()) {
							highlighting.highlighting(fileModel, builder);
						}
						activity.runOnUiThread(() -> view.setHighlighting(builder.build()));
						break;
					}
				}
			}
		}).start();
	}
	
	@Override
	public void textDeleted(int start, int end) {
		OpenFileModel fileModel = OpenFileModels.of(model.getName(), view.getText());
		new Thread(() -> {
			for (FileSupport support : supports) {
				for (String extension : support.getFileExtensions()) {
					if (extension.equals(model.getExtension())) {
						Highlighting.Builder builder = new Highlighting.Builder(128);
						for (SyntaxHighlighting highlighting : support.getHighlightingList()) {
							highlighting.highlighting(fileModel, builder);
						}
						activity.runOnUiThread(() -> view.setHighlighting(builder.build()));
						break;
					}
				}
			}
		}).start();
	}
	
	@Override
	public void textReplaced(int start, int end, @NonNull CharSequence newText) {
		OpenFileModel fileModel = OpenFileModels.of(model.getName(), view.getText());
		new Thread(() -> {
			for (FileSupport support : supports) {
				for (String extension : support.getFileExtensions()) {
					if (extension.equals(model.getExtension())) {
						Highlighting.Builder builder = new Highlighting.Builder(128);
						for (SyntaxHighlighting highlighting : support.getHighlightingList()) {
							highlighting.highlighting(fileModel, builder);
						}
						activity.runOnUiThread(() -> view.setHighlighting(builder.build()));
						break;
					}
				}
			}
		}).start();
	}
}
