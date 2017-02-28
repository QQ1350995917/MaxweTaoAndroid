package org.maxwe.tao.android.mate;

import org.maxwe.tao.android.account.agent.AgentModel;
import org.maxwe.tao.android.account.model.TokenModel;

import java.util.LinkedList;

/**
 * Created by Pengwei Ding on 2017-01-12 16:16.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: TODO
 */
public class BranchModel extends TokenModel {
    private int total;
    private int pageIndex;
    private int pageSize;
    private LinkedList<AgentModel> agentEntities; // 相应字段

    public BranchModel() {
        super();
    }

    public BranchModel(TokenModel sessionModel, int pageIndex, int pageSize) {
        super(sessionModel.getT(),sessionModel.getId(),sessionModel.getCellphone(),sessionModel.getApt());
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public LinkedList<AgentModel> getAgentEntities() {
        return agentEntities;
    }

    public void setAgentEntities(LinkedList<AgentModel> agentEntities) {
        this.agentEntities = agentEntities;
    }

    public boolean isParamsOk() {
        if (this.getPageIndex() >= 0 && this.getPageSize() > 0) {
            return true && super.isTokenParamsOk();
        }
        return false;
    }
}
