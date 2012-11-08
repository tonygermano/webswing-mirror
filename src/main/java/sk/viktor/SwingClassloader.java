package sk.viktor;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.SyntheticRepository;

import sk.viktor.ignored.PaintManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

public class SwingClassloader extends org.apache.bcel.util.ClassLoader {

    private static BiMap<String, String> classReplacementMapping;

    static {
        Builder<String, String> builder = new ImmutableBiMap.Builder<String, String>();
        builder.put("javax.swing.JComponent", "sk.web.swing.JComponent$$BCEL$$");
        builder.put("javax.swing.JButton", "sk.web.swing.JButton$$BCEL$$");
        builder.put("javax.swing.JCheckBoxMenuItem", "sk.web.swing.JCheckBoxMenuItem$$BCEL$$");
        builder.put("javax.swing.JMenu", "sk.web.swing.JMenu$$BCEL$$");
        builder.put("javax.swing.JMenuBar", "sk.web.swing.JMenuBar$$BCEL$$");
        builder.put("javax.swing.JMenuItem", "sk.web.swing.JMenuItem$$BCEL$$");
        builder.put("javax.swing.JToolBar", "sk.web.swing.JToolBar$$BCEL$$");
        builder.put("javax.swing.JPopupMenu", "sk.web.swing.JPopupMenu$$BCEL$$");
        builder.put("javax.swing.JRadioButtonMenuItem", "sk.web.swing.JRadioButtonMenuItem$$BCEL$$");
        builder.put("javax.swing.JToggleButton", "sk.web.swing.JToggleButton$$BCEL$$");
        builder.put("javax.swing.JCheckBox", "sk.web.swing.JCheckBox$$BCEL$$");
        builder.put("javax.swing.JRadioButton", "sk.web.swing.JRadioButton$$BCEL$$");
        builder.put("javax.swing.Box", "sk.web.swing.Box$$BCEL$$");
        builder.put("javax.swing.JColorChooser", "sk.web.swing.JColorChooser$$BCEL$$");
        builder.put("javax.swing.JFileChooser", "sk.web.swing.JFileChooser$$BCEL$$");
        builder.put("javax.swing.JComboBox", "sk.web.swing.JComboBox$$BCEL$$");
        builder.put("javax.swing.JLabel", "sk.web.swing.JLabel$$BCEL$$");
        builder.put("javax.swing.JTextField", "sk.web.swing.JTextField$$BCEL$$");
        builder.put("javax.swing.JTextArea", "sk.web.swing.JTextArea$$BCEL$$");
        builder.put("javax.swing.JTextPane", "sk.web.swing.JTextPane$$BCEL$$");
        builder.put("javax.swing.DefaultListCellRenderer", "sk.web.swing.DefaultListCellRenderer$$BCEL$$");
        builder.put("javax.swing.table.DefaultTableCellRenderer", "sk.web.swing.table.DefaultTableCellRenderer$$BCEL$$");
        builder.put("javax.swing.tree.DefaultTreeCellRenderer", "sk.web.swing.tree.DefaultTreeCellRenderer$$BCEL$$");
        builder.put("javax.swing.JList", "sk.web.swing.JList$$BCEL$$");
        builder.put("javax.swing.JLayeredPane", "sk.web.swing.JLayeredPane$$BCEL$$");
        builder.put("javax.swing.JDesktopPane", "sk.web.swing.JDesktopPane$$BCEL$$");
        builder.put("javax.swing.JRootPane", "sk.web.swing.JRootPane$$BCEL$$");
        builder.put("javax.swing.JSplitPane", "sk.web.swing.JSplitPane$$BCEL$$");
        builder.put("javax.swing.JPanel", "sk.web.swing.JPanel$$BCEL$$");
        builder.put("javax.swing.JScrollPane", "sk.web.swing.JScrollPane$$BCEL$$");
        builder.put("javax.swing.JScrollBar", "sk.web.swing.JScrollBar$$BCEL$$");
        builder.put("javax.swing.JViewPort", "sk.web.swing.JViewPort$$BCEL$$");
        builder.put("javax.swing.JOptionPane", "sk.web.swing.JOptionPane$$BCEL$$");
        builder.put("javax.swing.JFrame", "sk.viktor.ignored.containers.WebJFrame");
        builder.put("javax.swing.JDialog", "sk.viktor.ignored.containers.WebJDialog");
        builder.put("javax.swing.JWindow", "sk.viktor.ignored.containers.WebJWindow");
        
        classReplacementMapping = builder.build();

    }

    public SwingClassloader() {
        super(new String[]{"java.", "javax.", "sun.", "org.xml.sax"});
    }

    @Override
    protected JavaClass modifyClass(JavaClass clazz) {

        //do not modify classes placed in directory that contains "ignored"
        if(isInPackage(clazz.getPackageName(),new String[]{"sk.viktor.ignored"})){
            return clazz;
        }
        
        Package s = super.getPackage(clazz.getPackageName());
        if (s == null) {
            definePackage(clazz.getPackageName(), null, null, null, null, null, null, null);
        }

        if (!classReplacementMapping.values().contains(clazz.getClassName())) {
            ClassGen cg = new ClassGen(clazz);
            ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
            InstructionFactory f = new InstructionFactory(cg);

            //if this class is subclass of javax.swing.JComponent extend it with paint method which will call PaintManager
            if (isSubClassOfJComponent(clazz)) {
                //PAINT METHOD
                //modify existing paint method
                if (getPaintMethod(clazz) != null) {
                    Method m = getPaintMethod(clazz);
                    MethodGen mg = new MethodGen(m, clazz.getClassName(), cp);
                    InstructionList il = mg.getInstructionList();
                    //add to begin
                    il.insert(new ALOAD(1));
                    il.append(new ALOAD(0));
                    il.insert(f.createInvoke(PaintManager.class.getCanonicalName(), "beforePaintInterceptor", new ObjectType("java.awt.Graphics"), new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                    il.insert(new ASTORE(1));
                    //add to end 
                    il.append(new ALOAD(1));
                    il.append(new ALOAD(0));
                    il.append(f.createInvoke(PaintManager.class.getCanonicalName(), "afterPaintInterceptor", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                    il.dispose();
                } else {
                    //add paint method which call super.paint and than PaintManager
                    InstructionList il = new InstructionList();
                    MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                            Type.VOID, // return type
                            new Type[] { new ObjectType("java.awt.Graphics") }, // arg types
                            createArgNames(1), "paint", clazz.getClassName(), // method, class
                            il, cp);
                    il.append(new ALOAD(1));
                    il.append(new ALOAD(0));
                    il.append(f.createInvoke(PaintManager.class.getCanonicalName(), "beforePaintInterceptor", new ObjectType("java.awt.Graphics"), new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                    il.append(new ASTORE(1));
                    il.append(new ALOAD(0));
                    il.append(new ALOAD(1));
                    il.append(f.createInvoke(cg.getSuperclassName(), "paint", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics") }, Constants.INVOKESPECIAL));
                    il.append(new ALOAD(1));
                    il.append(new ALOAD(0));
                    il.append(f.createInvoke(PaintManager.class.getCanonicalName(), "afterPaintInterceptor", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                    il.append(InstructionConstants.RETURN);
                    mg.setMaxStack();
                    cg.addMethod(mg.getMethod());
                    il.dispose();
                }

            }

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
                                    InstructionHandle handle = findInstructionHandle(il, i);
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
            //dump
            //                try {
            //                    cg.getJavaClass().dump(cg.getClassName() + ".class");
            //                } catch (IOException ex) {
            //                    ex.printStackTrace();
            //                }
            //end dump
            return cg.getJavaClass();
        } else {
            return clazz;
        }
    }

    private InstructionHandle findInstructionHandle(InstructionList il, Instruction i) {
        for (InstructionHandle ih = il.getStart(); ih != null; ih = ih.getNext()) {
            if (ih.getInstruction().equals(i)) {
                return ih;
            }
        }
        return null;
    }

    @Override
    protected JavaClass createClass(String className) {
        try {
            //find super class
            String superClassName = classReplacementMapping.inverse().get(className);
            SyntheticRepository repository = SyntheticRepository.getInstance();
            JavaClass superClass = repository.loadClass(superClassName);

            //create subclass if superclass has paint method
            ClassGen cg = new ClassGen(className, superClassName, "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);
            ConstantPoolGen cp = cg.getConstantPool();
            InstructionFactory factory = new InstructionFactory(cg);

            //generate constructors
            for (Method constructor : getAllConstructors(superClass)) {
                InstructionList il = new InstructionList();
                MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                        Type.VOID, // return type
                        constructor.getArgumentTypes(), // arg types
                        createArgNames(constructor.getArgumentTypes().length), "<init>", className, // method, class
                        il, cp);
                for (int i = 0; i <= constructor.getArgumentTypes().length; i++) {
                    il.append(new ALOAD(i));
                }
                il.append(factory.createInvoke(superClassName, "<init>", Type.VOID, constructor.getArgumentTypes(), Constants.INVOKESPECIAL));
                il.append(InstructionConstants.RETURN);
                mg.setMaxStack();
                cg.addMethod(mg.getMethod());
                il.dispose();
            }

            //add modified paint method
            InstructionList il = new InstructionList();
            MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                    Type.VOID, // return type
                    new Type[] { new ObjectType("java.awt.Graphics") }, // arg types
                    createArgNames(1), "paint", className, // method, class
                    il, cp);
            il.append(new ALOAD(1));
            il.append(new ALOAD(0));
            il.append(factory.createInvoke(PaintManager.class.getCanonicalName(), "beforePaintInterceptor", new ObjectType("java.awt.Graphics"), new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
            il.append(new ASTORE(1));
            il.append(new ALOAD(0));
            il.append(new ALOAD(1));
            il.append(factory.createInvoke(superClassName, "paint", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics") }, Constants.INVOKESPECIAL));
            il.append(new ALOAD(1));
            il.append(new ALOAD(0));
            il.append(factory.createInvoke(PaintManager.class.getCanonicalName(), "afterPaintInterceptor", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
            il.append(InstructionConstants.RETURN);
            mg.setMaxStack();
            cg.addMethod(mg.getMethod());
            il.dispose();

            //            try {
            //                cg.getJavaClass().dump(className + ".class");
            //            } catch (java.io.IOException e) {
            //                System.err.println(e);
            //            }
            return cg.getJavaClass();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Method> getAllConstructors(JavaClass clazz) {
        List<Method> result = new ArrayList<Method>();
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("<init>")) {
                result.add(m);
            }
        }
        return result;
    }

    private Method getPaintMethod(JavaClass clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("paint") && m.isPublic() && m.getArgumentTypes().length == 1 && m.getArgumentTypes()[0].getSignature().equals("Ljava/awt/Graphics;")) {
                return m;
            }
        }
        return null;
    }

    private boolean isSubClassOfJComponent(JavaClass clazz) {
        try {
            for (JavaClass c : clazz.getSuperClasses()) {
                if (c.getClassName().equals("javax.swing.JComponent")) {
                    return true;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isInPackage(String packageInspected, String[] packagePrefixed){
        for(String prefix:packagePrefixed){
            if(packageInspected!=null && packageInspected.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }
    
    
    private static String[] createArgNames(int number) {
        String[] result = new String[number];
        for (int i = 0; i < number; i++) {
            result[i] = "arg" + i;
        }
        return result;
    }
}
