package com.musicbot.musicbot.entity;


import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileId;

    private String fileUniqueId;

    private String fileName;

    private Long fileSize;

    private Long uploaderId;

    private Integer duration;

    private String performer;

    private String title;

    private String mimeType;

    private Long downloadCount = 1L;

    @OrderBy
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp uploadedAt;

    public Music(String fileId, String fileUniqueId,
                 String fileName, Long fileSize,
                 Long uploaderId, Integer duration,
                 String performer, String title,
                 String mimeType) {
        this.fileId = fileId;
        this.fileUniqueId = fileUniqueId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploaderId = uploaderId;
        this.duration = duration;
        this.performer = performer;
        this.title = title;
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Music music = (Music) o;
        return id != null && Objects.equals(id, music.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
