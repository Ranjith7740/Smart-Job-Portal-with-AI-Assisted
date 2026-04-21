package com.hiresmarter.jobportal.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

public class FileUtil {

    private static final String UPLOAD_DIR = "uploads/";

    public static boolean isValidPdf(MultipartFile file) {
        return file != null &&
                "application/pdf".equals(file.getContentType()) &&
                file.getSize() <= 5 * 1024 * 1024; // 5MB Limit
    }

    // New Method to fix the "Cannot resolve" error
    public static String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF", e);
        }
    }

    public static String saveFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Generate unique filename to prevent overwriting
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR).resolve(fileName);

            Files.write(path, file.getBytes());
            return path.toString();
        } catch (IOException e) {
            throw new RuntimeException("File storage failed", e);
        }
    }
}