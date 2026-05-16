package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.entity.PostLike;
import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);
}
