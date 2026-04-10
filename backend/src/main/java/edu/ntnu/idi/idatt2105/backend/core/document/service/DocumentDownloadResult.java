package edu.ntnu.idi.idatt2105.backend.core.document.service;

/**
 * Immutable value object carrying the result of a document download operation.
 *
 * @param filename the original filename of the document
 * @param mimeType the MIME type of the document content
 * @param content  the raw file bytes
 */
public record DocumentDownloadResult(String filename, String mimeType, byte[] content) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentDownloadResult(var otherFilename, var otherMimeType, var otherContent))) return false;
        return java.util.Objects.equals(filename, otherFilename)
                && java.util.Objects.equals(mimeType, otherMimeType)
                && java.util.Arrays.equals(content, otherContent);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(filename, mimeType, java.util.Arrays.hashCode(content));
    }

    @Override
    public String toString() {
        return "DocumentDownloadResult[filename=" + filename
                + ", mimeType=" + mimeType
                + ", content=" + java.util.Arrays.toString(content) + "]";
    }
}
