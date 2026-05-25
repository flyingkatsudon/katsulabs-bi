package com.katsulabs.insightboard.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.insightboard.domain.board.BoardDetail;
import com.katsulabs.insightboard.domain.board.BoardRepository;
import com.katsulabs.insightboard.domain.board.BoardSummary;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardRow;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardMapper boardMapper;

    public BoardRepositoryImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<BoardSummary> findAllSummaries() {
        return boardMapper.findAllSummaries().stream().map(BoardRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<BoardDetail> findById(long boardId) {
        BoardRow row = boardMapper.findById(boardId);
        return row == null ? Optional.empty() : Optional.of(toDetail(row));
    }

    private static BoardSummary toSummary(BoardRow row) {
        return new BoardSummary(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryId(),
                row.getCategoryName(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static BoardDetail toDetail(BoardRow row) {
        return new BoardDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryId(),
                row.getLayout(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    @Override
    public boolean existsByName(String userId, String name, Long excludeId) {
        return boardMapper.countByName(userId, name, excludeId) > 0;
    }

    @Override
    public long insert(String userId, String name, Long categoryId, String layoutJson) {
        BoardRow row = new BoardRow();
        row.setUserId(userId);
        row.setName(name);
        row.setCategoryId(categoryId);
        row.setLayout(layoutJson);
        boardMapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(long id, String name, Long categoryId, String layoutJson) {
        BoardRow row = new BoardRow();
        row.setId(id);
        row.setName(name);
        row.setCategoryId(categoryId);
        row.setLayout(layoutJson);
        boardMapper.update(row);
    }

    @Override
    public void delete(long id) {
        boardMapper.delete(id);
    }
}
