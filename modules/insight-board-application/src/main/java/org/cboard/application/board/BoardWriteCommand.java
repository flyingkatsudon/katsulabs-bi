package org.cboard.application.board;

public record BoardWriteCommand(String name, Long categoryId, String layoutJson) {
}
