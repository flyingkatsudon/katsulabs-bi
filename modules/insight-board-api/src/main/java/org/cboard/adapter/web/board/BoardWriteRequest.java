package org.cboard.adapter.web.board;

public record BoardWriteRequest(String name, Long categoryId, String layoutJson) {
}
