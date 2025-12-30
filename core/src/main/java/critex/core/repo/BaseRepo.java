package critex.core.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

public abstract class BaseRepo {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

    public <T> List<T> queryHql(Query query, Map<String, Object> param, Class<T> clazz) {
        param.forEach(query::setParameter);
        return (List<T>) query.getResultList();
    }

    public <T> List<T> querySql(org.hibernate.query.Query query, Map<String, Object> param, Class<T> clazz) {
        param.forEach(query::setParameter);
        return (List<T>) query.getResultList();
    }
}
