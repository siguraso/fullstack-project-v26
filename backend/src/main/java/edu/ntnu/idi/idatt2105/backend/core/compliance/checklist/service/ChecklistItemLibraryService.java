package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistItemLibraryMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemLibraryRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChecklistItemLibraryService {

    private final ChecklistItemLibraryRepository repo;
    private final TenantRepository tenantRepo;
    private final ChecklistItemLibraryMapper mapper;

    public ChecklistItemLibraryDTO create(Long tenantId, ChecklistItemLibraryDTO request) {
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        ChecklistItemLibrary item = new ChecklistItemLibrary();
        item.setTenant(tenant);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setPriority(request.getPriority());

        return mapper.toDto(repo.save(item));
    }

    public List<ChecklistItemLibraryDTO> getAll(Long tenantId) {
        return repo.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}