package mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.google.gson.Gson;

import mapping.struct.GameVersionCFG;
import mapping.struct.GameVersions;
import mapping.struct.MethodData;
import party.detection.unknown.hook.json.JsonClassMapping;

/**
 * JSON generator that uses the MCP export-bot and Mojang API to generate mappings online.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public class MCPOnlineJsonGen extends MCPJsonGen {
	/**
	 * MCP channel to pull mappings from.
	 */
	private final String mcpChannel;

	public MCPOnlineJsonGen(String mcVersion, boolean pretty, String mcpChannel) {
		super(mcVersion, pretty);
		this.mcpChannel = mcpChannel;
	}

	/**
	 * Creates the JSON string for the given version.
	 * 
	 * @param mcVersion
	 *            Minecraft version.
	 * @param mcpChannel
	 *            MCP channel to fetch mappings from.
	 * @param pretty
	 *            Pretty print the output JSON.
	 * @return JSON string of mappings.
	 * @throws IOException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String createJSON() throws Exception {
		p("Version/Channel: " + mcVersion + "/" + mcpChannel);
		// Get mapping of id:name of core classes
		JsonClassMapping[] config = configure();

		// Download MCP mappings into map
		Map<String, Map<String, ArrayList<Double>>> mcpbotVersions = downloadJson(
				"http://export.mcpbot.bspk.rs/versions.json", Map.class);
		long mapVersion = (long) mcpbotVersions.get(mcVersion).get(mcpChannel).stream().mapToDouble(Double::doubleValue)
				.max().orElseThrow(() -> {
					throw new IllegalArgumentException(
							"No " + mcpChannel + " mappings found for version " + mcVersion + "!");
				});
		p("Downloaded version list: " + mcpbotVersions.size() + " game versions found");

		// Construct map of MCP SRG's to MCP names.
		Map<String, String> fieldSrgToMCP = new HashMap<>();
		Map<String, String> methodSrgToMCP = new HashMap<>();
		parseCSV(mcpChannel, mcVersion, mapVersion, fieldSrgToMCP, methodSrgToMCP);
		p("Finished parsing CSV maps of MCP_SRG->MCP_Name");

		// Construct map of MCP names to obfuscated vanilla names of the given version
		// (mcVersion)
		Map<String, String> classMCPToObf = new HashMap<>();
		Map<String, String> fieldMCPToObf = new HashMap<>();
		Map<MethodData, MethodData> methodMCPToObf = new HashMap<>();
		parseSrg(mcVersion, classMCPToObf, fieldSrgToMCP, fieldMCPToObf, methodSrgToMCP, methodMCPToObf);
		p("Finished parsing SRG map of MCP_Name->Obf_Name");

		// Download version manifest containing version info.
		GameVersions gv = downloadJson("https://launchermeta.mojang.com/mc/game/version_manifest.json",
				GameVersions.class);
		GameVersionCFG gcfg = downloadJson(
				Arrays.stream(gv.versions).filter(x -> x.id.equals(mcVersion)).findFirst().get().url,
				GameVersionCFG.class);
		p("Finished downloading game JSON for " + mcVersion);
		Map<String, String> obfFieldToDesc = new HashMap<>();
		parseFieldDescs(gcfg.downloads.client.url, obfFieldToDesc);
		p("Created descriptors map using game jar for " + mcVersion);

		updateMappings(config, classMCPToObf, fieldMCPToObf, obfFieldToDesc, methodMCPToObf);
		p("Applying obfuscation for " + mcVersion);

		removeInvalid(config);
		p("Removed invalid entries for " + mcVersion);

		return build(config);
	}

	/**
	 * Parses the contents of the jar <i>(at the given url)</i> and populates the
	 * given map.
	 * 
	 * @param jarURL
	 *            Jar to read from.
	 * @param obfFieldToDesc
	 *            Map of field names to descriptors.
	 * @throws IOException
	 *             Thrown if the jar could not be parsed.
	 */
	void parseFieldDescs(String jarURL, Map<String, String> obfFieldToDesc) throws IOException {
		JarInputStream jis = new JarInputStream(new URL(jarURL).openStream());
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
	 * @param mcVersion
	 *            Minecraft version.
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
	 *             Thrown if the SRG file for the given minecraft version could not
	 *             be reached <i>(Online file)</i> or if it could not be parsed.
	 */
	void parseSrg(String mcVersion, Map<String, String> classMCPToObf, Map<String, String> fieldSrgToMCP,
			Map<String, String> fieldMCPToObf, Map<String, String> methodSrgToMCP,
			Map<MethodData, MethodData> methodMCPToObf) throws IOException {
		URL url = new URL("http://export.mcpbot.bspk.rs/mcp/{ver}/mcp-{ver}-srg.zip".replace("{ver}", mcVersion));
		ZipInputStream zis = new ZipInputStream(url.openStream());
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			// forge : mcp
			// joined : notch-srg
			if (!"joined.srg".equals(ze.getName()))
				continue;
			BufferedReader br = new BufferedReader(new InputStreamReader(zis));
			super.parseSrg(br, classMCPToObf, fieldSrgToMCP, fieldMCPToObf, methodSrgToMCP, methodMCPToObf);
		}
	}

	/**
	 * Parses MCP CSV's into the given maps.
	 * 
	 * @param mcpChannel
	 *            MCP channel to read from.
	 * @param mcVersion
	 *            Minecraft version.
	 * @param mapVersion
	 *            Version of mappings file.
	 * @param fieldMap
	 *            Map of MCP SRG's to clear-text names.
	 * @param methodMap
	 *            Map of MCP SRG's to clear-text names.
	 * @throws IOException
	 *             Thrown if the SRG file for the given mcp channel / minecraft
	 *             version could not be reached <i>(Online file)</i> or if it oculd
	 *             not be parsed.
	 */
	void parseCSV(String mcpChannel, String mcVersion, long mapVersion, Map<String, String> fieldMap,
			Map<String, String> methodMap) throws IOException {
		URL url = new URL("http://export.mcpbot.bspk.rs/mcp_{channel}/{map}-{ver}/mcp_{channel}-{map}-{ver}.zip"
				.replace("{channel}", mcpChannel).replace("{map}", Long.toString(mapVersion))
				.replace("{ver}", mcVersion));
		System.out.println(url);
		ZipInputStream zis = new ZipInputStream(url.openStream());
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String name = ze.getName();
			// Closing this causes the tool to crash here.
			BufferedReader br = new BufferedReader(new InputStreamReader(zis));
				if ("fields.csv".equals(name)) {
					super.parseCSVFields(br, fieldMap);
				} else if ("methods.csv".equals(name)) {
					super.parseCSVMethods(br, methodMap);
				}
			
		}
		zis.close();
	}

	/**
	 * Downloads the JSON from the given URL.
	 * 
	 * @param url
	 *            URL pointing to JSON file.
	 * @param type
	 *            Type to read object as. For instance, a map.
	 * @return Object of given type.
	 * @throws IOException
	 *             Thrown if the file could not be reached / read.
	 */
	private static <T> T downloadJson(String url, Class<T> type) throws IOException {
		return new Gson().fromJson(new InputStreamReader(new URL(url).openStream()), type);
	}
}
