package mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import mapping.struct.MethodData;
import party.detection.unknown.hook.json.JsonClassMapping;
import party.detection.unknown.hook.json.JsonFieldMapping;
import party.detection.unknown.hook.json.JsonMethodMapping;
import party.detection.unknown.io.IOUtil;

/**
 * JSON generator that uses Forge-Gradle's MCP files to generate mappings without needing to access the internet.
 * 
 * @author GenericSkid
 * @since 1/16/2018
 */
public class MCPJsonGen extends AbstractJsonGen {
	private final static String fs = File.separator;

	public MCPJsonGen(String mcVersion, boolean pretty) {
		super(mcVersion, pretty);
	}

	@Override
	public String createJSON() throws Exception {
		// Gradle dir for forge's dumped mappings
		String mcpGradle = fs + ".gradle" + fs + "caches" + fs + "minecraft" + fs + "de" + fs + "oceanlabs" + fs + "mcp"
				+ fs + "mcp_snapshot" + fs;
		File root = new File(System.getProperty("user.home") + mcpGradle);
		if (!root.exists()) {
			p("The directory '" + mcpGradle + "' does not exist.");
			p("You need to run forge-gradle to generate the mappings.");
			System.exit(0);
		}
		// Mappings dir for version
		File dirSRG = IOUtil.getLatestFile(root, f -> f.getName().equals("notch-srg.srg") && f.getParentFile().getParentFile().getName().equals(mcVersion)).getParentFile();
		if (dirSRG == null) {
			p("Could not locate mappings for '" + mcVersion + "' in '" + root.getAbsolutePath() + "'");
			p("Run forge-gradle for the intended version and try again.");
			System.exit(0);
		}
		File dirCSV = dirSRG.getParentFile().getParentFile();
		if (!IOUtil.contains(dirCSV, "fields.csv", "methods.csv")) {
			p("Located SRG, but parent directory did not contain CSV's.");
			p("Run forge-gradle for the intended version and try again.");
			System.exit(0);
		}
		File mcJar = new File(IOUtil.getMCDir(), "versions" + File.separator + mcVersion + File.separator + mcVersion + ".jar");
		if (!mcJar.exists()) {
			p("Could not locate game jar for '" + mcVersion + "' in '" + mcJar.getAbsolutePath() + "'");
			p("Run the version in your minecraft launcher and try again.");
			System.exit(0);
		}
		// Construct map of MCP SRG's to MCP names.
		JsonClassMapping[] config = configure();
		Map<String, String> fieldSrgToMCP = new HashMap<>();
		Map<String, String> methodSrgToMCP = new HashMap<>();
		parseCSVFields(new BufferedReader(new FileReader(new File(dirCSV, "fields.csv"))), fieldSrgToMCP);
		parseCSVMethods(new BufferedReader(new FileReader(new File(dirCSV, "methods.csv"))), methodSrgToMCP);
		p("Finished parsing CSV maps of MCP_SRG->MCP_Name");
		// Construct map of MCP names to obfuscated vanilla names of the given version
		// (mcVersion)
		Map<String, String> classMCPToObf = new HashMap<>();
		Map<String, String> fieldMCPToObf = new HashMap<>();
		Map<MethodData, MethodData> methodMCPToObf = new HashMap<>();
		parseSrg(new BufferedReader(new FileReader(new File(dirSRG, "notch-srg.srg"))), classMCPToObf, fieldSrgToMCP,
				fieldMCPToObf, methodSrgToMCP, methodMCPToObf);
		p("Finished parsing SRG map of MCP_Name->Obf_Name");

		Map<String, String> obfFieldToDesc = new HashMap<>();
		parseFieldDescs(mcJar, obfFieldToDesc);
		p("Created descriptors map using game jar for " + mcVersion);

		updateMappings(config, classMCPToObf, fieldMCPToObf, obfFieldToDesc, methodMCPToObf);
		p("Applying obfuscation for " + mcVersion);

		removeInvalid(config);
		p("Removed invalid entries for " + mcVersion);

		return build(config);
	}
	
	@SuppressWarnings("deprecation")
	private static void parseFieldDescs(File jar, Map<String, String> obfFieldToDesc) throws IOException {
		JarInputStream jis = new JarInputStream(jar.toURL().openStream());
		JarEntry je;
		while ((je = jis.getNextJarEntry()) != null) {
			if (je.getName().endsWith(".class")) {
				ClassReader cr = new ClassReader(jis);
				ClassNode cn = new ClassNode();
				cr.accept(cn, ClassReader.SKIP_CODE);
				// If future releases use aggresive overloading, change this.
				// Fine for now.
				for (FieldNode fn : cn.fields) {
					obfFieldToDesc.put(cn.name + '/' + fn.name, fn.desc);
				}
			}
		}
		jis.close();
	}

	/**
	 * Parses MCP SRG's into the given maps.
	 * 
	 * @param br
	 *            Reader of srg file.
	 * @param classMCPToObf
	 *            Map of MCP names to obfuscated names.
	 * @param fieldSrgToMCP
	 *            Map of field SRG's to MCP names.
	 * @param fieldMCPToObf
	 *            Map of field MCP names to obfuscated names.
	 * @param methodSrgToMCP
	 *            Map of method SRG's to MCP names.
	 * @param methodMCPToObf
	 *            Map of method MCP names to obfuscated names.
	 * @throws IOException
	 *             Thrown if the SRG file could not be parsed.
	 */
	protected void parseSrg(BufferedReader br, Map<String, String> classMCPToObf, Map<String, String> fieldSrgToMCP,
			Map<String, String> fieldMCPToObf, Map<String, String> methodSrgToMCP,
			Map<MethodData, MethodData> methodMCPToObf) throws IOException {
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(" ");
			switch (parts[0]) {
			case "CL:": {
				classMCPToObf.put(parts[2], parts[1]);
				break;
			}
			case "FD:": {
				int splitPoint = parts[2].lastIndexOf('/') + 1;
				String clsBit = parts[2].substring(0, splitPoint);
				String srgField = parts[2].substring(splitPoint);
				String replace = fieldSrgToMCP.get(srgField);
				String fkey = clsBit + (replace != null ? replace : srgField);
				fieldMCPToObf.put(fkey, parts[1]);
				break;
			}
			case "MD:": {
				int splitObf = parts[1].lastIndexOf('/');
				MethodData obf = new MethodData(parts[1].substring(0, splitObf), parts[1].substring(splitObf + 1),
						parts[2]);
				int splitPoint = parts[3].lastIndexOf('/');
				String mcpName = parts[3].substring(splitPoint + 1);
				String replace = methodSrgToMCP.get(mcpName);
				MethodData mcp = new MethodData(parts[3].substring(0, splitPoint), replace != null ? replace : mcpName,
						parts[4]);
				methodMCPToObf.put(mcp, obf);
				break;
			}
			}
		}
	}

	/**
	 * Populates the field map by reading the {@code fields.csv} the BufferedReader
	 * is reading from.
	 * 
	 * @param br
	 *            Reader of csv file.
	 * @param fieldMap
	 *            Map of field srgs to cleartext names.
	 * @throws IOException
	 *             Thrown if csv could not be read.
	 */
	protected void parseCSVFields(BufferedReader br, Map<String, String> fieldMap) throws IOException {
		br.readLine(); // skip the explanation header line
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			// srg --> cleartext
			fieldMap.put(parts[0], parts[1]);
		}
	}

	/**
	 * Populates the method map by reading the {@code methods.csv} the
	 * BufferedReader is reading from.
	 * 
	 * @param br
	 *            Reader of csv file.
	 * @param methodMap
	 *            Map of method srgs to cleartext names.
	 * @throws IOException
	 *             Thrown if csv could not be read.
	 */
	protected void parseCSVMethods(BufferedReader br, Map<String, String> methodMap) throws IOException {
		br.readLine(); // skip the explanation header line
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			// srg --> cleartext
			methodMap.put(parts[0], parts[1]);
		}
	}

	/**
	 * Updates field and method in the config array.
	 * @param config
	 * @param classMCPToObf
	 * @param fieldMCPToObf
	 * @param obfFieldToDesc
	 * @param methodMCPToObf
	 */
	protected void updateMappings(JsonClassMapping[] config, Map<String, String> classMCPToObf,
			Map<String, String> fieldMCPToObf, Map<String, String> obfFieldToDesc,
			Map<MethodData, MethodData> methodMCPToObf) {
		for (JsonClassMapping classMapping : config) {
			String origName = classMapping.getObfName();
			String obfClassName = classMCPToObf.get(origName);
			if (obfClassName != null)
				classMapping.setObfName(obfClassName);
			for (JsonFieldMapping fieldMapping : classMapping.getFieldMappings()) {
				
				for (String mcpAlias : fieldMapping.getMcpAliases()) {
					String obfFieldName = fieldMCPToObf.get(origName + '/' + mcpAlias);
					if (obfFieldName != null) {
						fieldMapping.setObfName(obfFieldName.substring(obfFieldName.lastIndexOf('/') + 1));
						fieldMapping.setDesc(obfFieldToDesc.get(obfFieldName));
						break;
					}
				}

			}
			for (JsonMethodMapping methodMapping : classMapping.getMethodMappings()) {
				MethodData obf = methodMCPToObf.get(new MethodData(origName, methodMapping.getMcpAlias(), methodMapping.getDesc()));
				if (obf != null) {
					methodMapping.setObfName(obf.name);
					methodMapping.setDesc(obf.desc);
				} // else throw new RuntimeException();
			}
		}
	}

	/**
	 * Removes any entries in the config that are irrelevant for the given version of minecraft.
	 * @param config
	 */
	protected void removeInvalid(JsonClassMapping[] config) {
		for (JsonClassMapping classMapping : config) {
			JsonFieldMapping[] fields = classMapping.getFieldMappings();
			for (int i = fields.length - 1; i >= 0; i--) {
				JsonFieldMapping field = fields[i];
				if (field.getObfName() == null) {
					p("\tRemove: " + classMapping.getObfName() + "#" + field.getMcpAliases()[0] + " (" + field.getId()
							+ ")");
					fields[i] = null;
				}
			}
			JsonMethodMapping[] methods = classMapping.getMethodMappings();
			for (int i = methods.length - 1; i >= 0; i--) {
				JsonMethodMapping method = methods[i];
				if (method.getObfName() == null) {
					p("\tRemove: " + classMapping.getObfName() + "#" + method.getMcpAlias() + " (" + method.getId()
							+ ")");
					methods[i] = null;
				}
			}
		}
	}

}
