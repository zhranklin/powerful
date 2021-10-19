package zhranklin.powerful.assist;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import zhranklin.powerful.core.PowerfulAutoConfiguration;
import zhranklin.powerful.core.invoker.DubboRemoteInvoker;
import zhranklin.powerful.dubbo.tmpl.DubboService;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 张武 at 2019/9/24
 */
public class Gen {

    public static final String GEN_PACKAGE = "zhranklin.powerful.dubbo.gen";
    public static final String JAVASSIST_PATH = "/usr/local/javalib/BOOT-INF/classes";
    public static boolean isStage0 = false;
    private static ClassPool pool = ClassPool.getDefault();
    private static Map<String, InterfaceRefField> interfaces = new HashMap<>();

    static {
        pool.appendClassPath(new LoaderClassPath(PowerfulAutoConfiguration.class.getClassLoader()));
        try {
            pool.appendClassPath(JAVASSIST_PATH);
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void gen(String appName) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(new DefaultResourceLoader(Thread.currentThread().getContextClassLoader()));
//            Resource[] resources = resolver.getResources("classpath*:" + GEN_PACKAGE.replaceAll("\\.", "/") + "/**/*.class");
            ClassPathScanningCandidateComponentProvider pvd = new ClassPathScanningCandidateComponentProvider(false);
            pvd.addIncludeFilter(new AnnotationTypeFilter(PowerfulRPCTemplate.class));
            pvd.setResourceLoader(resolver);
            Set<BeanDefinition> bd = pvd.findCandidateComponents(DubboService.class.getPackage().getName());
            for (BeanDefinition bdd : bd) {
                Class<?> clazz = Class.forName(bdd.getBeanClassName());
                PowerfulRPCTemplate rpcTemplate = clazz.getAnnotation(PowerfulRPCTemplate.class);
                if (rpcTemplate != null) {
                    rpcTemplate.subPackage();
                    String service = rpcTemplate.value();
                    genClass(clazz, appName, service);
                }
            }
            genDubboInvoker(getDependsOn());
        } catch (CannotCompileException | IOException | NotFoundException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void genDubboInvoker(Set<String> dependsOn) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        CtClass clazz = pool.get(DubboRemoteInvoker.class.getName());
        for (String dep : dependsOn) {
            for (Map.Entry<String, InterfaceRefField> entry : interfaces.entrySet()) {
                InterfaceRefField ref = entry.getValue();
                genClass(ref.clazz, dep, null);
                if (!isStage0) {
                    continue;
                }
                CtClass fieldType = pool.get(genClassName(ref.clazz, dep));
                CtField field = new CtField(fieldType, genFieldName(dep, entry.getKey()), clazz);

                ConstPool constPool = clazz.getClassFile().getConstPool();
                AnnotationsAttribute annAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation annotation = new Annotation(Reference.class.getName(), constPool);
                annotation.addMemberValue("check", new BooleanMemberValue(false, constPool));
                annotation.addMemberValue("group", new StringMemberValue(ref.group, constPool));
                annotation.addMemberValue("version", new StringMemberValue(ref.version, constPool));
                annotation.addMemberValue("retries", new IntegerMemberValue(0, constPool));
                annAttr.setAnnotation(annotation);

                field.getFieldInfo().addAttribute(annAttr);
                clazz.addField(field);
            }

        }
        writeClass(clazz);
    }

    public static void genDubboRefFields(Map<String, Class<?>> fieldToClass) throws ClassNotFoundException {
        for (String dep : getDependsOn()) {
            for (Map.Entry<String, InterfaceRefField> entry : interfaces.entrySet()) {
                fieldToClass.put(genFieldName(dep, entry.getKey()), Class.forName(genClassName(entry.getValue().clazz, dep)));
            }
        }
    }

    public static Set<String> getDependsOn() {
        String app = System.getenv("APP");
        HashSet<String> result = new HashSet<>();
        for (String d : System.getenv("DUBBO_DEPENDS_ON").split(",")) {
            if (d.isEmpty() || d.equals(app)) {
                continue;
            }
            result.add(d);
        }
        return result;
    }

    public static void genClass(Class<?> impl, String suffix, String service) throws CannotCompileException, IOException, NotFoundException, ClassNotFoundException {
        String cPkg = impl.getPackage().getName();
        CtClass ctClass = copyClassFromTmpl(impl, suffix);
        CtClass[] interfaces = ctClass.getInterfaces();
        Service sa = (Service) ctClass.getAnnotation(Service.class);
        String group = sa == null ? "" : sa.group();
        String version = sa == null ? "" : sa.version();
        int iClassCount = 0;
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> iClass = Class.forName(interfaces[i].getName());
            String iPkg = iClass.getPackage().getName();
            if (cPkg.equals(iPkg)) {
                String serviceName = service;
                if (iClassCount > 0) {
                    serviceName += iClassCount;
                }
                Gen.interfaces.put(serviceName, new InterfaceRefField(iClass, group, version));
                CtClass interfaceClass = copyClassFromTmpl(iClass, suffix);
                writeClass(interfaceClass);
                interfaces[i] = interfaceClass;
                iClassCount++;
            }
        }
        ctClass.setInterfaces(interfaces);
        writeClass(ctClass);
    }

    private static CtClass copyClassFromTmpl(Class<?> interf, String suffix) throws NotFoundException {
        String in = genClassName(interf, suffix);
        return pool.getAndRename(interf.getName(), in);
    }

    private static void writeClass(CtClass ctClass) throws CannotCompileException, IOException {
        if (isStage0) {
            ctClass.writeFile(JAVASSIST_PATH);
        }
    }

    public static String genClassName(Class<?> tmpl, String suffix) {
        return GEN_PACKAGE + "." + tmpl.getSimpleName() + "_" + suffix;
    }

    public static String genFieldName(String app, String service) {
        return String.format("gen_dubbo%s_%s", service, app);
    }

    public static void main(String[] args) {
        System.out.println();
        gen("a");
    }

    public static class InterfaceRefField {
        public final Class<?> clazz;
        public final String group;
        public final String version;

        public InterfaceRefField(Class<?> clazz, String group, String version) {
            this.clazz = clazz;
            this.group = group;
            this.version = version;
        }
    }

}
