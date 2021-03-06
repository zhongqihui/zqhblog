package com.zqh.blog.controller;

import com.zqh.blog.entity.ArticleInfo;
import com.zqh.blog.entity.Category;
import com.zqh.blog.listener.MySessionContext;
import com.zqh.blog.service.ArticleService;
import com.zqh.blog.service.CategoryService;
import com.zqh.blog.vo.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zqh
 * email：zqhfsf@gmail.com
 * date: 2018/1/21 0021 下午 9:13
 * description: 博客前台controller，用于展示
 **/
@Controller
public class IndexController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 博客首页展示
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping({"index.html", "index", "index.jsp"})
    public String toHomePage(Model model, HttpServletRequest request) {
        Page<ArticleInfo> page = articleService.getPage(request);
        page.setUrl("index");

        List<Category> categoryList = categoryService.selectList(new HashMap());
        model.addAttribute("page", page)
                .addAttribute("categoryList", categoryList);
        return "fts/index";
    }

    @RequestMapping("favicon")
    public String favicon() {
        return null;
    }

    /**
     * 根据文章id，展示该文章
     *
     * @param aid
     * @param model
     * @return
     */
    @RequestMapping("{aid}")
    public String getArticle(@PathVariable("aid") String aid, Model model, HttpServletRequest request) {
        Integer sn = null;
        try {
            sn = Integer.parseInt(aid);
        } catch (Exception e) {
            return ret404Page();
        }

        MySessionContext.getInstance().addArticleSn2Session(request.getSession(), aid);

        ArticleInfo info = articleService.selectById(sn);
        if (info == null) {
            return ret404Page();
        }

        ArticleInfo next = articleService.getNext(sn);
        ArticleInfo prev = articleService.getPrev(sn);
        Category c = categoryService.selectById(info.getCategorySn());
        model.addAttribute("article", info)
                .addAttribute("next", next)
                .addAttribute("prev", prev)
                .addAttribute("category", c);
        return "fts/show_article";
    }


    /**
     * 归档页面的展示
     *
     * @param model
     * @return
     */
    @GetMapping("archive")
    public String toArchive(Model model) {
        Map<String, List<ArticleInfo>> map = articleService.getYearMap();
        int count = articleService.getCount(new HashMap());
        model.addAttribute("map", map).addAttribute("count", count);
        return "fts/archive";
    }

    /**
     * 博客分类的展示
     *
     * @param model
     * @return
     */
    @GetMapping("categories")
    public String categoryList(Model model) {
        List<Category> categoryList = categoryService.getCategoryList();
        model.addAttribute("categoryList", categoryList);

        return "fts/category_list";
    }


    /**
     * 博客分类下的文章列表
     *
     * @param cid
     * @param model
     * @return
     */
    @RequestMapping("categories/{cid}")
    public String getCategory(@PathVariable("cid") String cid, Model model) {
        Integer sn = null;
        try {
            sn = Integer.parseInt(cid);
        } catch (Exception e) {
            return ret404Page();
        }

        Category category = categoryService.getCategoryBySn(sn);
        model.addAttribute("category", category);
        return "fts/category";
    }


}
