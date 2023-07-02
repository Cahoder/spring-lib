package org.spring.lib.ibatis.dao.base.curd;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.SqlCommandType;
import org.spring.lib.ibatis.dao.AbstractSqlProvider;
import org.spring.lib.ibatis.dao.ProvideSql;
import org.spring.lib.ibatis.entity.Column;

import java.io.Serializable;

import static org.spring.lib.ibatis.utils.SqlUtil.*;

/**
 * 通用CURD的SQL提供器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public class CurdSqlProvider extends AbstractSqlProvider {

    /**
     * @see CurdMapper#getById(Serializable)
     */
    public ProvideSql getById() {
        String sql = sqlEntity.getBaseSelectSql() + " where " + eqStr(sqlEntity.getIdColumn());
        return new ProvideSql(sql, SqlCommandType.SELECT);
    }

    /**
     * @see CurdMapper#insert(Object)
     */
    public ProvideSql insert() {
        StringBuilder sb = new StringBuilder("insert into ").append(sqlEntity.getTableName());

        sb.append(trimStart("(", ")", ","));
        Column idColumn = sqlEntity.getIdColumn();
        if (idColumn.needInsert()) {
            sb.append(ifNotNullWithComma(idColumn.getFieldName(), idColumn.getColumnName()));
        }
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needInsert()) {
                sb.append(ifNotNullWithComma(sqlColumn.getFieldName(), sqlColumn.getColumnName()));
            }
        }
        sb.append(trimEnd());
        sb.append(" values ");

        sb.append(trimStart("(", ")", ","));
        if (idColumn.needInsert()) {
            sb.append(ifNotNullWithComma(idColumn.getFieldName(), wrapProperty(idColumn)));
        }
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needInsert()) {
                sb.append(ifNotNullWithComma(sqlColumn.getFieldName(), wrapProperty(sqlColumn)));
            }
        }
        sb.append(trimEnd());
        return new ProvideSql(sb.toString(), SqlCommandType.INSERT);
    }

    /**
     * @see CurdMapper#insertForKey(Object)
     */
    public ProvideSql insertForKey() {
        ProvideSql provideSql = this.insert();
        provideSql.setUseGeneratedKeys(true);
        provideSql.setKeyFieldName(sqlEntity.getIdColumn().getFieldName());
        return provideSql;
    }

    /**
     * @see CurdMapper#updateByIdSelective(Object)
     */
    public ProvideSql updateByIdSelective() {
        StringBuilder sb = new StringBuilder("update ").append(sqlEntity.getTableName());
        sb.append(setStart());
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needUpdate()) {
                sb.append(ifNotNullWithComma(sqlColumn.getFieldName(), eqStr(sqlColumn)));
            }
        }
        sb.append(setEnd());
        sb.append(" where ").append(eqStr(sqlEntity.getIdColumn()));
        return new ProvideSql(sb.toString(), SqlCommandType.UPDATE);
    }

    /**
     * @see CurdMapper#deleteById(Serializable)
     */
    public ProvideSql deleteById() {
        String sql = new SQL().DELETE_FROM(sqlEntity.getTableName()).WHERE(eqStr(sqlEntity.getIdColumn())).toString();
        return new ProvideSql(sql, SqlCommandType.DELETE);
    }

    /**
     * @see CurdMapper#listByCondition(Object)
     */
    public ProvideSql listByCondition() {
        StringBuilder sb = new StringBuilder(sqlEntity.getBaseSelectSql());
        sb.append(whereStart());
        Column idColumn = sqlEntity.getIdColumn();
        if (idColumn.needSelect()) {
            sb.append(ifNotNull(idColumn.getFieldName(), " and " + eqStr(idColumn)));
        }
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needSelect()) {
                sb.append(ifNotNull(sqlColumn.getFieldName(), " and " + eqStr(sqlColumn)));
            }
        }
        sb.append(whereEnd());
        return new ProvideSql(sb.toString(), SqlCommandType.SELECT);
    }

    /**
     * @see CurdMapper#getByCondition(Object)
     */
    public ProvideSql getByCondition() {
        return this.listByCondition();
    }

}
