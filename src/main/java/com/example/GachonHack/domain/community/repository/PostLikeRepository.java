package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.entity.PostLike;
import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.id = :userId")
    Set<Long> findPostIdsByUserId(@Param("userId") Long userId);
}
