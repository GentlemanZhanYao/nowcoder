package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //添加评论,其中涉及到两次DML操作，所以我们给它加上事务管理
    @Transactional(isolation =Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment==null)
            throw new IllegalArgumentException("评论不能为空！！");
        //转义处理
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //敏感词过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows=commentMapper.insertComment(comment);

        //如果为文章，则更新评论数量，否则不需要更新评论数量
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            //获取评论数量
            int count=commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }


        return rows;
    }
}
