package jpabook.jpashop.repository.order.query;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.dto.OrderItemQueryDto;
import jpabook.jpashop.dto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  public List<OrderQueryDto> findOrderQueryDtos() {
    List<OrderQueryDto> result = findOrders();

    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItmes(o.getOrderId());
      o.setOrderItems(orderItems);
    });

    return result;
  }

  private List<OrderItemQueryDto> findOrderItmes(Long orderId) {
    return em.createQuery(
        "select new jpabook.jpashop.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
            + " from OrderItem oi"
            + " join oi.item i"
            + " where oi.order.id = :orderId", OrderItemQueryDto.class)
        .setParameter("orderId", orderId)
        .getResultList();
  }


  public List<OrderQueryDto> findOrders() {
    return em.createQuery(
        "select new jpabook.jpashop.dto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o"
            + " join o.member m"
            + " join o.delivery d", OrderQueryDto.class)
        .getResultList();
  }
}
