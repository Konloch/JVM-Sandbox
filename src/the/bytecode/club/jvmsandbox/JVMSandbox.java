package the.bytecode.club.jvmsandbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

/**
 * JVM Sandbox - Utilize Java Agents and ASM to manipulate then override Java SE classes.
 * 
 * Just a simple POC to block Runtime.exec for any Java jar
 * 
 * How to use this:
 *    0) Download the zip file containing the jar and .bat/.sh files.
 *    1) Click the .bat or .sh depending on your OS
 *    2) Import the jar you wish to sandbox/debug
 *    3) Enter the main and method class it'll invoke
 *    4) If you want to just test to see if it works, run the test.jar class.
 * 
 * Features:
 *     Blocks all Runtime.exec calls
 *     Blocks the process builder
 *     Blocks awt.Robot
 *     Can block JNI (LOL)
 * 
 * @author Konloch
 *
 */

public class JVMSandbox {
	
	private static boolean LOADED = false;
	public static GUI gui;
	
	public static void premain(String args, Instrumentation inst) throws Exception {
		replaceClasses(inst);
	}

	public static void agentmain(String args, Instrumentation inst) throws Exception {
		replaceClasses(inst);
	}
	public static void main(String[] args) throws Exception {
		System.out.println("This was a simple POC for Bytecode Viewer's EZ-Injection plugin, the full version of this is inside of BCV (which is also open sourced)");
		if(args.length == 0) {
			gui = new GUI();
			if(LOADED) {
				if(Runtime.getRuntime().exec("calc.exe") != null) { //ensure it's blocked (windows only, didn't bother adding *nix check)
					showMessage("Warning: For some reason the custom class isn't being loaded, report your JVM and OS to konloch@gmail.com please");
					System.exit(0);
				} else
					gui.setVisible(true);
			} else {
				showMessage("Execute it as a Java Agent, run the .bat or .sh file that came with the zip.");
				System.exit(0);
			}
		} else {
			System.out.println("Command line coming soon, sorry homebre.");
		}
	}
	
	/**
	 * Replaces the Runtime class via instrumentation, transforms the class via ASM
	 * @param inst
	 */
	public static void replaceClasses(Instrumentation inst) {
		for(Class<?> c : inst.getAllLoadedClasses()) {
			if(c.getName().equals("java.lang.Runtime")) {
				try {
					inst.redefineClasses(new java.lang.instrument.ClassDefinition(java.lang.Runtime.class, transformClass(c.getName(), getClassFile(c))));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(c.getName().equals("java.lang.ProcessBuilder")) {
				try {
					inst.redefineClasses(new java.lang.instrument.ClassDefinition(java.lang.ProcessBuilder.class, transformClass(c.getName(), getClassFile(c))));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			/*if(c.getName().equals("java.lang.System")) { //blocks JNI loading?
				LOADEDCOUNT++;
				try {
					inst.redefineClasses(new java.lang.instrument.ClassDefinition(java.lang.System.class, transformClass(c.getName(), getClassFile(c))));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}*/
			if(c.getName().equals("java.awt.Robot")) {
				try {
					inst.redefineClasses(new java.lang.instrument.ClassDefinition(java.lang.ProcessBuilder.class, transformClass(c.getName(), getClassFile(c))));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		LOADED = true;
	}
	
	/**
	 * Use ASM to modify the byte array
	 * @param className
	 * @param classBytes
	 * @return
	 */
	public static byte[] transformClass(String className, byte[] classBytes) {
		if (className.equals("java.lang.Runtime")) {
			ClassReader cr=new ClassReader(classBytes);
			ClassNode cn=new ClassNode();
			cr.accept(cn,ClassReader.EXPAND_FRAMES);
			
			for (Object o : cn.methods.toArray()) {
				MethodNode m = (MethodNode) o;
				if(m.name.equals("exec")) {
					m.instructions.insert(new InsnNode(ARETURN));
					m.instructions.insert(new InsnNode(ACONST_NULL));
				}
			}
			ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		} else if (className.equals("java.lang.ProcessBuilder")) {
			ClassReader cr=new ClassReader(classBytes);
			ClassNode cn=new ClassNode();
			cr.accept(cn,ClassReader.EXPAND_FRAMES);
			
			for (Object o : cn.methods.toArray()) {
				MethodNode m = (MethodNode) o;
				if(m.name.equals("start")) {
					m.instructions.insert(new InsnNode(ARETURN));
					m.instructions.insert(new InsnNode(ACONST_NULL));
				}
			}
			ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		} else if (className.equals("java.lang.System")) { //not used for now, but should block JNI interaction
			ClassReader cr=new ClassReader(classBytes);
			ClassNode cn=new ClassNode();
			cr.accept(cn,ClassReader.EXPAND_FRAMES);
			
			for (Object o : cn.methods.toArray()) {
				MethodNode m = (MethodNode) o;
				if(m.name.equals("loadLibrary")) {
					m.instructions.insert(new InsnNode(ARETURN));
					m.instructions.insert(new InsnNode(ACONST_NULL));
				}
			}
			ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		} else if (className.equals("java.awt.Robot")) {
			ClassReader cr=new ClassReader(classBytes);
			ClassNode cn=new ClassNode();
			cr.accept(cn,ClassReader.EXPAND_FRAMES);
			
			for (Object o : cn.methods.toArray()) {
				MethodNode m = (MethodNode) o;
				if(	m.name.equals("createScreenCapture") 	|| m.name.equals("getPixelColor") ||
					m.name.equals("keyPress") 				|| m.name.equals("keyRelease") ||
					m.name.equals("mouseMove")				|| m.name.equals("mousePress") ||
					m.name.equals("mouseWheel"))
				{
					m.instructions.insert(new InsnNode(ARETURN));
					m.instructions.insert(new InsnNode(ACONST_NULL));
				}
			}
			ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		}
		return classBytes;
	}
	
	/**
	 * Grab the byte array from the loaded Class object
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static byte[] getClassFile(Class<?> clazz) throws IOException {     
	    InputStream is = clazz.getResourceAsStream( "/" + clazz.getName().replace('.', '/') + ".class");
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    int r = 0;
	    byte[] buffer = new byte[8192];
	    while((r=is.read(buffer))>=0) {
	        baos.write(buffer, 0, r);
	    }   
	    return baos.toByteArray();
	}
	
	/**
	 * Load the jar file into the current classloader and return the loaded main Class object
	 * @param jar
	 * @param mainClassName
	 * @return
	 */
	public static Class<?> loadIntoClassloader(String jar, String mainClassName) {
		try {
			ClassPathHack.addFile(jar);
			return ((URLClassLoader) ClassLoader.getSystemClassLoader()).loadClass(mainClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Alert the user to anything
	 * @param message
	 */
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(gui, message);
	}
	
	/**
	 * Verifies if the class has been successfully loaded
	 * @return
	 */
	public static boolean isLoaded() {
		return LOADED;
	}
	
}
