package com.musicbot.musicbot.repository;

import com.musicbot.musicbot.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    @Query(nativeQuery = true,
            value = "select * from music m where m.file_name like %:text%")
    Page<Music> searchMusic(Pageable pageable, @Param("text") String text);

}
