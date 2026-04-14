package com.platform.notification.application.service;

import com.platform.core.exception.ResourceNotFoundException;
import com.platform.notification.domain.model.NotificationTemplate;
import com.platform.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public String getSubject(String templateKey) {
        NotificationTemplate template = templateRepository.findByTemplateKey(templateKey)
                .orElseThrow(() -> new ResourceNotFoundException("Template", "key", templateKey));
        return template.getSubject();
    }

    @Transactional(readOnly = true)
    public String render(String templateKey, Map<String, Object> data) {
        NotificationTemplate template = templateRepository.findByTemplateKey(templateKey)
                .orElseThrow(() -> new ResourceNotFoundException("Template", "key", templateKey));
        String content = template.getBody();
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
            }
        }
        return content;
    }
}