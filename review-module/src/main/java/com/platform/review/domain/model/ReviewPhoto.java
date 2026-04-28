package com.platform.review.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "review_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewPhoto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String url;
    private String caption;
}
