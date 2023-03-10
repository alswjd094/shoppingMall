package com.example.shoppingmall.service;

import com.example.shoppingmall.dto.CartItemDTO;
import com.example.shoppingmall.dto.OrderDTO;
import com.example.shoppingmall.entity.*;
import com.example.shoppingmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderReadyRepository orderReadyRepository;

    public void save(OrderDTO orderDTO) {
        System.out.println("세이브넘어옴orderDTO = " + orderDTO);
        Optional<MemberEntity> memberEntity1 = memberRepository.findById(orderDTO.getMemberId());
        ItemEntity itemEntity1 = itemRepository.findById(orderDTO.getItemId()).get();
        itemEntity1.setItemSellCount(itemEntity1.getItemSellCount() + orderDTO.getOrderCount());
        System.out.println("구매수량"+orderDTO.getOrderCount());
        System.out.println("재고수량"+itemEntity1.getItemCount());
        itemEntity1.setItemCount(itemEntity1.getItemCount() - orderDTO.getOrderCount());
        itemRepository.save(itemEntity1);
        if (memberEntity1.isPresent()) {
            MemberEntity memberEntity2 = memberEntity1.get();
//            orderRepository.deleteByMemberEntity(memberEntity2);
            memberEntity2.setMemberAddress(orderDTO.getMemberAddress());
            memberEntity2.setDetailAddress(orderDTO.getDetailAddress());
            memberEntity2.setExtraAddress(orderDTO.getExtraAddress());
            memberEntity2.setPostcode(orderDTO.getPostcode());
            memberRepository.save(memberEntity2);

            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderStatus(orderDTO.getOrderStatus());
            orderEntity.setReview(orderDTO.getReview());
            orderEntity.setMemberEntity(memberEntity2);
            orderEntity.setOrderName(orderDTO.getOrderName());
            orderRepository.save(orderEntity);

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderEntity(orderEntity);
            itemEntity.setItemEntity(itemEntity1);
            itemEntity.setOrderCount(orderDTO.getOrderCount());
            itemEntity.setOrderName(orderDTO.getOrderName());
            itemEntity.setOrderPrice(orderDTO.getOrderPrice());
            orderItemRepository.save(itemEntity);
        }
    }

    @Transactional
    public void save2(JSONArray itemDTOList, String userId) throws JSONException {
        MemberEntity memberEntity = memberRepository.findByUserId(userId).get();
        memberEntity.setPostcode(itemDTOList.getJSONObject(0).getString("postcode"));
        memberEntity.setMemberAddress(itemDTOList.getJSONObject(0).getString("memberAddress"));
        memberEntity.setDetailAddress(itemDTOList.getJSONObject(0).getString("detailAddress"));
        memberEntity.setExtraAddress(itemDTOList.getJSONObject(0).getString("extraAddress"));
        memberRepository.save(memberEntity);

        for (int i = 0; i < itemDTOList.length(); i++) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            OrderEntity orderEntity = new OrderEntity();
            ItemEntity itemEntity = itemRepository.findByItemName(itemDTOList.getJSONObject(i).getString("itemName")).get();
            itemEntity.setItemSellCount(itemEntity.getItemSellCount() + itemDTOList.getJSONObject(i).getInt("cartCount"));
            itemEntity.setItemCount(itemEntity.getItemCount() - itemDTOList.getJSONObject(i).getInt("cartCount"));
            orderEntity.setOrderStatus("주문완료");
            orderEntity.setReview("리뷰작성");
            orderEntity.setMemberEntity(memberEntity);
            orderEntity.setOrderName(itemDTOList.getJSONObject(i).getString("itemName"));
            orderItemEntity.setOrderName(itemDTOList.getJSONObject(i).getString("itemName"));
            orderItemEntity.setOrderPrice(itemDTOList.getJSONObject(i).getInt("itemPrice"));
            orderItemEntity.setOrderCount(itemDTOList.getJSONObject(i).getInt("cartCount"));
            orderEntity=orderRepository.save(orderEntity);
            orderItemEntity.setOrderEntity(orderEntity);
            orderItemEntity.setItemEntity(itemEntity);
            orderItemRepository.save(orderItemEntity);
        }

        for (int i = 0; i < itemDTOList.length(); i++) {
            CartItemEntity cartItemEntity = cartItemRepository.findById(itemDTOList.getJSONObject(i).getLong("cartItemId")).get();
            cartItemRepository.delete(cartItemEntity);
        }
        orderReadyRepository.deleteByMemberEntity(memberEntity);

    }

    @Transactional
    public List<OrderDTO> findAll(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId).get();
        List<OrderEntity> orderEntityList = orderRepository.findByMemberEntity(memberEntity,Sort.by(Sort.Direction.DESC, "id"));
        System.out.println("orderEntityList = " + orderEntityList);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntityList) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(orderEntity.getId());
            orderDTO.setReview(orderEntity.getReview());
            orderDTO.setOrderStatus(orderEntity.getOrderStatus());
            orderDTO.setOrderName(orderEntity.getOrderName());
            orderDTOList.add(orderDTO);

        }
        return orderDTOList;
    }

    @Transactional
    public Page<OrderDTO> findListAll(Pageable pageable, String sort) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = pageable.getPageSize();
        Page<OrderEntity> orderEntityList = orderRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, sort)));
        Page<OrderDTO> orderDTOList = orderEntityList.map(OrderDTO::toOrderDTO);
        return orderDTOList;
    }

    @Transactional
    public List<CartItemDTO> findCartById(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId).get();
        CartEntity cartEntity = cartRepository.findByMemberEntity(memberEntity).get();
        List<CartItemEntity> cartEntityList = cartItemRepository.findByCartEntity(cartEntity);
        System.out.println("cartEntityList = " + cartEntityList);
        List<CartItemDTO> cartItemDTOList = new ArrayList<>();
        for (CartItemEntity cartItemEntity : cartEntityList) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cartItemEntity.getId());
            cartItemDTO.setItemName(cartItemEntity.getItemEntity().getItemName());
            cartItemDTO.setItemPrice(cartItemEntity.getItemEntity().getItemPrice());
            cartItemDTO.setItemCount(cartItemEntity.getItemEntity().getItemCount());
            cartItemDTO.setCartCount(cartItemEntity.getCartCount());
            cartItemDTO.setItemImage(cartItemEntity.getItemEntity().getItemFileEntityList().get(0).getStoredFileNameItem());
            cartItemDTOList.add(cartItemDTO);
        }
        return cartItemDTOList;
    }

    public void update(Long id, String status) {
        OrderEntity orderEntity=orderRepository.findById(id).get();
        orderEntity.setOrderStatus(status);
        orderRepository.save(orderEntity);
    }

    public String checkOrder(String userId, JSONArray itemDTOList) throws JSONException {
        MemberEntity memberEntity = memberRepository.findByUserId(userId).get();
        if (orderReadyRepository.findByMemberEntity(memberEntity).isEmpty()) {
            for (int i = 0; i < itemDTOList.length(); i++) {
                OrderReadyEntity orderReadyEntity = new OrderReadyEntity();
                orderReadyEntity.setMemberEntity(memberEntity);
                orderReadyEntity.setOrderName(itemDTOList.getJSONObject(i).getString("itemName"));
                orderReadyEntity.setOrderPrice(itemDTOList.getJSONObject(i).getInt("itemPrice"));
                orderReadyEntity.setCartCount(itemDTOList.getJSONObject(i).getInt("cartCount"));
                orderReadyEntity.setItemPriceTotal(itemDTOList.getJSONObject(i).getInt("itemPriceTotal"));
                orderReadyEntity.setItemImage(itemDTOList.getJSONObject(i).getString("itemImage"));
                orderReadyEntity.setCartItemId(itemDTOList.getJSONObject(i).getLong("cartItemId"));
                orderReadyRepository.save(orderReadyEntity);
            }
        } else {
            return "no";
        }

        return "ok";
    }

    public List<CartItemDTO> findByOrderReady(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId).get();
        List<OrderReadyEntity> orderReadyEntityList = orderReadyRepository.findByMemberEntity(memberEntity);
        List<CartItemDTO> cartItemDTOList = new ArrayList<>();
        for (OrderReadyEntity orderReadyEntity : orderReadyEntityList) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setItemName(orderReadyEntity.getOrderName());
            cartItemDTO.setItemPrice(orderReadyEntity.getOrderPrice());
            cartItemDTO.setCartCount(orderReadyEntity.getCartCount());
            cartItemDTO.setItemPriceTotal(orderReadyEntity.getItemPriceTotal());
            cartItemDTO.setItemImage(orderReadyEntity.getItemImage());
            cartItemDTO.setId(orderReadyEntity.getCartItemId());
            cartItemDTOList.add(cartItemDTO);
        }
        return cartItemDTOList;
    }

    @Transactional
    public String cancel(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).get();
        String userId = memberEntity.getUserId();
        System.out.println(memberEntity);
        System.out.println("삭제전");
        orderReadyRepository.deleteByMemberEntity(memberEntity);
        return userId;
    }
}

