package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionExportFilter;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionPdfExportService {

    private final InspectionLogService inspectionLogService;
    private final TemplateEngine templateEngine;

    public byte[] generatePdf(InspectionExportFilter filter) {
        List<InspectionLogDTO> logs = inspectionLogService.getFilteredLogs(filter);

        Context ctx = new Context();
        ctx.setVariable("logs", logs);
        ctx.setVariable("filter", filter);
        ctx.setVariable("generatedDate", LocalDate.now().toString());
        ctx.setVariable("totalCount", logs.size());

        String html = templateEngine.process("inspection-report", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html, null);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate inspection PDF", e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
