package com.musicbot.musicbot.repository;

import com.musicbot.musicbot.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    @Query(nativeQuery = true,
            value = "select * from music m where m.music_name like %:text% ")
    List<Music> searchMusic(@Param("text") String text);
}
