package com.zb.daily.UI.helper;


public interface ItemTouchHelperAdapterCallback {

    /**
     * 拖拽滑动，交换item
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * 拖拽滑动，删除item
     *
     * @param position
     * @return
     */
    void onItemDelete(int position);
}