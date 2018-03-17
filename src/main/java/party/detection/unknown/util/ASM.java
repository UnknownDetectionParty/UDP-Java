package party.detection.unknown.util;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * ASM-based utilities.
 * 
 * @author bloo, GenericSkid
 * @since 2/18/2018
 */
public class ASM implements Opcodes {

	/**
	 * Append a default return opcode to the given method insns list. The type will
	 * be based off of the return value of the given method descriptor. Value is
	 * always {@code 0} for primitives and {@code null} for objects.
	 * 
	 * @param insns
	 *            Method opcode list.
	 * @param methodDesc
	 *            Method descriptor.
	 */
	public static void appendReturn(InsnList insns, String methodDesc) {
		Type retType = Type.getReturnType(methodDesc);
		switch (retType.getSort()) {
		case Type.BOOLEAN:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
			insns.add(new InsnNode(ICONST_0));
			break;
		case Type.LONG:
			insns.add(new InsnNode(LCONST_0));
			break;
		case Type.FLOAT:
			insns.add(new InsnNode(FCONST_0));
			break;
		case Type.DOUBLE:
			insns.add(new InsnNode(DCONST_0));
			break;
		case Type.ARRAY:
		case Type.OBJECT:
			insns.add(new InsnNode(ACONST_NULL));
			break;
		}
		insns.add(new InsnNode(retType.getOpcode(IRETURN)));
	}

	/**
	 * Fetch method in the given class by the given name and descriptor.
	 * 
	 * @param cn
	 *            Class node containing the method.
	 * @param name
	 *            Method name.
	 * @param desc
	 *            Method descriptor.
	 * @return Method matching given values. {@code null} if not found.
	 */
	public static MethodNode getMethod(ClassNode cn, String name, String desc) {
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals(name) && mn.desc.equals(desc)) {
				return mn;
			}
		}
		return null;
	}

	/**
	 * Fetch field in the given class by the given name and descriptor.
	 * 
	 * @param cn
	 *            Class node containing the field.
	 * @param name
	 *            Field name.
	 * @param desc
	 *            Field descriptor.
	 * @return Field matching given values. {@code null} if not found.
	 */
	public static FieldNode getField(ClassNode cn, String name, String desc) {
		for (FieldNode fn : cn.fields) {
			if (fn.name.equals(name) && fn.desc.equals(desc)) {
				return fn;
			}
		}
		return null;
	}

	/**
	 * Creates an array of integers of opcodes for loading variables at the given
	 * local variable indices. For example if a method has the following local
	 * variable table:
	 * 
	 * <pre>
	 * 1: int
	 * 2: float
	 * 3: Object
	 * </pre>
	 * 
	 * Then the int array returned will be:
	 * 
	 * <pre>
	 * { ILOAD, FLOAD, ALOAD }
	 * </pre>
	 * 
	 * @param mn
	 *            Method to parse for local types.
	 * @return Array of opcodes matching local types.
	 */
	public static int[] getLocalLoadInsns(MethodNode mn) {
		final int[] ret = new int[mn.maxLocals];
		Type[] params = Type.getArgumentTypes(mn.desc);
		for (int i = 0; i < params.length; i++) {
			ret[i] = params[i].getOpcode(ILOAD);
		}
		mn.accept(new MethodVisitor(ASM5) {
			@Override
			public void visitVarInsn(int opcode, int var) {
				switch (opcode) {
				case ILOAD:
				case FLOAD:
				case DLOAD:
				case LLOAD:
				case ALOAD:
					ret[var] = opcode;
					break;
				case ISTORE:
					ret[var] = ILOAD;
					break;
				case FSTORE:
					ret[var] = FLOAD;
					break;
				case DSTORE:
					ret[var] = DLOAD;
					break;
				case LSTORE:
					ret[var] = LLOAD;
					break;
				case ASTORE:
					ret[var] = ALOAD;
					break;
				}
			}
		});
		return ret;
	}

}
