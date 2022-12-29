package com.example.shoppingmall.controller;

import com.example.shoppingmall.dto.CommentDTO;
import com.example.shoppingmall.dto.ItemDTO;
import com.example.shoppingmall.service.CommentService;
import com.example.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping("/item/save")
    public String saveForm(){
        return "itemPages/itemSave";
    }

    @PostMapping("/item/save")
    public String save(@ModelAttribute ItemDTO itemDTO) throws IOException {
        itemService.save(itemDTO);
        return "redirect:/item/main";
    }
    @GetMapping("/item/main")
    public String findAll(@PageableDefault(page = 1)Pageable pageable, Model model , @RequestParam(required = false , value = "sort", defaultValue = "id") String sort){
        Page<ItemDTO> itemDTOList = itemService.findAll(pageable, sort);
        model.addAttribute("itemList",itemDTOList);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < itemDTOList.getTotalPages()) ? startPage + blockLimit - 1 : itemDTOList.getTotalPages();
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("sort", sort);
        return "itemPages/itemMain";
    }

    @GetMapping("/item/")
    public String findById(@PageableDefault(page = 1) Pageable pageable, @RequestParam("itemId") Long itemId, Model model){
        System.out.println( "itemId = " + itemId);
//        Page<CommentDTO> commentDTOS = itemService.commentPaging(pageable);

        ItemDTO itemDTO = itemService.findById(itemId);
        model.addAttribute("item",itemDTO);
        Page<CommentDTO> commentDTOList = commentService.findAll(itemId,pageable);
        if (!commentDTOList.isEmpty()) {
            model.addAttribute("commentList", commentDTOList);
            int blockLimit = 3;
            //시작 페이지 값 계산
            int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
            //끝 페이지 값 계산(3, 6, 9, 12---)
            //endPage 값이 totalPage값보다 크다면 endPage값을 totalPage값으로 덮어쓴다.
            int endPage = ((startPage + blockLimit - 1) < commentDTOList.getTotalPages()) ? startPage + blockLimit - 1 : commentDTOList.getTotalPages();
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("itemId",itemId);
        }else {
            model.addAttribute("commentList", "empty");
        }
       return "itemPages/itemDetail";
    }

}
