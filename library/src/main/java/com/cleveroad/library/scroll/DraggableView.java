package com.cleveroad.library.scroll;

public interface DraggableView extends ScrollableView {

    void onDragAndDropStart(int x, int y);

    void onDragAndDropScroll(int x, int y);

    void onDragAndDropEnd(int x, int y);

//    int getLayoutWidth();
//
//    int getLayoutHeight();

}
