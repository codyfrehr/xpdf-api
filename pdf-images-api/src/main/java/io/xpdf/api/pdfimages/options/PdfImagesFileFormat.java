/*
 * PdfImages API - An API for accessing a native pdfimages library (https://xpdf.io)
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
