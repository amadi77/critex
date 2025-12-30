package critex.core.repo;

import critex.core.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class to verify AbstractService and its functionality
 */
public class AbstractServiceTest {

    // Mock entity for testing
    static class TestEntity {
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    // Mock repository
    interface TestRepository extends JpaSpecificationExecutor<TestEntity> {
    }

    // Concrete implementation of AbstractService for testing
    static class TestAbstractService extends AbstractService<TestEntity, TestRepository> {

        public TestAbstractService(TestRepository repository) {
            super(repository);
        }

        public ReportCondition generateReport(Object filterBase) {
            return new ReportCondition();
        }
    }

    // Mock filter for testing
    static class TestFilter {
        private String name;

        public TestFilter(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    @Test
    public void testAbstractServiceParameterizedTypeSupport() {
        // Test that Parameterized class correctly extracts generic type
        TestRepository mockRepo = mock(TestRepository.class);
        TestAbstractService service = new TestAbstractService(mockRepo);

        // Verify that the service was created successfully
        assertNotNull(service);
        assertEquals(mockRepo, service.getRepository());

        // Test utility methods
        assertNotNull(service.newCondition());
        assertNotNull(service.newFilter());
        assertNotNull(service.join("testPath"));
        assertNotNull(service.joins("path1", "path2"));
    }

    @Test
    public void testAbstractServiceWithMockRepo() {
        TestRepository mockRepo = mock(TestRepository.class);
        TestAbstractService service = new TestAbstractService(mockRepo);

        ReportCondition condition = new ReportCondition();
        PageRequestParam pageRequest = PageRequestParam.of(0, 10);
        
        Page<TestEntity> mockPage = mock(Page.class);
        when(mockPage.toList()).thenReturn(Collections.emptyList());
        when(mockRepo.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(mockPage);

        service.findAll(condition, pageRequest);
        verify(mockRepo).findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class));
    }
}