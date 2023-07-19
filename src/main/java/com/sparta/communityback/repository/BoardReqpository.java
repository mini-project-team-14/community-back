package com.sparta.communityback.repository;

import com.sparta.communityback.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReqpository extends JpaRepository<Board, Long> {
}
