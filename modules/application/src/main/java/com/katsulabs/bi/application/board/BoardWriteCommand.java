package com.katsulabs.bi.application.board;

public record BoardWriteCommand(String name, Long categoryId, String layoutJson, boolean publishedToViewers) {
}
