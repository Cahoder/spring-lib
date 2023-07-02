package org.spring.lib.ibatis.service.impl;

import org.spring.lib.ibatis.dao.base.BaseDao;
import org.spring.lib.ibatis.service.BaseService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
public abstract class BaseServiceImpl<PO, ID extends Serializable> implements BaseService<PO, ID> {

    @Resource
    private BaseDao<PO, ID> baseDao;

    @Override
    public PO getById(ID id) {
        return baseDao.getById(id);
    }

    @Override
    public int insert(PO po) {
        return baseDao.insert(po);
    }

    @Override
    public int insertForKey(PO po) {
        return baseDao.insertForKey(po);
    }

    @Override
    public int updateByIdSelective(PO po) {
        return baseDao.updateByIdSelective(po);
    }

    @Override
    public int deleteById(ID id) {
        return baseDao.deleteById(id);
    }

    @Override
    public List<PO> listByCondition(PO po) {
        return baseDao.listByCondition(po);
    }

    @Override
    public PO getByCondition(PO po) {
        return baseDao.getByCondition(po);
    }

    @Override
    public int batchInsert(List<PO> poList) {
        return baseDao.batchInsert(poList);
    }

    @Override
    public int batchInsertForKey(List<PO> poList) {
        return baseDao.batchInsertForKey(poList);
    }

    @Override
    public int batchUpdateByIdSelective(List<PO> poList) {
        return baseDao.batchUpdateByIdSelective(poList);
    }

    @Override
    public int batchDeleteByIdList(List<ID> idList) {
        return baseDao.batchDeleteByIdList(idList);
    }

}
