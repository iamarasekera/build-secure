package lk.ishara.buildsecure.core;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.annotation.processing.Processor;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;
import com.sun.source.tree.BlockTree;

import java.io.IOException;
import java.util.Arrays;

import lk.ishara.buildsecure.utils.ConfigReader;
import lk.ishara.buildsecure.utils.ManifestParser;
import lk.ishara.buildsecure.utils.Util;

@SupportedAnnotationTypes("lk.ishara.buildsecure.core.BuildSecure")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BuildSecProcessor extends AbstractProcessor {
private static Util util = new Util();
private static Configuration config;
private static AndroidManifest androidManifest;
private  static List<String> usesList = null;

private static String msg;
    static {
        System.out.println("---Configuration Reader Started ----");
        ConfigReader cr = new ConfigReader();
        try {
            config = cr.getPropValues();
            System.out.println(config.toJSON());
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error in reading config file");
        }
        androidManifest = ManifestParser.parse("app/src/main/"  + Constants.USER_MANIFEST);
        System.out.println(androidManifest.toJSON());
    }

    private Trees trees;
    private static class MethodScanner extends TreePathScanner<List<MethodTree>, Trees> {
        private List<MethodTree> methodTrees = new ArrayList<>();

        public MethodTree scan(ExecutableElement methodElement, Trees trees) {
            assert methodElement.getKind() == ElementKind.METHOD;

            List<MethodTree> methodTrees = this.scan(trees.getPath(methodElement), trees);
            assert methodTrees.size() == 1;

            return methodTrees.get(0);
        }

        @Override
        public List<MethodTree> scan(TreePath treePath, Trees trees) {
            super.scan(treePath, trees);
            return this.methodTrees;
        }

        @Override
        public List<MethodTree> visitMethod(MethodTree methodTree, Trees trees) {
            this.methodTrees.add(methodTree);
            return super.visitMethod(methodTree, trees);
        }
    }
    @Override
    public void init(ProcessingEnvironment pe) {
        super.init(pe);
        this.trees = Trees.instance(pe);
    }
    @Override
    public boolean process(Set<? extends TypeElement> elemSet, RoundEnvironment roundEnv) {
        StringBuilder consoleMsg = new StringBuilder();

        System.out.println(String.format("Annotation Processor invoked at %s Build Secure", Util.getFormattedCurrentDateTime()));
        for (Element elem : roundEnv.getElementsAnnotatedWith(BuildSecure.class)) {

       // for (Element elem : roundEnv.getRootElements()) {
            if (elem.getKind() != ElementKind.METHOD) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Can be applied only to methods.");
                return true;
            } else {
                System.out.println("Scanning annotated method " + elem.getSimpleName());
                MethodScanner methodScanner = new MethodScanner();
                MethodTree methodTree = methodScanner.scan((ExecutableElement) elem, this.trees);
                BlockTree bt = methodTree.getBody();
                usesList = UsesCodeMapping.checkCode(bt);
                for (String str : usesList) {
                    System.out.println(str);
                }
                // log
                if (UsesCodeMapping.logUsed) {
                    msg = "Log(s) used in " + ElementKind.METHOD + ":" + elem.getSimpleName();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Log used in source code");
                }
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, consoleMsg.toString());
        }
        if (roundEnv.processingOver()){
            // write manifest
            if (usesList != null && !usesList.isEmpty()) {
                androidManifest.updateUsesPermission(usesList);
                System.out.println(usesList);
                ManifestParser.modifyManifest( "app/src/main/"  + Constants.USER_MANIFEST, androidManifest, config.isAllowDataBackup());
                System.out.println(androidManifest.toJSON());
            }
            util.publishReport(androidManifest, config, msg);
        }
        return false; // allow others to process this annotation type
    }
}