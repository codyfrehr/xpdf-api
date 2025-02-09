package io.xpdf.api.pdfimages.options;

/**
 * Defines the preferred file format to save extracted images in.
 *
 * @since 1.2.0
 */
public enum PdfImagesFileFormat {

    /**
     * From <em>pdfimages</em> documentation:
     * <blockquote><pre>
     *  Normally, all images are written as PBM (for monochrome images),
     *  PGM (for grayscale images), or PPM  (for  color  images)  files.
     *  With  this option, images in DCT format are saved as JPEG files.
     *  All non-DCT images are saved in  PBM/PGM/PPM  format  as  usual.
     *  (Inline images are always saved in PBM/PGM/PPM format.)
     * </pre></blockquote>
     *
     * @since 1.2.0
     */
    JPEG,

    /**
     * From <em>pdfimages</em> documentation:
     * <blockquote><pre>
     *  Write all images in PDF-native formats.  Most of the formats are
     *  not standard image formats, so this option is  primarily  useful
     *  as input to a tool that generates PDF files.  (Inline images are
     *  always saved in PBM/PGM/PPM format.)
     * </pre></blockquote>
     *
     * @since 1.2.0
     */
    RAW,

}
