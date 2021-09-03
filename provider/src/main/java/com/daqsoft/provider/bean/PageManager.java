package com.daqsoft.provider.bean;

/**
 * @author ly
 * @version V1.0
 * @Description: 分业管理器实体
 * @date 2017/4/26 10:25
 */

public class PageManager {
    private int pageSize;
    private int pageIndex;

    public PageManager(int pageSize) {
        initPageIndex();
        this.pageSize = pageSize;
    }

    /**
     * 初始化页码
     */
    public void  initPageIndex(){
        setPageIndex(1);
    }

    /**
     *获取下页的页码
     */
    public int  getNexPageIndex(){
        pageIndex = pageIndex+1;
        return pageIndex;
    }

    /**
     * 当加载更多报错的时候，恢复页码调用
     */
    public void recoveryPageIndex(){
        pageIndex = pageIndex-1;
    }

    /**
     * 是否第一页
     * @return ture firstIndex
     */
    public boolean isFirstIndex(){
        return pageIndex==1?true:false;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
