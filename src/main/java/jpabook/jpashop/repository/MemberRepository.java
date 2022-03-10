package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext
////    @Autowired      //Spring boot JPA의 경우 Autowired 어노테이션으로 대체 가능
//    private EntityManager em;
//
//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

    private final EntityManager em;         //Lombok이 엔티티매니저도 생성자를 만들고 빈등록을 해준다.

//    @PersistenceUnit              //엔티티매니저 팩토리를 직접 만들고 싶을 땨
//    private EntityManagerFactory emp;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) //jpql => Entity를 대상으로 쿼링한다.
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)      //:name => 파라메터 바인딩
                .setParameter("name", name)
                .getResultList();
    }
}
