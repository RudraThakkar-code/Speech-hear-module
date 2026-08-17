package com.example.clinical.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;

/**
 * H2 test dialect that provides compatibility mappings for PostgreSQL-specific
 * named enums and enum arrays used by the production entities.
 *
 * Production PostgreSQL mappings remain unchanged; this dialect is used only
 * by the test profile.
 */
public class H2PostgresEnumDialect extends H2Dialect {

    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);

        var ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
        ddlTypeRegistry.addDescriptor(
                new DdlTypeImpl(SqlTypes.NAMED_ENUM, "varchar($l)", this)
        );

        // H2 accepts standard VARCHAR ARRAY columns. This replaces the PostgreSQL
        // named-enum array DDL shape (e.g. voice_quality[]) during tests only.
        ddlTypeRegistry.addDescriptor(
                new DdlTypeImpl(SqlTypes.ARRAY, "varchar array", this)
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
