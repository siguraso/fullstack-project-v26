package edu.ntnu.idi.idatt2105.backend.core.document.service;

public record DocumentDownloadResult(String filename, String mimeType, byte[] content) {
}
