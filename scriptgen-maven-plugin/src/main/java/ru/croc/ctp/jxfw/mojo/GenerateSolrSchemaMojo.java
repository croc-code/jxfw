package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.data.solr.core.mapping.SolrDocument;
import ru.croc.ctp.jxfw.solr.SchemaMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Set;

/**
 * @author SMufazzalov
 * @since 1.4
 */
@Mojo(
        name = "generateSolrSchema",
        defaultPhase = LifecyclePhase.PACKAGE
)
public class GenerateSolrSchemaMojo extends AbstractScriptMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            //получить доменные объекты Solr
            Set<Class<?>> solrDomainObjects = getSolrDomainObjects();

            if (solrDomainObjects.size() < 1) {
                getLog().info("0 SolrDomainObjects found, nothing to generate, exiting");
                return;
            }

            for (Class<?> solrDomain : solrDomainObjects) {

                try {
                    getLog().info("start solr-schema script creation for " + solrDomain.getCanonicalName());

                    //объект с иформацией о таблице для шаблона
                    SchemaMetadata meta = new SchemaMetadata(solrDomain);
                    VelocityContext context = new VelocityContext();
                    context.put("meta", meta);

                    //умеет барть шаблоны запакованные в jar
                    VelocityEngine ve = getVelocityEngine();

                    //выложить скрипт файликом
                    createSolrSchemaFile(solrDomain, context, ve);

                } catch (Exception e) {
                    getLog().error(e);
                }

            }

        } catch (MalformedURLException e) {
            getLog().error(e);
        }
    }

    private void createSolrSchemaFile(
            Class<?> solrDomain,
            VelocityContext context,
            VelocityEngine ve
    ) throws IOException {
        Path path = Paths.get(outputDirectory.toURI());
        Path solr = path.resolve("solr");
        Files.createDirectories(solr);
        File script = new File(solr.toFile(), solrDomain.getSimpleName() + ".xml");

        //autocloseable иначе не зафлюшит данные
        try (Writer w = new OutputStreamWriter(new FileOutputStream(script), StandardCharsets.UTF_8)) {
            Template vt = ve.getTemplate("solr-schema.vm");
            vt.merge(context, w);
        }

        getLog().info("Generated file: " + script.getAbsolutePath());
    }

    /**
     * Velocity template engine.
     * @return VelocityEngine
     */
    public static VelocityEngine getVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        return ve;
    }

    private Set<Class<?>> getSolrDomainObjects() throws MalformedURLException, MojoExecutionException {
        //директория с классами проекта
        URL url = new File(project.getBuild().getDirectory()).toURL();
        getLog().info("project.getBuild().getOutputDirectory() : " + url);

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(url)
                        .addClassLoaders(getClassLoader())
        );

        Set<Class<?>> cassandraDomains = reflections.getTypesAnnotatedWith(SolrDocument.class);

        getLog().info(
                MessageFormat
                        .format("Total num of classes annotated with SolrDocument found: {0}", cassandraDomains.size())
        );
        return cassandraDomains;
    }

}
