package org.cboard.dto;

/**
 * Created by yfyuan on 2016/12/21.
 */
public class DashboardMenu {

    private long menuId;
    private long parentId;
    private String menuName;
    private String menuCode;
    
    // custom
    private String partition;

    public DashboardMenu() {
    }
    
    public DashboardMenu(long menuId, long parentId, String menuName, String menuCode) {
        this.menuId = menuId;
        this.parentId = parentId;
        this.menuName = menuName;
        this.menuCode = menuCode;
    }

    public DashboardMenu(String partition, long menuId, long parentId, String menuName, String menuCode) {
    	this.partition = partition;
        this.menuId = menuId;
        this.parentId = parentId;
        this.menuName = menuName;
        this.menuCode = menuCode;
    }
    
    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}
}
