package com.katsulabs.bi.application.domains.board;

public record BoardWriteCommand(String name, Long categoryId, String layoutJson, boolean publishedToViewers) {
}
