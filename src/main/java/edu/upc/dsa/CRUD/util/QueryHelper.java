package edu.upc.dsa.CRUD.util;

import java.util.HashMap;
import java.util.Objects;

public class QueryHelper {

    public static String createQueryINSERT(Object entity) {

        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(entity.getClass().getSimpleName()).append(" (");

        String [] fields = ObjectHelper.getFields(entity);
        int var = fields.length;
        int var1;
        String [] var2 = fields;

        for (var1 = 0; var1 < var; var1++) {
            String field = var2[var1];
            sb.append(field).append(", ");
        }
        sb.delete(sb.length()-2,sb.length());
        sb.append(") VALUES (");

        for(var1 = 0; var1 < var; var1++) {
            String var25 = var2[var1];
            sb.append("?, ");
        }
        sb.delete(sb.length()-2,sb.length());
        sb.append(")");
        return sb.toString();
    }

    public static String createQuerySELECT(Class theClass, String pk) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName().toLowerCase());
        sb.append(" WHERE "+pk+"= ?");

        return sb.toString();
    }

    public static String createQuerySELECTAll(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName().toLowerCase());
        return sb.toString();
    }

    public static String createQuerySELECTByName(Class theClass, String username){
        StringBuffer sb = new StringBuffer("");
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE username = '").append(username).append("'");
        return sb.toString();
    }

    public static String createQuerySelectWithP(Class theClass, HashMap params) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName().toLowerCase());
        sb.append(" WHERE (");

        params.forEach((k,v) ->{
            //k = k.substring(0, 1).toUpperCase() + k.substring(1);
            if(k.equals("password")){
                sb.append(k).append(" = MD5(").append("?").append(") AND ");
            }else {
                sb.append(k).append(" = ").append("?").append(" AND ");
            }
        });
        sb.delete(sb.length()-4, sb.length()-1);
        sb.append(")");

        return sb.toString();
    }
    public static String createQueryUPDATE(Object entity) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("UPDATE ").append(entity.getClass().getSimpleName().toLowerCase());
        buffer.append(" SET ");
        String[] fields = ObjectHelper.getFields(entity);
        String[] var3 = fields;
        int var4 = fields.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String field = var3[var5];
            buffer.append(field).append(" = ?, ");
        }

        buffer.setLength(buffer.length() - 2);
        buffer.append(" WHERE ").append(ObjectHelper.getIdAttributeName(entity.getClass())).append(" = ?");
        return buffer.toString();
    }
}

