package ru.croc.ctp.jxfw.mojo;

import static java.nio.file.Files.newDirectoryStream;
import static org.joox.JOOX.$;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.joox.Match;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

/**
 * Модификатор шаблонов отчетов.
 * 
 * @author Nosov Alexander
 */
@Mojo(name = "birtReportModification", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
@SuppressWarnings("unused")
public class BirtReportModifier extends AbstractMojo {
    
    @Parameter(readonly = true, required = true)
    private File reportDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try (final DirectoryStream<Path> stream = newDirectoryStream(reportDirectory.toPath(), "*.{rptdesign}")) {
            for (final Path designPath : stream) {
                final Document document = $(designPath.toFile()).document();
                final Match designXml = $(document);

                designXml.find("data-sources").find("oda-data-source").children().remove();

                final Writer out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(designPath.toFile()), "UTF8"));
                designXml.write(out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
