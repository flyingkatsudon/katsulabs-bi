package org.cboard.application.widget;

public class WidgetNotFoundException extends RuntimeException {

    public WidgetNotFoundException(long id) {
        super("위젯을 찾을 수 없습니다: " + id);
    }
}
