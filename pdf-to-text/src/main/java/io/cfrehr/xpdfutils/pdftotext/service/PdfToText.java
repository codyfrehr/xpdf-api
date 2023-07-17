package io.cfrehr.xpdfutils.pdftotext.service;

import io.cfrehr.xpdfutils.common.XpdfException;
import io.cfrehr.xpdfutils.common.XpdfUtility;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextResponse;
import lombok.Builder;
import lombok.val;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.concurrent.Future;

@Builder
public class PdfToText implements XpdfUtility<PdfToTextRequest, PdfToTextResponse> {

    @Override
    public PdfToTextResponse process(PdfToTextRequest request) throws XpdfException {
        try {
            val archBit = System.getProperty("sun.arch.data.model"); // 32, 64, or unknown
            //todo: if unknown, log warning

            final String binDirectory;
            if (SystemUtils.IS_OS_WINDOWS) {
                binDirectory = archBit.equals("64") ? "resources/windows/64" : "resources/windows/32";
            } else {
                throw new XpdfException("Xpdf can only run against Windows, Mac, or Linux operating systems.");
            }

            // process:
            // https://stackoverflow.com/a/30926869
            // https://www.baeldung.com/run-shell-command-in-java
            val command = "%s %s".formatted("xpdfcommand.exe ", request.getCommandLineArgs());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(binDirectory));
            Process process = builder.start();

            //todo: wtf is stream gobbler lol? ...i think its a dummy for something i need to create
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = executorService.submit(streamGobbler);

            //todo: do stuff with result

            process.getErrorStream().readAllBytes();

            return PdfToTextResponse.builder().build();
        } catch (XpdfException e) {
            throw e;
        } catch (Exception e) {
            throw new XpdfException(e);
        }
    }
}
