package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardFilterMapper {

    List<BoardFilterRow> findByBoardId(@Param("boardId") long boardId);

    List<String> findOptionValuesByFilterId(@Param("filterId") long filterId);

    void deleteOptionsByBoardId(@Param("boardId") long boardId);

    void deleteByBoardId(@Param("boardId") long boardId);

    void insert(BoardFilterRow row);

    void insertOption(@Param("filterId") long filterId, @Param("optionValue") String optionValue);
}
