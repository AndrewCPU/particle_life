package net.andrewcpu.particle.util;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class ResourceLoader {
	private static ResourceLoader instance = null;
	public static ResourceLoader getInstance() {
		if(instance == null){
			instance = new ResourceLoader();
		}
		return instance;
	}

	public String loadTextFile(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String file = "";
		String line;
		try {
			while((line = bufferedReader.readLine()) != null){
				file += line + "\n";
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return file;
	}

	public Font loadFont(String path) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT,this.getClass().getResourceAsStream(path));
			font = font.deriveFont(16.0f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font;
		} catch (FontFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
