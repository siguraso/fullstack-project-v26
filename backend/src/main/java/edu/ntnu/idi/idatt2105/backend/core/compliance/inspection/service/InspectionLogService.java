package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InspectionLogService {

    private final DeviationRepository deviationRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final AlcoholLogRepository alcoholLogRepository;

    public List<InspectionLogDTO> getInspectionLogs() {
        Long tenantId = TenantContext.getCurrentOrg();

        List<InspectionLogDTO> logs = new ArrayList<>();

        for (Deviation d : deviationRepository.findByTenantId(tenantId)) {
            InspectionLogDTO dto = new InspectionLogDTO();
            dto.setType("DEVIATION");
            dto.setReferenceId(d.getId());
            dto.setTitle(d.getTitle());
            dto.setDescription(summarizeDeviation(d));
            dto.setStatus(d.getStatus().name());
            dto.setSeverity(d.getSeverity().name());
            dto.setModule(d.getModule().name());
            dto.setTimestamp(d.getCreatedAt().toString());

            dto.setActor(resolveActor(d.getCreatedBy()));
            logs.add(dto);
        }

        for (TemperatureComplianceLog log : temperatureLogRepository.findAllByTenantId(tenantId)) {
            InspectionLogDTO dto = new InspectionLogDTO();
            dto.setType("TEMPERATURE");
            dto.setReferenceId(log.getId());
            dto.setTitle(log.getTitle());
            dto.setDescription(log.getDescription());
            dto.setStatus(log.getStatus().name());
            dto.setModule(log.getModule().name());
            dto.setTimestamp(log.getRecordedAt().toString());

            dto.setActor(resolveActor(log.getRecordedBy()));
            logs.add(dto);
        }

        for (AlcoholComplianceLog log : alcoholLogRepository.findAllByTenantId(tenantId)) {
            InspectionLogDTO dto = new InspectionLogDTO();
            dto.setType("ALCOHOL");
            dto.setReferenceId(log.getId());
            dto.setTitle(log.getTitle());
            dto.setDescription(log.getDescription());
            dto.setStatus(log.getStatus().name());
            dto.setModule(log.getModule().name());
            dto.setTimestamp(log.getRecordedAt().toString());

            dto.setActor(resolveActor(log.getRecordedBy()));
            logs.add(dto);
        }

        return logs.stream()
                .sorted(Comparator.comparing(InspectionLogDTO::getTimestamp).reversed())
                .toList();
    }

    private String resolveActor(User user) {
        if (user == null)
            return "System";
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    private String summarizeDeviation(Deviation deviation) {
        if (deviation.getIssueDescription() != null && !deviation.getIssueDescription().isBlank()) {
            return deviation.getIssueDescription();
        }

        if (deviation.getImmediateAction() != null && !deviation.getImmediateAction().isBlank()) {
            return deviation.getImmediateAction();
        }

        return "Deviation recorded";
    }
}