package party.detection.unknown.plugin.internal;

import org.objectweb.asm.*;

import party.detection.unknown.plugin.PluginData;
import party.detection.unknown.plugin.annotations.Plugin;

public class AnnoVisitorData extends ClassVisitor implements Opcodes {

	private PluginData data;

	public AnnoVisitorData(PluginData data) {
		super(ASM5);
		this.data = data;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals(Type.getDescriptor(Plugin.class))) {
			return new AnnotationVisitor(api, super.visitAnnotation(desc, visible)) {
				@Override
				public void visit(String name, Object value) {
					// Set plugin data from annotation data
					String s = (String) value;
					switch (name) {
					case "id":
						data.setUniqueID(s);
						break;
					case "name":
						data.setName(s);
						break;
					case "author":
						data.setAuthor(s);
						break;
					case "description":
						data.setDescription(s);
						break;
					}
					super.visit(name, value);
				}

				@Override
				public AnnotationVisitor visitArray(String name) {
					if ("versions".equals(name)) {
						return new AnnotationVisitor(api, super.visitArray(name)) {
							@Override
							public void visit(String name, Object value) {
								data.addVersion((String) value);
							}
						};
					}
					return super.visitArray(name);
				}
			};
		}
		return super.visitAnnotation(desc, visible);
	}
}
