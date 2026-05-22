package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardWidgetMapper {

    List<BoardWidgetRow> findByBoardId(@Param("boardId") long boardId);

    void deleteByBoardId(@Param("boardId") long boardId);

    void insert(BoardWidgetRow row);
}
