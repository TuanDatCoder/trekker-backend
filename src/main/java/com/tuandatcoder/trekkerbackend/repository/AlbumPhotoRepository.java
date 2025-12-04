package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, Long> {

    @Query("SELECT ap FROM AlbumPhoto ap WHERE ap.album.id = :albumId AND ap.deletedAt IS NULL ORDER BY ap.orderIndex ASC, ap.addedAt ASC")
    List<AlbumPhoto> findByAlbumIdActive(@Param("albumId") Long albumId);

    @Query("SELECT ap FROM AlbumPhoto ap WHERE ap.album.id = :albumId AND ap.photo.id = :photoId AND ap.deletedAt IS NULL")
    Optional<AlbumPhoto> findByAlbumIdAndPhotoId(@Param("albumId") Long albumId, @Param("photoId") Long photoId);

    @Query("SELECT ap FROM AlbumPhoto ap WHERE ap.id = :id AND ap.deletedAt IS NULL")
    Optional<AlbumPhoto> findActiveById(@Param("id") Long id);
}