package org.spring.lib.ibatis.dao.base.batch;

import org.apache.ibatis.mapping.SqlCommandType;
import org.spring.lib.ibatis.dao.AbstractSqlProvider;
import org.spring.lib.ibatis.dao.ProvideSql;
import org.spring.lib.ibatis.entity.Column;

import java.util.List;

import static org.spring.lib.ibatis.utils.SqlUtil.*;

/**
 * 通用批量操作的SQL提供器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public class BatchSqlProvider extends AbstractSqlProvider {

    /**
     * @see BatchMapper#batchInsert(List)
     */
    public ProvideSql batchInsert() {
        StringBuilder sb = new StringBuilder("insert into ")
                .append(sqlEntity.getTableName())
                .append(" (");

        Column idColumn = sqlEntity.getIdColumn();
        if (idColumn.needInsert()) {
            sb.append(addComma(idColumn.getColumnName()));
        }
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needInsert()) {
                sb.append(addComma(sqlColumn.getColumnName()));
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values");

        sb.append(foreachListItemStart("", ",", ""));
        sb.append(" ( ");
        if (idColumn.needInsert()) {
            sb.append(addComma(itemWrapProperty(idColumn)));
        }
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needInsert()) {
                sb.append(addComma(itemWrapProperty(sqlColumn)));
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ) ");
        sb.append(forEachEnd());

        return new ProvideSql(sb.toString(), SqlCommandType.INSERT);
    }

    /**
     * @see BatchMapper#batchInsertForKey(List)
     */
    public ProvideSql batchInsertForKey() {
        ProvideSql provideSql = this.batchInsert();
        provideSql.setUseGeneratedKeys(true);
        provideSql.setKeyFieldName(sqlEntity.getIdColumn().getFieldName());
        return provideSql;
    }

    /**
     * @see BatchMapper#batchUpdateByIdSelective(List)
     */
    public ProvideSql batchUpdateByIdSelective() {
        StringBuilder sb = new StringBuilder();
        sb.append(foreachListItemStart("", ";", ""));

        sb.append("update ").append(sqlEntity.getTableName());

        sb.append(setStart());
        for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
            if (sqlColumn.needUpdate()) {
                sb.append(ifNotNullWithComma(ITEM_POINT + sqlColumn.getFieldName(), itemEqStr(sqlColumn)));
            }
        }
        sb.append(setEnd());

        sb.append(" where ").append(itemEqStr(sqlEntity.getIdColumn()));

        sb.append(forEachEnd());
        return new ProvideSql(sb.toString(), SqlCommandType.UPDATE);
    }

    /**
     * @see BatchMapper#batchDeleteByIdList(List)
     */
    public ProvideSql batchDeleteByIdList() {
        String sql = "delete from " +
                sqlEntity.getTableName() +
                " where " +
                sqlEntity.getIdColumn().getColumnName() +
                " in " +
                foreachListItemStart("(", ",", ")") +
                "#{" + ITEM + "}" +
                forEachEnd();
        return new ProvideSql(sql, SqlCommandType.DELETE);
    }

}