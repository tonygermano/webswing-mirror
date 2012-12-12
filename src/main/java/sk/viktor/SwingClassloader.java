package sk.viktor;

import java.util.HashMap;
import java.util.Map;

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
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.SyntheticRepository;

import sk.viktor.ignored.common.PaintManager;
import sk.viktor.util.CLUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

public class SwingClassloader extends org.apache.bcel.util.ClassLoader {

    private static BiMap<String, String> classReplacementMapping;
    private static BiMap<String, String> methodReplacementMapping;

    static {
        Builder<String, String> classBuilder = new ImmutableBiMap.Builder<String, String>();
        classBuilder.put("javax.swing.JComponent", "sk.web.swing.JComponent$$BCEL$$");
        classBuilder.put("javax.swing.JButton", "sk.web.swing.JButton$$BCEL$$");
        classBuilder.put("javax.swing.JCheckBoxMenuItem", "sk.web.swing.JCheckBoxMenuItem$$BCEL$$");
        classBuilder.put("javax.swing.JMenu", "sk.web.swing.JMenu$$BCEL$$");
        classBuilder.put("javax.swing.JMenuBar", "sk.web.swing.JMenuBar$$BCEL$$");
        classBuilder.put("javax.swing.JMenuItem", "sk.web.swing.JMenuItem$$BCEL$$");
        classBuilder.put("javax.swing.JToolBar", "sk.web.swing.JToolBar$$BCEL$$");
        classBuilder.put("javax.swing.JPopupMenu", "sk.web.swing.JPopupMenu$$BCEL$$");
        classBuilder.put("javax.swing.JRadioButtonMenuItem", "sk.web.swing.JRadioButtonMenuItem$$BCEL$$");
        classBuilder.put("javax.swing.JToggleButton", "sk.web.swing.JToggleButton$$BCEL$$");
        classBuilder.put("javax.swing.JCheckBox", "sk.web.swing.JCheckBox$$BCEL$$");
        classBuilder.put("javax.swing.JRadioButton", "sk.web.swing.JRadioButton$$BCEL$$");
        classBuilder.put("javax.swing.Box", "sk.web.swing.Box$$BCEL$$");
        classBuilder.put("javax.swing.JColorChooser", "sk.web.swing.JColorChooser$$BCEL$$");
        classBuilder.put("javax.swing.JFileChooser", "sk.web.swing.JFileChooser$$BCEL$$");
        classBuilder.put("javax.swing.JComboBox", "sk.web.swing.JComboBox$$BCEL$$");
        classBuilder.put("javax.swing.JLabel", "sk.web.swing.JLabel$$BCEL$$");
        classBuilder.put("javax.swing.JTextField", "sk.web.swing.JTextField$$BCEL$$");
        classBuilder.put("javax.swing.JTextArea", "sk.web.swing.JTextArea$$BCEL$$");
        classBuilder.put("javax.swing.JTextPane", "sk.web.swing.JTextPane$$BCEL$$");
        classBuilder.put("javax.swing.DefaultListCellRenderer", "sk.web.swing.DefaultListCellRenderer$$BCEL$$");
        classBuilder.put("javax.swing.table.DefaultTableCellRenderer", "sk.web.swing.table.DefaultTableCellRenderer$$BCEL$$");
        classBuilder.put("javax.swing.tree.DefaultTreeCellRenderer", "sk.web.swing.tree.DefaultTreeCellRenderer$$BCEL$$");
        classBuilder.put("javax.swing.JList", "sk.web.swing.JList$$BCEL$$");
        classBuilder.put("javax.swing.JLayeredPane", "sk.web.swing.JLayeredPane$$BCEL$$");
        classBuilder.put("javax.swing.JDesktopPane", "sk.web.swing.JDesktopPane$$BCEL$$");
        classBuilder.put("javax.swing.JRootPane", "sk.web.swing.JRootPane$$BCEL$$");
        classBuilder.put("javax.swing.JSplitPane", "sk.web.swing.JSplitPane$$BCEL$$");
        classBuilder.put("javax.swing.JPanel", "sk.web.swing.JPanel$$BCEL$$");
        classBuilder.put("javax.swing.JScrollPane", "sk.web.swing.JScrollPane$$BCEL$$");
        classBuilder.put("javax.swing.JScrollBar", "sk.web.swing.JScrollBar$$BCEL$$");
        classBuilder.put("javax.swing.JViewPort", "sk.web.swing.JViewPort$$BCEL$$");
        classBuilder.put("javax.swing.JOptionPane", "sk.web.swing.JOptionPane$$BCEL$$");
        classBuilder.put("javax.swing.JFrame", "sk.viktor.containers.WebJFrame");
        classBuilder.put("javax.swing.JDialog", "sk.viktor.containers.WebJDialog");
        classBuilder.put("javax.swing.JWindow", "sk.viktor.containers.WebJWindow");
        classReplacementMapping = classBuilder.build();

        Builder<String, String> methodBuilder = new ImmutableBiMap.Builder<String, String>();
        methodBuilder.put("java.lang.Runtime exit (I)V", "sk.viktor.special.RedirectedMethods exit (I)V");
        methodBuilder.put("java.beans.XMLEncoder writeObject (Ljava/lang/Object;)V", "sk.viktor.special.RedirectedMethods writeObject (Ljava/lang/Object;)V");
        methodReplacementMapping = methodBuilder.build();
        
    }

    private String clientId;

    public SwingClassloader(String clientId) {
        super(new String[] { "java.","javax.", "sun.", "org.xml.sax", "sk.viktor.ignored" });
        this.clientId = clientId;
    }

    @Override
    protected JavaClass modifyClass(JavaClass clazz) {

        //do not modify classes placed in this list
        if (CLUtil.isInPackage(clazz.getPackageName(), new String[] { "sk.viktor.containers","sk.viktor.special" })) {
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

            //+++++++++ Rerouting swing component classes to generated subclasses with intercepted paint methods +++++++
            rerouteSwingClasses(clazz, cg, cp);

            //+++++++++ Intercept paint method if current class is subclass of JComponent +++++++
            interceptPaintMethod(clazz, cg, cp, f);

            //+++++++++ Reroute methods that require special handling +++++++++++++ 
            rerouteMehods(clazz, cg, cp, f);

            //dump
            //            try {
            //                if (print)
            //                    cg.getJavaClass().dump(cg.getClassName() + ".class");
            //                print = false;
            //            } catch (IOException ex) {
            //                ex.printStackTrace();
            //            }
            //end dump
            return cg.getJavaClass();
        } else {
            return clazz;
        }
    }

    private void rerouteMehods(JavaClass clazz, ClassGen cg, ConstantPoolGen cp, InstructionFactory f) {
        Map<Integer,Integer> indexReplacementMap= new HashMap<Integer, Integer>();
        //find methods to replace in current class
        for(String methodDef:methodReplacementMapping.keySet()){
            String[] md=methodDef.split(" ");
            int methodIndex = cp.lookupMethodref(md[0], md[1], md[2]);
            if(methodIndex !=-1){
                //method found in current class, create a replacement MethodRefConstant in cp
                String[] replace=methodReplacementMapping.get(methodDef).split(" ");
                int replacementIndex = cp.addMethodref(replace[0], replace[1], replace[2]);
                indexReplacementMap.put(methodIndex, replacementIndex);
            }
        }
        //replace methodRef indexes in instructions
        if(indexReplacementMap.size()>0){
            for (Method m : cg.getMethods()) {
                MethodGen mg = new MethodGen(m, clazz.getClassName(), cp);
                InstructionList il = mg.getInstructionList();
                if (il != null) {
                    boolean dirtyFlag = false;
                    for (Instruction instruction : il.getInstructions()) {
                        if (instruction instanceof CPInstruction) {
                            CPInstruction i = (CPInstruction) instruction;
                            if(indexReplacementMap.containsKey(i.getIndex())){
                               InstructionHandle handle = CLUtil.findInstructionHandle(il, i);
                               INVOKESTATIC replacedInstruction = new INVOKESTATIC(indexReplacementMap.get(i.getIndex()));
                               handle.setInstruction(replacedInstruction);
                               dirtyFlag = true;
                               System.out.println("Replacing "+mg.getName()+ " in "+clazz.getClassName());
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
                                InstructionHandle handle = CLUtil.findInstructionHandle(il, i);
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

    private void interceptPaintMethod(JavaClass clazz, ClassGen cg, ConstantPoolGen cp, InstructionFactory f) {
        //if this class is subclass of javax.swing.JComponent extend it with paint method which will call PaintManager
        if (CLUtil.isSubClassOfJComponent(clazz)) {
            //PAINT METHOD
            //modify existing paint method
            if (CLUtil.getPaintMethod(clazz) != null) {
                Method m = CLUtil.getPaintMethod(clazz);
                MethodGen mg = new MethodGen(m, clazz.getClassName(), cp);
                InstructionList il = mg.getInstructionList();
                //add to begin(in reverted order-always insetrs to first position)
                il.insert(new ASTORE(1));
                il.insert(f.createInvoke(PaintManager.class.getCanonicalName(), "beforePaintInterceptor", new ObjectType("java.awt.Graphics"), new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                il.insert(new ALOAD(0));
                il.insert(new ALOAD(1));
                //add to end 
                il.append(new ALOAD(1));
                il.append(new ALOAD(0));
                il.append(f.createInvoke(PaintManager.class.getCanonicalName(), "afterPaintInterceptor", Type.VOID, new Type[] { new ObjectType("java.awt.Graphics"), new ObjectType("javax.swing.JComponent") }, Constants.INVOKESTATIC));
                il.setPositions();
                mg.setInstructionList(il);
                mg.setMaxStack();
                mg.setMaxLocals();
                mg.removeLineNumbers();
                cg.replaceMethod(m, mg.getMethod());
                il.dispose();
            } else {
                //add paint method which call super.paint and than PaintManager
                InstructionList il = new InstructionList();
                MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                        Type.VOID, // return type
                        new Type[] { new ObjectType("java.awt.Graphics") }, // arg types
                        CLUtil.createArgNames(1), "paint", clazz.getClassName(), // method, class
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
            for (Method constructor : CLUtil.getAllConstructors(superClass)) {
                InstructionList il = new InstructionList();
                MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, // access flags
                        Type.VOID, // return type
                        constructor.getArgumentTypes(), // arg types
                        CLUtil.createArgNames(constructor.getArgumentTypes().length), "<init>", className, // method, class
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
                    CLUtil.createArgNames(1), "paint", className, // method, class
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

    public String getClientId() {
        return clientId;
    }

}
