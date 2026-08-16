package com.example.clinical.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;

/**
 * H2 test dialect that treats PostgreSQL named enums as VARCHAR.
 *
 * Production PostgreSQL mappings intentionally use NAMED_ENUM. H2 does not
 * provide a DDL/JDBC mapping for that Hibernate SQL type code, so tests need
 * a compatibility mapping without changing the production entities.
 */
public class H2PostgresEnumDialect extends H2Dialect {

    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);

        var ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
        ddlTypeRegistry.addDescriptor(
                new DdlTypeImpl(SqlTypes.NAMED_ENUM, "varchar($l)", this)
        );
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);

        // Map Hibernate's NAMED_ENUM JDBC type code to ordinary VARCHAR for H2 tests.
        typeContributions.getTypeConfiguration()
                .getJdbcTypeRegistry()
                .addDescriptor(SqlTypes.NAMED_ENUM, VarcharJdbcType.INSTANCE);
    }
}
