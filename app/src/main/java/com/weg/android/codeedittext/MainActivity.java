package com.weg.android.codeedittext;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.weg.android.codeedittext.lang.LanguageService;
import com.weg.android.codeedittext.lang.OpenFileModel;
import com.weg.android.codeedittext.lang.OpenFileModels;
import com.weg.android.codeedittext.lang.typescript.TypeScriptFileSupport;
import com.weg.android.editor.EditorView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
	
	EditorView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new EditorView(this);
		setContentView(view);
		//view.setColorScheme(new ColorSchemeDark());
		LanguageService.getInstance().init(this, view);
		LanguageService.getInstance().addSupport(new TypeScriptFileSupport());
		openFile("pieceTreeBase.ts");
	}
	
	public void openFile(String fileName) {
		new Thread(() -> {
			OpenFileModel model;
			try (InputStream is = getAssets().open(fileName)) {
				String text = StreamUtilities.readFully(is);
				model = OpenFileModels.of(fileName, text);
			} catch (Exception e) {
				e.printStackTrace();
				model = OpenFileModels.of(fileName, e.getMessage());
			}
			LanguageService.getInstance().open(model);
		}).start();
	}
}