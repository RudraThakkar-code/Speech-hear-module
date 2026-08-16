package com.example.clinical.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class TestEnumTypeContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, org.hibernate.service.ServiceRegistry serviceRegistry) {
        // Force mapping of NAMED_ENUM (6001) to standard VARCHAR logic in H2
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().addDescriptor(SqlTypes.NAMED_ENUM, VarcharJdbcType.INSTANCE);
    }
}
