package com.katsulabs.insightboard.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.insightboard.domain.board.BoardDetail;
import com.katsulabs.insightboard.domain.board.BoardRepository;
import com.katsulabs.insightboard.domain.board.BoardSummary;
import com.katsulabs.insightboard.infrastructure.persistence.compat.BoardFilterSync;
import com.katsulabs.insightboard.infrastructure.persistence.compat.BoardLayoutSync;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardFilterMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardRow;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.BoardWidgetMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardMapper boardMapper;
    private final BoardWidgetMapper boardWidgetMapper;
    private final BoardFilterMapper boardFilterMapper;

    public BoardRepositoryImpl(
            BoardMapper boardMapper,
            BoardWidgetMapper boardWidgetMapper,
            BoardFilterMapper boardFilterMapper) {
        this.boardMapper = boardMapper;
        this.boardWidgetMapper = boardWidgetMapper;
        this.boardFilterMapper = boardFilterMapper;
    }

    @Override
    public List<BoardSummary> findAllSummaries(boolean publishedOnly) {
        return boardMapper.findAllSummaries(publishedOnly).stream()
                .map(BoardRepositoryImpl::toSummary)
                .toList();
    }

    @Override
    public Optional<BoardDetail> findById(long boardId) {
        BoardRow row = boardMapper.findById(boardId);
        if (row == null) {
            return Optional.empty();
        }
        var placements = boardWidgetMapper.findByBoardId(boardId);
        var filters = BoardFilterSync.loadFilters(boardFilterMapper, boardId);
        String layout = BoardLayoutSync.resolveLayoutJson(row.getLayout(), placements);
        layout = BoardFilterSync.resolveLayoutJson(layout, filters);
        return Optional.of(toDetail(row, layout));
    }

    private static BoardSummary toSummary(BoardRow row) {
        return new BoardSummary(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryId(),
                row.getCategoryName(),
                Boolean.TRUE.equals(row.getPublishedToViewers()),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static BoardDetail toDetail(BoardRow row, String layoutJson) {
        return new BoardDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryId(),
                layoutJson,
                Boolean.TRUE.equals(row.getPublishedToViewers()),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    @Override
    public boolean existsByName(String userId, String name, Long excludeId) {
        return boardMapper.countByName(userId, name, excludeId) > 0;
    }

    @Override
    public long insert(
            String userId,
            String name,
            Long categoryId,
            String layoutJson,
            boolean publishedToViewers,
            String publishedByUserId) {
        BoardRow row = new BoardRow();
        row.setUserId(userId);
        row.setName(name);
        row.setCategoryId(categoryId);
        row.setLayout(layoutJson);
        applyPublish(row, publishedToViewers, publishedByUserId);
        boardMapper.insert(row);
        long boardId = row.getId();
        syncLayoutAndFilters(boardId, layoutJson);
        return boardId;
    }

    @Override
    public void update(
            long id,
            String name,
            Long categoryId,
            String layoutJson,
            boolean publishedToViewers,
            String publishedByUserId) {
        BoardRow row = new BoardRow();
        row.setId(id);
        row.setName(name);
        row.setCategoryId(categoryId);
        row.setLayout(layoutJson);
        applyPublish(row, publishedToViewers, publishedByUserId);
        boardMapper.update(row);
        syncLayoutAndFilters(id, layoutJson);
    }

    @Override
    public void delete(long id) {
        boardFilterMapper.deleteOptionsByBoardId(id);
        boardFilterMapper.deleteByBoardId(id);
        boardWidgetMapper.deleteByBoardId(id);
        boardMapper.delete(id);
    }

    private void syncLayoutAndFilters(long boardId, String layoutJson) {
        BoardLayoutSync.syncFromLayoutJson(boardWidgetMapper, boardId, layoutJson);
        BoardFilterSync.syncFromLayoutJson(boardFilterMapper, boardId, layoutJson);
    }

    private static void applyPublish(BoardRow row, boolean publishedToViewers, String publishedByUserId) {
        row.setPublishedToViewers(publishedToViewers);
        row.setPublishedBy(publishedToViewers ? publishedByUserId : null);
    }
}
