package com.platform.core.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String key;
    private String operation;
    private Object value;
}