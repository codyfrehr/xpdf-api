/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
 * Copyright © 2025 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.xpdf.api.pdfinfo;

import io.xpdf.api.pdfinfo.options.PdfInfoEncoding;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Represents a set of command options specific to the <em>pdfinfo</em> executable.
 *
 * <br><br> Not all <em>pdfinfo</em> options have been implemented by this class.
 * See {@link #nativeOptions} for a complete list of command options and instructions on how to include them in your request.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfInfoOptions.builder()
 *      .encoding(PdfInfoEncoding.UTF_8)
 *      .datesUndecoded(true)
 *      .ownerPassword("Secret123")
 *      .nativeOptions(Map.ofEntries(Map.entry("-cfg", "~/config/xpdfrc")))
 *      .build();
 * </pre></blockquote>
 *
 * @implNote See <em>pdfinfo</em> documentation for detailed information about each of the options.
 * You can find source documentation alongside the executable file in the package resources.
 * @since 1.1.0
 */
@Builder
@Getter
@ToString
public class PdfInfoOptions {

    /**
     * Page number of PDF file to begin info extraction from.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -f <number>}
     * </pre></blockquote>
     *
     * @implNote Must be positive.
     * If unassigned, <em>pdfinfo</em> starts from first page.
     * @since 1.1.0
     */
    private final Integer pageStart;

    /**
     * Page number of PDF file to end info extraction on.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -l <number>}
     * </pre></blockquote>
     *
     * @implNote Must be positive and greater than or equal to {@link #pageStart}.
     * If unassigned, <em>pdfinfo</em> stops on last page.
     * @since 1.1.0
     */
    private final Integer pageStop;

    /**
     * Encoding of text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -enc <string>}
     * </pre></blockquote>
     *
     * @implNote If unassigned, <em>pdfinfo</em> defaults to {@link PdfInfoEncoding#LATIN_1}.
     * @since 1.1.0
     */
    private final PdfInfoEncoding encoding;

    /**
     * Flag to print bounding boxes in text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -box}
     * </pre></blockquote>
     *
     * @implNote If unassigned, <em>pdfinfo</em> does not print bounding box info.
     * @since 1.1.0
     */
    private final Boolean boundingBoxesIncluded;

    /**
     * Flag to print document-level XML metadata in text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -meta}
     * </pre></blockquote>
     *
     * @implNote If unassigned, <em>pdfinfo</em> does not print metadata.
     * @since 1.1.0
     */
    private final Boolean metadataIncluded;

    /**
     * Flag to print undecoded date strings in text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -rawdates}
     * </pre></blockquote>
     *
     * @implNote If unassigned, <em>pdfinfo</em> decodes date strings.
     * @since 1.1.0
     */
    private final Boolean datesUndecoded;

    /**
     * Owner password for encrypted PDF file.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -opw <string>}
     * </pre></blockquote>
     *
     * @since 1.1.0
     */
    private final String ownerPassword;

    /**
     * User password for encrypted PDF file.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -upw <string>}
     * </pre></blockquote>
     *
     * @since 1.1.0
     */
    private final String userPassword;

    /**
     * Options to pass directly into the shell command invoking the executable.
     *
     * <br><br> Useful for including <em>pdfinfo</em> options which have not been implemented by this class.
     * Each entry in the map should represent a part of the shell command, where the key is the option and the value is the parameter associated with that option.
     * If the option has no associated parameter, then just map it to a null or empty string.
     *
     * <br><br> No validation is performed against this input.
     * The options you provide here will be passed directly into the shell command, as is.
     * Be aware that you can inadvertently duplicate an option if you both (a) manually include it here, and (b) assign a value to this class's implementation of that option.
     *
     * <br><br> Example usage:
     * <blockquote><pre>
     *  PdfInfoOptions.builder()
     *      .nativeOptions(Map.ofEntries(
     *          Map.entry("-meta", null),
     *          Map.entry("-enc", "UTF-8"),
     *          Map.entry("-opw", "Secret123")))
     *      .build();
     * </pre></blockquote>
     *
     * Native options:
     * <blockquote><pre>
     *  {@literal -f <int>               : first page to convert}
     *  {@literal -l <int>               : last page to convert}
     *  {@literal -box                   : print the page bounding boxes}
     *  {@literal -meta                  : print the document metadata (XML)}
     *  {@literal -rawdates              : print the undecoded date strings directly from the PDF file}
     *  {@literal -enc <string>          : output text encoding name}
     *  {@literal -opw <string>          : owner password (for encrypted files)}
     *  {@literal -upw <string>          : user password (for encrypted files)}
     *  {@literal -cfg <string>          : configuration file to use in place of .xpdfrc}
     *  {@literal -v                     : print copyright and version info}
     *  {@literal -h                     : print usage information}
     *  {@literal -help                  : print usage information}
     *  {@literal --help                 : print usage information}
     *  {@literal -?                     : print usage information}
     * </pre></blockquote>
     *
     * @implNote No validation is performed against this input.
     * See <em>pdfinfo</em> documentation for detailed information about each of the options.
     * You can find source documentation alongside the executable file in the package resources.
     * @since 1.1.0
     */
    private final Map<String, String> nativeOptions;

}
