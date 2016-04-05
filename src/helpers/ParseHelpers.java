package helpers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import astparser.JavaASTParser;

/**
 * Helper functions for parsing arguments or properties files.
 * 
 * @author themis
 */
public class ParseHelpers {

	/**
	 * Parses the command line arguments.
	 * 
	 * @param args the arguments to be parsed.
	 * @return a string with the values of the arguments.
	 */
	public static String[] parseArgs(String[] args) {
		List<String> col = new ArrayList<String>();
		for (String arg : args) {
			String narg = arg.trim();
			if (narg.contains("=")) {
				for (String n : narg.split("=")) {
					col.add(n);
				}
			} else
				col.add(arg.trim());
		}
		boolean sproject = false;
		boolean sfile = false;
		boolean srepr = false;
		String project = "";
		String file = "";
		String repr = "";
		for (String c : col) {
			if (c.startsWith("-project")) {
				sproject = true;
				sfile = false;
				srepr = false;
			} else if (c.startsWith("-file")) {
				sproject = false;
				sfile = true;
				srepr = false;
			} else if (c.startsWith("-repr")) {
				sproject = false;
				sfile = false;
				srepr = true;
			} else {
				if (sproject)
					project += c + " ";
				else if (sfile)
					file += c + " ";
				else if (srepr)
					repr += c + " ";
			}
		}
		project = project.trim();
		file = file.trim();
		return new String[] { project.trim(), file.trim(), repr.trim().toUpperCase() };
	}

	/**
	 * Returns the location of the properties file.
	 * 
	 * @param filename the filename of the properties file.
	 * @return the absolute path of the properties file.
	 */
	public static String getPropertiesFileLocation(String filename) {
		URI propertiesFile = null;
		try {
			propertiesFile = JavaASTParser.class.getProtectionDomain().getCodeSource().getLocation().toURI()
					.resolve(filename);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (propertiesFile != null && new File(propertiesFile.getPath()).exists())
			return propertiesFile.getPath();
		else
			return filename;
	}

	/**
	 * Parses a properties file with the OMIT and ASIS types of nodes.
	 * 
	 * @param filename the filename of the properties file
	 * @return an ArrayList containing node types and category (ASIS or OMIT).
	 */
	public static ArrayList<String> parseProperties(String filename) {
		ArrayList<String> rules = new ArrayList<String>();
		if (new File(filename).isFile()) {
			String propertiesString = FileSystemHelpers.readFileToString(filename);
			String lines[] = propertiesString.split("\\r?\\n");
			for (String line : lines) {
				line = line.trim();
				if (line.startsWith("LEAF")) {
					String[] sline = line.split("=")[1].trim().split(",");
					for (String string : sline)
						rules.add(string.trim() + "=LEAF");
				} else if (line.startsWith("OMIT")) {
					String[] sline = line.split("=")[1].trim().split(",");
					for (String string : sline)
						rules.add(string.trim() + "=OMIT");
				}
			}
		}
		return rules;
	}

}
