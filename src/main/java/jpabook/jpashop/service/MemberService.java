package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)         // 2번째
//@AllArgsConstructor             //lombok
@RequiredArgsConstructor          //lombok : final이 있는 변수만 생성자를 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional      // 1번째
    public Long join(Member member) {
        validateDuplicateMember(member);        //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //회원 전체 조회
//    @Transactional(readOnly = true)             //데이터를 읽어올때는 속도 최적화 가능, 쓰기 시에느 리드온리를 넣으면 쿼링이 안먹는다,
    public List<Member> findMember() {
        return memberRepository.findAll();
    }

    //회원 조회
//    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    /**
     * 변경감지 예제
     * @Transactional AOP가 끝나는 시점에서 JPA가 영속성컨텍스트 커밋 -> DB 커밋
     * @param id
     * @param name
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
