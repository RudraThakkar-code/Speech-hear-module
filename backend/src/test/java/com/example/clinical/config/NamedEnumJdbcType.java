package com.example.clinical.config;

import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class NamedEnumJdbcType extends VarcharJdbcType {
    @Override
    public int getJdbcTypeCode() {
        return 6001; // SqlTypes.NAMED_ENUM
    }
}
