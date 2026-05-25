package com.katsulabs.insightboard.adapter.web.board;

public record BoardWriteRequest(String name, Long categoryId, String layoutJson) {
}
