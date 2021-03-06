package com.zqh.blog.service;

import com.zqh.blog.entity.ArticleInfo;
import com.zqh.blog.entity.Category;
import com.zqh.blog.mapper.ArticleInfoMapper;
import com.zqh.blog.mapper.CategoryMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* author: zqh
* email：zqhfsf@gmail.com
* date: 2018/1/25 0025 下午 10:05
* description: 博客分类service层
**/
@Service
public class CategoryService extends BaseService<Category,Integer> {

    @Autowired
    ArticleInfoMapper articleInfoMapper;

    CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper baseMapper) {
        super(baseMapper);
        this.categoryMapper = baseMapper;
    }

    /**
     * 获取所有的分类和分类下文章的篇数
     * @return
     */
    public List<Category> getCategoryList() {
        Map map = new HashMap();
        List<Category> categories = categoryMapper.selectList(map);
        if(categories != null && !categories.isEmpty()) {
            for (Category c : categories) {
                map.clear();
                map.put("categorySn", c.getSn());
                int i = articleInfoMapper.selectCount(map);
                c.setArtSize(i);
            }
        }

        return categories;
    }

    /**
     * 获取某分类和该分类下的所有文章
     * @param sn
     * @return
     */
    public Category getCategoryBySn(Integer sn) {
        Category category = categoryMapper.selectById(sn);
        if(category != null) {
            Map<String, Integer> map = new HashMap<>();
            map.put("categorySn", category.getSn());
            List<ArticleInfo> infos = articleInfoMapper.selectList(map);
            category.setArts(infos);
        }

        return category;
    }


    /**
     * 删除博客分类的同时，将文章的CategorySn修改成0，暂无分类
     * @return
     */
    public String delCategory(String sn) {
        int id;
        try{
            id = Integer.parseInt(sn);
        }catch (Exception e){
            return "fail";
        }

        int delCount = categoryMapper.deleteById(id);
        if(delCount <= 0) {
            return "fail";
        }

        Map<String, String> map = new HashMap<>();
        map.put("category", sn);
        List<ArticleInfo> list = articleInfoMapper.selectAllNoContent(map);
        StringBuffer sb = new StringBuffer();
        for(ArticleInfo a:list) {
            sb.append(a.getSn()).append(",");
        }

        String aSns = sb.toString();
        if(StringUtils.isNotEmpty(aSns) && aSns.endsWith(",")) {
            aSns = aSns.substring(0, aSns.length() - 1);
            articleInfoMapper.updateBatchCategorySn(aSns);
        }

        return "success";
    }
}
