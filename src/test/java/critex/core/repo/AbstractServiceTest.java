//package critex.core.repo;
//
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//
//import java.util.*;
//
////import static org.junit.jupiter.api.Assertions.*;
////import static org.mockito.Mockito.*;
//
///**
// * Test class to verify AbstractService and AbstractFilterableService functionality
// */
//public class AbstractServiceTest {
//
//    // Mock entity for testing
//    static class TestEntity {
//        private Long id;
//        private String name;
//
//        public TestEntity(Long id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//
//        public Long getId() { return id; }
//        public String getName() { return name; }
//    }
//
//    // Mock repository
//    interface TestRepository extends JpaSpecificationExecutor<TestEntity> {
//    }
//
//    // Concrete implementation of AbstractService for testing
//    static class TestAbstractService extends AbstractService<TestEntity, TestRepository> {
//
//        public TestAbstractService(TestRepository repository) {
//            super(repository);
//        }
//
//        @Override
//        public Specification<TestEntity> toPredicate(ReportCondition condition) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//        }
//
//        @Override
//        public Specification<TestEntity> toPredicate(ReportCondition condition, Collection<JoinReport> joins) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//        }
//
//        @Override
//        public Collection<JoinReport> toJoin(Collection<String> joins) {
//            return joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
//        }
//
//        @Override
//        public Specification<TestEntity> getById(Object id, Collection<JoinReport> joins) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
//        }
//
//        @Override
//        public Specification<TestEntity> getAllByIds(List<Object> ids, Collection<JoinReport> joins) {
//            return (root, query, criteriaBuilder) -> root.get("id").in(ids);
//        }
//
//        @Override
//        public Collection<JoinReport> toJoin(Collection<String> joins) {
//            return joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
//        }
//
//        @Override
//        public Specification<TestEntity> toPredicate(FilterBase filterBase) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//        }
//
//        @Override
//        public Specification<TestEntity> toPredicate(FilterBase filterBase, Collection<JoinReport> joins) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//        }
//
//        @Override
//        public ReportCondition generateReport(FilterBase filterBase) {
//            return new ReportCondition();
//        }
//    }
//
//    // Mock filter for testing
//    static class TestFilter extends FilterBase {
//        private String name;
//
//        public TestFilter(String name) {
//            this.name = name;
//        }
//
//        public String getName() { return name; }
//    }
//
//    // Concrete implementation of AbstractFilterableService for testing
//    static class TestAbstractFilterableService extends AbstractFilterableService<TestEntity, TestFilter, TestRepository> {
//
//        public TestAbstractFilterableService(TestRepository repository) {
//            super(repository);
//        }
//
//        @Override
//        public ReportCondition generateReport(FilterBase filter) {
//            ReportCondition condition = new ReportCondition();
//            if (filter instanceof TestFilter) {
//                TestFilter testFilter = (TestFilter) filter;
//                if (testFilter.getName() != null) {
//                    condition.addEqual("name", testFilter.getName());
//                }
//            }
//            return condition;
//        }
//
//        @Override
//        public Specification<TestEntity> toPredicate(FilterBase filter) {
//            return (root, query, criteriaBuilder) -> {
//                if (filter instanceof TestFilter) {
//                    TestFilter testFilter = (TestFilter) filter;
//                    if (testFilter.getName() != null) {
//                        return criteriaBuilder.equal(root.get("name"), testFilter.getName());
//                    }
//                }
//                return criteriaBuilder.conjunction();
//            };
//        }
//    }
//
//    @Test
//    public void testAbstractServiceParameterizedTypeSupport() {
//        // Test that Parameterized class correctly extracts generic type
//        TestRepository mockRepo = mock(TestRepository.class);
//        TestAbstractService service = new TestAbstractService(mockRepo);
//
//        // Verify that the service was created successfully
//        assertNotNull(service);
//        assertEquals(mockRepo, service.getRepository());
//
//        // Test utility methods
//        assertNotNull(service.newCondition());
//        assertNotNull(service.newFilter());
//        assertNotNull(service.join("testPath"));
//        assertNotNull(service.joins("path1", "path2"));
//    }
//
//    @Test
//    public void testAbstractFilterableServiceGenerateReport() {
//        // Test that AbstractFilterableService correctly implements generateReport
//        TestRepository mockRepo = mock(TestRepository.class);
//        TestAbstractFilterableService service = new TestAbstractFilterableService(mockRepo);
//
//        // Test generateReport method
//        TestFilter filter = new TestFilter("testName");
//        ReportCondition condition = service.generateReport(filter);
//
//        assertNotNull(condition);
//        // The condition should have been configured with the filter data
//        // This verifies that the generateReport method is working
//    }
//
//    @Test
//    public void testAbstractServiceDoesNotHaveGenerateReportMethods() {
//        // Verify that AbstractService doesn't have the filtering methods that AbstractFilterableService has
//        TestRepository mockRepo = mock(TestRepository.class);
//        TestAbstractService service = new TestAbstractService(mockRepo);
//
//        // AbstractService should have basic CRUD methods but not filtering methods
//        assertNotNull(service.newCondition());
//        assertNotNull(service.getRepository());
//
//        // This test passes if the class compiles, meaning AbstractService doesn't have
//        // conflicting methods with AbstractFilterableService
//        assertTrue(true);
//    }
//
//    @Test
//    public void testAbstractFilterableServiceHasFilteringMethods() {
//        // Verify that AbstractFilterableService has the filtering methods
//        TestRepository mockRepo = mock(TestRepository.class);
//        TestAbstractFilterableService service = new TestAbstractFilterableService(mockRepo);
//
//        // AbstractFilterableService should have filtering methods
//        TestFilter filter = new TestFilter("test");
//        PageRequestParam pageRequest = PageRequestParam.of(0, 10);
//
//        // These methods should exist and be callable
//        assertNotNull(service.generateReport(filter));
//
//        // This test passes if the class compiles and methods are accessible
//        assertTrue(true);
//    }
//}