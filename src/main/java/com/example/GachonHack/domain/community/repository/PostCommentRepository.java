package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT c FROM PostComment c JOIN FETCH c.author WHERE c.post = :post ORDER BY c.createdAt ASC")
    List<PostComment> findByPostWithAuthor(@Param("post") Post post);
}
