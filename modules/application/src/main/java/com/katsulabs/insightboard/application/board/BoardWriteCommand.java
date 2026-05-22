package com.katsulabs.insightboard.application.board;

public record BoardWriteCommand(String name, Long categoryId, String layoutJson, boolean publishedToViewers) {
}
