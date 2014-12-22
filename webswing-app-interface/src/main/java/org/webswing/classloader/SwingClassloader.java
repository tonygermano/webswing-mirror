package org.webswing.classloader;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassLoaderRepository;
import org.webswing.util.ClassLoaderUtil;

import sun.security.util.SecurityConstants;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

@SuppressWarnings("restriction")
public class SwingClassloader extends ClassLoader {

    private static BiMap<String, String> classReplacementMapping;
    private static BiMap<String, String> methodReplacementMapping;
    private static BiMap<String, String> methodOverrideMapping;

    static {
        Builder<String, String> classBuilder = new ImmutableBiMap.Builder<String, String>();
        //        classBuilder.put("javax.swing.JComponent", "sk.web.swing.JComponent$$BCEL$$");
        //        classBuilder.put("javax.swing.JButton", "sk.web.swing.JButton$$BCEL$$");
        //        classBuilder.put("javax.swing.JCheckBoxMenuItem", "sk.web.swing.JCheckBoxMenuItem$$BCEL$$");
        //        classBuilder.put("javax.swing.JMenu", "sk.web.swing.JMenu$$BCEL$$");
        //        classBuilder.put("javax.swing.JMenuBar", "sk.web.swing.JMenuBar$$BCEL$$");
        //        classBuilder.put("javax.swing.JMenuItem", "sk.web.swing.JMenuItem$$BCEL$$");
        //        classBuilder.put("javax.swing.JToolBar", "sk.web.swing.JToolBar$$BCEL$$");
        //        classBuilder.put("javax.swing.JPopupMenu", "sk.web.swing.JPopupMenu$$BCEL$$");
        //        classBuilder.put("javax.swing.JRadioButtonMenuItem", "sk.web.swing.JRadioButtonMenuItem$$BCEL$$");
        //        classBuilder.put("javax.swing.JToggleButton", "sk.web.swing.JToggleButton$$BCEL$$");
        //        classBuilder.put("javax.swing.JCheckBox", "sk.web.swing.JCheckBox$$BCEL$$");
        //        classBuilder.put("javax.swing.JRadioButton", "sk.web.swing.JRadioButton$$BCEL$$");
        //        classBuilder.put("javax.swing.Box", "sk.web.swing.Box$$BCEL$$");
        //        classBuilder.put("javax.swing.JColorChooser", "sk.web.swing.JColorChooser$$BCEL$$");
        //        classBuilder.put("javax.swing.JFileChooser", "sk.web.swing.JFileChooser$$BCEL$$");
        //        classBuilder.put("javax.swing.JComboBox", "sk.web.swing.JComboBox$$BCEL$$");
        //        classBuilder.put("javax.swing.JLabel", "sk.web.swing.JLabel$$BCEL$$");
        //        classBuilder.put("javax.swing.JTextField", "sk.web.swing.JTextField$$BCEL$$");
        //        classBuilder.put("javax.swing.JTextArea", "sk.web.swing.JTextArea$$BCEL$$");
        //        classBuilder.put("javax.swing.JTextPane", "sk.web.swing.JTextPane$$BCEL$$");
        //        classBuilder.put("javax.swing.DefaultListCellRenderer", "sk.web.swing.DefaultListCellRenderer$$BCEL$$");
        //        classBuilder.put("javax.swing.table.DefaultTableCellRenderer", "sk.web.swing.table.DefaultTableCellRenderer$$BCEL$$");
        //        classBuilder.put("javax.swing.tree.DefaultTreeCellRenderer", "sk.web.swing.tree.DefaultTreeCellRenderer$$BCEL$$");
        //        classBuilder.put("javax.swing.JList", "sk.web.swing.JList$$BCEL$$");
        //        classBuilder.put("javax.swing.JLayeredPane", "sk.web.swing.JLayeredPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JDesktopPane", "sk.web.swing.JDesktopPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JRootPane", "sk.web.swing.JRootPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JSplitPane", "sk.web.swing.JSplitPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JPanel", "sk.web.swing.JPanel$$BCEL$$");
        //        classBuilder.put("javax.swing.JScrollPane", "sk.web.swing.JScrollPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JScrollBar", "sk.web.swing.JScrollBar$$BCEL$$");
        //        classBuilder.put("javax.swing.JViewPort", "sk.web.swing.JViewPort$$BCEL$$");
        //        classBuilder.put("javax.swing.JOptionPane", "sk.web.swing.JOptionPane$$BCEL$$");
        //        classBuilder.put("javax.swing.JFrame", "org.webswing.containers.WebJFrame");
        //        classBuilder.put("javax.swing.JDialog", "org.webswing.containers.WebJDialog");
        //        classBuilder.put("javax.swing.JWindow", "org.webswing.containers.WebJWindow");
        classReplacementMapping = classBuilder.build();

        Builder<String, String> methodReplacementBuilder = new ImmutableBiMap.Builder<String, String>();
        methodReplacementBuilder.put("java.lang.ClassLoader getSystemClassLoader ()Ljava/lang/ClassLoader;", "org.webswing.special.RedirectedMethods getSystemClassLoader ()Ljava/lang/ClassLoader;");
        methodReplacementBuilder.put("java.lang.ClassLoader getSystemResource (Ljava/lang/String;)Ljava/net/URL;", "org.webswing.special.RedirectedMethods getSystemResource (Ljava/lang/String;)Ljava/net/URL;");
        methodReplacementBuilder.put("java.lang.ClassLoader getSystemResourceAsStream (Ljava/lang/String;)Ljava/io/InputStream;", "org.webswing.special.RedirectedMethods getSystemResourceAsStream (Ljava/lang/String;)Ljava/io/InputStream;");
        methodReplacementBuilder.put("java.lang.ClassLoader getSystemResources (Ljava/lang/String;)Ljava/util/Enumeration;", "org.webswing.special.RedirectedMethods getSystemResources (Ljava/lang/String;)Ljava/util/Enumeration;");
        methodReplacementBuilder.put("javax.swing.RepaintManager setCurrentManager (Ljavax/swing/RepaintManager;)V", "org.webswing.special.RedirectedMethods setCurrentManager (Ljavax/swing/RepaintManager;)V");
        methodReplacementBuilder.put("java.lang.System setErr (Ljava/io/PrintStream;)V", "org.webswing.special.RedirectedMethods dummy ()V");
        methodReplacementBuilder.put("java.lang.System setOut (Ljava/io/PrintStream;)V", "org.webswing.special.RedirectedMethods dummy2 ()V");
        methodReplacementBuilder.put("java.beans.XMLEncoder writeObject (Ljava/lang/Object;)V", "org.webswing.special.RedirectedMethods writeObject (Ljava/lang/Object;)V");
        if (System.getProperty(org.webswing.Constants.SWING_START_SYS_PROP_ISOLATED_FS, "").equalsIgnoreCase("true")) {
            methodReplacementBuilder.put("java.io.File listRoots ()[Ljava/io/File;", "org.webswing.special.RedirectedMethods listRoots ()[Ljava/io/File;");
        }
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/lang/Object;)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/lang/Object;)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "org.webswing.special.RedirectedJOptionPane showInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;)V", "org.webswing.special.RedirectedJOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)V", "org.webswing.special.RedirectedJOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;)V", "org.webswing.special.RedirectedJOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;)I", "org.webswing.special.RedirectedJOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I", "org.webswing.special.RedirectedJOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;II)I", "org.webswing.special.RedirectedJOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;II)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;)I", "org.webswing.special.RedirectedJOptionPane showConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showOptionDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)I", "org.webswing.special.RedirectedJOptionPane showOptionDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;)V", "org.webswing.special.RedirectedJOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)V", "org.webswing.special.RedirectedJOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;)V", "org.webswing.special.RedirectedJOptionPane showInternalMessageDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;)V");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;)I", "org.webswing.special.RedirectedJOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I", "org.webswing.special.RedirectedJOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;II)I", "org.webswing.special.RedirectedJOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;II)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;)I", "org.webswing.special.RedirectedJOptionPane showInternalConfirmDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalOptionDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)I", "org.webswing.special.RedirectedJOptionPane showInternalOptionDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;IILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)I");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/String;", "org.webswing.special.RedirectedJOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/String;");
        //        methodReplacementBuilder.put("javax.swing.JOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "org.webswing.special.RedirectedJOptionPane showInternalInputDialog (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;ILjavax/swing/Icon;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        //        methodReplacementBuilder.put("javax.swing.JColorChooser showDialog (Ljava/awt/Component;Ljava/lang/String;Ljava/awt/Color;)Ljava/awt/Color;", "org.webswing.special.RedirectedJColorChooser showDialog (Ljava/awt/Component;Ljava/lang/String;Ljava/awt/Color;)Ljava/awt/Color;");
        //        methodReplacementBuilder.put("javax.swing.JColorChooser createDialog (Ljava/awt/Component;Ljava/lang/String;ZLjavax/swing/JColorChooser;Ljava/awt/event/ActionListener;Ljava/awt/event/ActionListener;)Ljavax/swing/JDialog;", "org.webswing.special.RedirectedJColorChooser createDialog (Ljava/awt/Component;Ljava/lang/String;ZLjavax/swing/JColorChooser;Ljava/awt/event/ActionListener;Ljava/awt/event/ActionListener;)Ljavax/swing/JDialog;");
        //        methodReplacementBuilder.put("java.awt.Desktop isDesktopSupported ()Z", "org.webswing.special.RedirectedDesktop isDesktopSupported ()Z");
        //        methodReplacementBuilder.put("java.awt.Desktop isSupported (Ljava/awt/Desktop/Action;)Z", "org.webswing.special.RedirectedDesktop isSupported (Ljava/awt/Desktop/Action;)Z");
        //        methodReplacementBuilder.put("java.awt.Desktop browse (Ljava/net/URI;)V", "org.webswing.special.RedirectedDesktop browse (Ljava/net/URI;)V");
        //        methodReplacementBuilder.put("java.awt.Desktop mail (Ljava/net/URI;)V", "org.webswing.special.RedirectedDesktop mail (Ljava/net/URI;)V");
        //        methodReplacementBuilder.put("java.awt.Desktop mail ()V", "org.webswing.special.RedirectedDesktop mail ()V");
        methodReplacementMapping = methodReplacementBuilder.build();

        Builder<String, String> methodOverrideBuilder = new ImmutableBiMap.Builder<String, String>();
        //        methodOverrideBuilder.put("sk.web.swing.JFileChooser$$BCEL$$ createDialog (Ljava/awt/Component;)Ljavax/swing/JDialog;","org.webswing.special.OverridenMethods createDialog");
        methodOverrideMapping = methodOverrideBuilder.build();

    }

    private Hashtable<String, Class<?>> classes = new Hashtable<String, Class<?>>(); // Hashtable is synchronized thus thread-safe
    private String[] ignored_packages;
    private ClassLoaderRepository repository;

    public SwingClassloader(ClassLoader parent) {
        super(parent);
        this.ignored_packages = new String[] { "java.", "javax.", "sun.", "org.xml.sax", "org.omg.CORBA", "org.w3c.dom", "org.webswing.special" };
        this.repository = new ClassLoaderRepository(parent);
    }

    protected synchronized Class<?> loadClass(String class_name, boolean resolve) throws ClassNotFoundException {
        Class<?> cl = null;
        /* First try: lookup hash table.
         */
        if ((cl = classes.get(class_name)) == null) {
            /* Second try: Load system class using system class loader. You better
             * don't mess around with them.
             */
            for (int i = 0; i < ignored_packages.length; i++) {
                if (class_name.startsWith(ignored_packages[i])) {
                    cl = getParent().loadClass(class_name);
                    break;
                }
            }
            if (cl == null) {
                JavaClass clazz = null;
                /* Third try: Special request?
                 */
                if (class_name.indexOf("$$BCEL$$") >= 0) {
                    clazz = createClass(class_name);
                } else { // Fourth try: Load classes via repository
                    if ((clazz = repository.loadClass(class_name)) != null) {
                        clazz = modifyClass(clazz);
                    } else {
                        throw new ClassNotFoundException(class_name);
                    }
                }
                if (clazz != null) {
                    byte[] bytes = clazz.getBytes();
                    java.security.Permissions perms = new java.security.Permissions();
                    perms.add(SecurityConstants.ALL_PERMISSION);
                    String classFilePath = this.getParent().getResource(clazz.getClassName().replace('.', '/') + ".class").toExternalForm();
                    int jarSeparatorIndex = classFilePath.lastIndexOf('!');
                    boolean inJar = classFilePath.startsWith("jar:");
                    classFilePath = jarSeparatorIndex > 0 && inJar ? classFilePath.substring(4, jarSeparatorIndex) : classFilePath;
                    CodeSource source=null;
                    try {
                        source = new CodeSource(new URL(classFilePath), new Certificate[]{});
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        //should not happen
                    }
                    ProtectionDomain allPermDomain = new java.security.ProtectionDomain(source, perms);
                    cl = defineClass(class_name, bytes, 0, bytes.length, allPermDomain);
                } else {
                    cl = Class.forName(class_name);
                }
            }
            if (resolve) {
                resolveClass(cl);
            }
        }
        classes.put(class_name, cl);
        return cl;
    }

    private JavaClass createClass(String class_name) {
        throw new RuntimeException("not creating classes");
    }

    protected JavaClass modifyClass(JavaClass clazz) {

        //do not modify classes placed in this list
        if (ClassLoaderUtil.isInPackage(clazz.getPackageName(), new String[] { "org.webswing" })) {
            return clazz;
        }

        Package s = super.getPackage(clazz.getPackageName());
        if (s == null) {
            super.definePackage(clazz.getPackageName(), null, null, null, null, null, null, null);
        }

        if (!classReplacementMapping.values().contains(clazz.getClassName())) {
            ClassGen cg = new ClassGen(clazz);
            ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
            InstructionFactory f = new InstructionFactory(cg);

            //+++++++++ Rerouting swing component classes to generated subclasses with intercepted paint methods +++++++
            //rerouteSwingClasses(clazz, cg, cp);

            //+++++++++ Intercept paint method if current class is subclass of JComponent +++++++
            //interceptPaintMethod(clazz, cg, cp, f);

            //+++++++++ Reroute (static) methods that require special handling +++++++++++++
            rerouteMehods(clazz, cg, cp, f);

            //+++++++++ Override methods that needs to be modified +++++++++++++
            overrideMehods(cg, cp, f);

            //dump
            //            try {
            //                if (cg.getClassName().equals("org.webswing.special.RedirectedJOptionPane"))
            //                    cg.getJavaClass().dump(cg.getClassName() + ".class");
            //            } catch (Exception ex) {
            //                ex.printStackTrace();
            //            }
            //end dump
            return cg.getJavaClass();
        } else {
            return clazz;
        }
    }

    private void overrideMehods(ClassGen cg, ConstantPoolGen cp, InstructionFactory f) {
        for (String method : methodOverrideMapping.keySet()) {
            String[] md = method.split(" ");
            if (md[0].equals(cg.getClassName())) {
                String methodName = md[1];
                String signature = md[2];
                Type returnType = Type.getReturnType(signature);
                Type[] signatureTypes = Type.getArgumentTypes(signature);
                Method m = cg.containsMethod(methodName, signature);
                InstructionList il = new InstructionList();
                MethodGen mg;

                if (m == null) {
                    mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                            returnType, // return type
                            signatureTypes, ClassLoaderUtil.createArgNames(signatureTypes.length),// arg types
                            methodName, cg.getClassName(), // method, class
                            il, cp);
                } else {
                    mg = new MethodGen(m, cg.getClassName(), cp);
                }

                //call method defined in map
                String[] overridenMd = methodOverrideMapping.get(method).split(" ");
                String overridenClassName = overridenMd[0];
                String overridenMethodName = overridenMd[1];
                Type[] overridenArgs = new Type[signatureTypes.length + 1];
                for (int i = 0; i <= signatureTypes.length; i++) {
                    overridenArgs[i] = i == 0 ? new ObjectType(classReplacementMapping.containsValue(cg.getClassName()) ? classReplacementMapping.inverse().get(cg.getClassName()) : cg.getClassName()) : signatureTypes[i - 1];
                    il.append(new ALOAD(i));
                }
                il.append(f.createInvoke(overridenClassName, overridenMethodName, returnType, overridenArgs, Constants.INVOKESTATIC));
                if (!returnType.equals(Type.VOID)) {
                    il.append(InstructionConstants.RETURN);
                }

                mg.setMaxStack();
                cg.addMethod(mg.getMethod());
                il.dispose();
            }
        }

    }

    private void rerouteMehods(JavaClass clazz, ClassGen cg, ConstantPoolGen cp, InstructionFactory f) {
        Map<Integer, Integer> indexReplacementMap = new HashMap<Integer, Integer>();
        //find methods to replace in current class
        for (String methodDef : methodReplacementMapping.keySet()) {
            String[] md = methodDef.split(" ");
            int methodIndex = cp.lookupMethodref(md[0], md[1], md[2]);
            if (methodIndex != -1) {
                //method found in current class, create a replacement MethodRefConstant in cp
                String[] replace = methodReplacementMapping.get(methodDef).split(" ");
                int replacementIndex = cp.addMethodref(replace[0], replace[1], replace[2]);
                indexReplacementMap.put(methodIndex, replacementIndex);
            }
        }
        //replace methodRef indexes in instructions
        if (indexReplacementMap.size() > 0) {
            for (Method m : cg.getMethods()) {
                MethodGen mg = new MethodGen(m, clazz.getClassName(), cp);
                InstructionList il = mg.getInstructionList();
                if (il != null) {
                    boolean dirtyFlag = false;
                    for (Instruction instruction : il.getInstructions()) {
                        if (instruction instanceof CPInstruction) {
                            CPInstruction i = (CPInstruction) instruction;
                            if (indexReplacementMap.containsKey(i.getIndex())) {
                                InstructionHandle handle = ClassLoaderUtil.findInstructionHandle(il, i);
                                INVOKESTATIC replacedInstruction = new INVOKESTATIC(indexReplacementMap.get(i.getIndex()));
                                handle.setInstruction(replacedInstruction);
                                dirtyFlag = true;
                            }
                        }
                    }
                    if (dirtyFlag) {
                        il.setPositions();
                        mg.setInstructionList(il);
                        mg.setMaxStack();
                        mg.setMaxLocals();
                        mg.removeLineNumbers();
                        cg.replaceMethod(m, mg.getMethod());
                        il.dispose();
                    }
                }
            }

        }
    }

    @SuppressWarnings("unused")
    private void rerouteSwingClasses(JavaClass clazz, ClassGen cg, ConstantPoolGen cp) {
        //Reroute all swing components to our own proxy component
        //1.add class constant to constant pool
        for (String jComponentName : classReplacementMapping.keySet()) {
            int jComponentClassConstantPosition = cp.lookupClass(jComponentName);
            if (jComponentClassConstantPosition != -1) {
                cp.addClass(classReplacementMapping.get(jComponentName));
            }
        }
        //2.replace superclass type
        if (classReplacementMapping.containsKey(cg.getSuperclassName())) {
            cg.setSuperclassName(classReplacementMapping.get(cg.getSuperclassName()));
        }

        //3.reroute instructions that refer to changed classes, except for all "instanceof"/"checkcast" instructions.
        for (Method m : cg.getMethods()) {
            MethodGen mg = new MethodGen(m, clazz.getClassName(), cp);
            InstructionList il = mg.getInstructionList();
            if (il != null) {
                boolean dirtyFlag = false;
                for (Instruction instruction : il.getInstructions()) {
                    if (instruction instanceof CPInstruction) {
                        CPInstruction i = (CPInstruction) instruction;
                        Constant instConstant = cp.getConstant(i.getIndex());
                        if (instConstant instanceof ConstantClass) {
                            ConstantClass classConstant = (ConstantClass) instConstant;
                            String referencedClassName = ((String) classConstant.getConstantValue(cp.getConstantPool())).replace("/", ".");
                            if (classReplacementMapping.containsKey(referencedClassName)) {
                                if (i instanceof INSTANCEOF || i instanceof CHECKCAST) {
                                    //dont change;
                                    continue;
                                }
                                String myProxyClassName = classReplacementMapping.get(referencedClassName);
                                int myProxyConstantClassPosition = cp.lookupClass(myProxyClassName);
                                InstructionHandle handle = ClassLoaderUtil.findInstructionHandle(il, i);
                                i.setIndex(myProxyConstantClassPosition);
                                handle.setInstruction(i);
                                dirtyFlag = true;
                            }
                        }

                    }
                }
                if (dirtyFlag) {
                    il.setPositions();
                    mg.setInstructionList(il);
                    mg.setMaxStack();
                    mg.setMaxLocals();
                    mg.removeLineNumbers();
                    cg.replaceMethod(m, mg.getMethod());
                    il.dispose();
                }
            }
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream result = super.getResourceAsStream(name);
        if (result == null) {
            result = getParent().getResourceAsStream(name);
        }
        return result;
    }

}
