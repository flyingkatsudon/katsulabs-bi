package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    List<BoardRow> findAllSummaries(
            @org.apache.ibatis.annotations.Param("publishedOnly") boolean publishedOnly);

    BoardRow findById(@org.apache.ibatis.annotations.Param("boardId") long boardId);

    long countByName(@org.apache.ibatis.annotations.Param("userId") String userId,
            @org.apache.ibatis.annotations.Param("name") String name,
            @org.apache.ibatis.annotations.Param("excludeId") Long excludeId);

    void insert(BoardRow row);

    void update(BoardRow row);

    void delete(@org.apache.ibatis.annotations.Param("boardId") long boardId);
}
