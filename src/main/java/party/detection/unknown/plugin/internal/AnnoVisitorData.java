package party.detection.unknown.plugin.internal;

import org.objectweb.asm.*;

import party.detection.unknown.plugin.PluginPack.PluginClass;
import party.detection.unknown.plugin.annotations.Plugin;
import party.detection.unknown.plugin.annotations.PluginGroup;

public class AnnoVisitorData extends ClassVisitor implements Opcodes {

	private final PluginClass data;

	public AnnoVisitorData(PluginClass data) {
		super(ASM5);
		this.data = data;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
		data.setClassName(name.replace("/", "."));
		super.visit(version, access, name, signature, superName, interfaces);
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
		} else if (desc.equals(Type.getDescriptor(PluginGroup.class))) {
			return new AnnotationVisitor(api, super.visitAnnotation(desc, visible)) {
				@Override
				public void visit(String name, Object value) {
					if (name.equals("value")) {
						data.setUpstreamGroup((String) value);
					}
					super.visit(name, value);
				}
			};
		}
		return super.visitAnnotation(desc, visible);
	}
}
