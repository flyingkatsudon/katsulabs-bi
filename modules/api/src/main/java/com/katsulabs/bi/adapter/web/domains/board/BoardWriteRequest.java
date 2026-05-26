package com.katsulabs.bi.adapter.web.domains.board;

public record BoardWriteRequest(String name, Long categoryId, String layoutJson, Boolean publishedToViewers) {
}
