package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import jpabook.jpashop.dto.OrderFlatDto;
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

    public List<OrderQueryDto> findAllByDto_optimization() {
      //쿼리 1
      List<OrderQueryDto> result = findOrders();

      List<Long> orderIds = toOrderIds(result);
      //쿼리 2
      Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);


      result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

      return result;
    }

  private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
    List<OrderItemQueryDto> orderItems = em.createQuery(
            "select new jpabook.jpashop.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                    + " from OrderItem oi"
                    + " join oi.item i"
                    + " where oi.order.id in :orderIds", OrderItemQueryDto.class)
            .setParameter("orderIds", orderIds)
            .getResultList();

    // 메모리 상에서 id와 dto 매핑(조인)
    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
            .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
    return orderItemMap;
  }

  private List<Long> toOrderIds(List<OrderQueryDto> result) {
    return result.stream()
            .map(o -> o.getOrderId())
            .collect(Collectors.toList());
  }

  public List<OrderFlatDto> findAllByDto_flat() {
    return em.createQuery(
            "select new " +
                    " jpabook.jpashop.dto.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                    " from Order o" +
                    " join o.member m" +
                    " join o.delivery d" +
                    " join o.orderItems oi " +
                    " join oi.item i", OrderFlatDto.class)
            .getResultList();
  }
}
