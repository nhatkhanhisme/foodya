package com.foodya.foodya_backend;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Enforces the module rule from README.md: cross-module calls go through the
 * owning module's *Service, never its *Repository/entity directly. Each rule
 * below caught a real violation during the package-by-feature restructure
 * (identity.persistence.UserRepository was being injected directly from both
 * the old user and order modules) — this test exists so that regression
 * cannot land silently again.
 */
@AnalyzeClasses(packages = "com.foodya.foodya_backend", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  @ArchTest
  static final ArchRule only_identity_may_access_its_own_persistence =
      noClasses().that().resideOutsideOfPackage("..identity..")
          .should().dependOnClassesThat().resideInAPackage("..identity.persistence..");

  @ArchTest
  static final ArchRule only_catalog_may_access_its_own_persistence =
      noClasses().that().resideOutsideOfPackage("..catalog..")
          .should().dependOnClassesThat().resideInAPackage("..catalog.persistence..");

  @ArchTest
  static final ArchRule only_ordering_may_access_its_own_persistence =
      noClasses().that().resideOutsideOfPackage("..ordering..")
          .should().dependOnClassesThat().resideInAPackage("..ordering.persistence..");
}
